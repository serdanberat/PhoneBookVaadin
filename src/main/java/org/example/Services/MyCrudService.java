package org.example.Services;

import com.vaadin.flow.component.notification.Notification;
import org.example.DbHandler;
import org.example.Entity.PersonInformations;
import org.example.Model.VaadinStatements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCrudService {

    DbHandler dbHandler = new DbHandler();

    public MyCrudService() {
    }

    public boolean isPhoneUnique(ConcurrentHashMap<Integer, PersonInformations> dataProvider, String v) {
        return !dataProvider.values().stream().anyMatch(x -> x.getPhone().equals(v));
    }

    public void fetchData(ConcurrentHashMap<Integer, PersonInformations> data) {

        try ( PreparedStatement stmt = dbHandler.getConnection().prepareStatement(VaadinStatements.getFetchAll())){

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), new PersonInformations(resultSet.getInt("id"),resultSet.getString("name"), resultSet.getString("street"), resultSet.getString("city"), resultSet.getString("country"), resultSet.getString("phone"), resultSet.getString("mail")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void addPerson(AtomicInteger atomicIntegerId, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data){

        String oldPhoneNumber = null;
        
        try( PreparedStatement prepareStatementCheck =  dbHandler.getConnection().prepareStatement(VaadinStatements.getCheckQuery());
             PreparedStatement prepareStatementAdd =  dbHandler.getConnection().prepareStatement(VaadinStatements.getAddQuery());
             PreparedStatement prepareStatementFetchOldPhone =  dbHandler.getConnection().prepareStatement(VaadinStatements.getFetchOldPhone())) {

            prepareStatementCheck.setInt(1,toSave.getId());
            prepareStatementFetchOldPhone.setInt(1,toSave.getId());

            Integer myId = atomicIntegerId.incrementAndGet();

            ResultSet recordCheck = prepareStatementCheck.executeQuery();
            ResultSet recordOldPhone = prepareStatementFetchOldPhone.executeQuery();

            prepareStatementAdd.setInt   (1, myId);
            prepareStatementAdd.setString   (2, toSave.getName());
            prepareStatementAdd.setString   (3, toSave.getStreet());
            prepareStatementAdd.setString   (4, toSave.getCity());
            prepareStatementAdd.setString   (5, toSave.getCountry());
            prepareStatementAdd.setString   (6, toSave.getPhone());
            prepareStatementAdd.setString   (7, toSave.getEmail());

            recordCheck.next();
            recordCheck.getInt("count");

            while(recordOldPhone.next()) {
                oldPhoneNumber = recordOldPhone.getString("phone");
            }

            if (recordCheck.getInt("count")==0){
                prepareStatementAdd.executeUpdate();
                toSave.setId(atomicIntegerId.get());
                data.put(toSave.getId(),toSave);
            }
            else
                updatePerson(dbHandler,toSave,data, oldPhoneNumber);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void updatePerson(DbHandler dbHandler, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data, String oldPhone){

        try(PreparedStatement preparedStmtUpdate = dbHandler.getConnection().prepareStatement(VaadinStatements.getUpdateQuery());
            PreparedStatement preparedStmtUpdatePhone = dbHandler.getConnection().prepareStatement(VaadinStatements.getUpdateNoPhoneQuery());
            PreparedStatement preparedStmtCheckPhone = dbHandler.getConnection().prepareStatement(VaadinStatements.getCheckByPhone())) {

            preparedStmtUpdate.setString   (1, toSave.getName());
            preparedStmtUpdate.setString   (2, toSave.getStreet());
            preparedStmtUpdate.setString   (3, toSave.getCity());
            preparedStmtUpdate.setString   (4, toSave.getCountry());
            preparedStmtUpdate.setString   (5, toSave.getEmail());
            preparedStmtUpdate.setString   (6, toSave.getPhone());
            preparedStmtUpdate.setInt      (7, toSave.getId());

            preparedStmtUpdatePhone.setString   (1, toSave.getName());
            preparedStmtUpdatePhone.setString   (2, toSave.getStreet());
            preparedStmtUpdatePhone.setString   (3, toSave.getCity());
            preparedStmtUpdatePhone.setString   (4, toSave.getCountry());
            preparedStmtUpdatePhone.setString   (5, toSave.getEmail());
            preparedStmtUpdatePhone.setInt      (6, toSave.getId());

            preparedStmtCheckPhone.setString   (1, toSave.getPhone());
            preparedStmtCheckPhone.setString   (1, toSave.getPhone());

            ResultSet record = preparedStmtCheckPhone.executeQuery();
            record.next();

            if (record.getInt("count")>0){
                Notification.show("Phone Number Already Exists,did not updated",5280, Notification.Position.TOP_CENTER);
                preparedStmtUpdatePhone.executeUpdate();

                data.put(toSave.getId(),new PersonInformations(toSave.getId(),toSave.getName(),toSave.getStreet(), toSave.getCity(), toSave.getCountry(), oldPhone,toSave.getEmail()));
            }
            else{
                preparedStmtUpdate.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void deletePerson(PersonInformations toDelete, ConcurrentHashMap<Integer, PersonInformations> data){

        try(PreparedStatement prepareStatementDelete = dbHandler.getConnection().prepareStatement(VaadinStatements.getDeleteQuery())) {

            prepareStatementDelete.setInt(1, toDelete.getId());

            prepareStatementDelete.executeUpdate();
            data.remove(toDelete.getId());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void setMaxAtomicID(AtomicInteger atomicIntegerId){

        try ( PreparedStatement stmt = dbHandler.getConnection().prepareStatement(VaadinStatements.getFetchMaxId())){

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();
            atomicIntegerId.set(resultSet.getInt("max_id"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void createTestData() {

        int id =20;
        int i=0;

        try {

            while(i<10000000) {
                PreparedStatement preparedStatementAdd = dbHandler.getConnection().prepareStatement("insert into phonebook.person_info (id,name,street,city,country,phone,mail) values ("+id+",'Berat','Str1','C1','Co1',"+id+",'berat@gmail.com');");
                id++;
                PreparedStatement preparedStatementAdd2 = dbHandler.getConnection().prepareStatement("insert into phonebook.person_info (id,name,street,city,country,phone,mail) values ("+id+",'Test','Str2','C2','Co2',"+id+",'test');");
                id++;

                preparedStatementAdd.executeUpdate();
                preparedStatementAdd2.executeUpdate();

                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
