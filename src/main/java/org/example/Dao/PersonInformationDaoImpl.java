package org.example.Dao;

import com.vaadin.flow.component.notification.Notification;
import org.example.DbHandler;
import org.example.Entity.PersonInformations;
import org.example.Model.DbStatements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PersonInformationDaoImpl implements PersonInformationDao {

	static DbHandler dbHandler = new DbHandler();

	private static ConcurrentHashMap<Integer, PersonInformations> data = new ConcurrentHashMap<>();

	public ConcurrentHashMap<Integer, PersonInformations> fetchData() {

		try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(DbStatements.getFetchAll())) {

			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				data.put(resultSet.getInt("id"),
						 new PersonInformations(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("street"), resultSet.getString("city"),
												resultSet.getString("country"), resultSet.getString("phone"), resultSet.getString("mail")));
			}

			return data;

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return null;

	}

	public void addPerson(AtomicInteger atomicIntegerId, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data) {

		String oldPhoneNumber = null;

		try (PreparedStatement prepareStatementCheck = dbHandler.getConnection().prepareStatement(DbStatements.getCheckById());
			 PreparedStatement prepareStatementAdd = dbHandler.getConnection().prepareStatement(DbStatements.getAddQuery());
			 PreparedStatement prepareStatementFetchOldPhone = dbHandler.getConnection().prepareStatement(DbStatements.getFetchOldPhone())) {

			prepareStatementCheck.setInt(1, toSave.getId());
			prepareStatementFetchOldPhone.setInt(1, toSave.getId());

			Integer myId = atomicIntegerId.incrementAndGet();

			ResultSet recordCheck = prepareStatementCheck.executeQuery();
			ResultSet recordOldPhone = prepareStatementFetchOldPhone.executeQuery();

			prepareStatementAdd.setInt(1, myId);
			prepareStatementAdd.setString(2, toSave.getName());
			prepareStatementAdd.setString(3, toSave.getStreet());
			prepareStatementAdd.setString(4, toSave.getCity());
			prepareStatementAdd.setString(5, toSave.getCountry());
			prepareStatementAdd.setString(6, toSave.getPhone());
			prepareStatementAdd.setString(7, toSave.getEmail());

			recordCheck.next();
			recordCheck.getInt("count");

			while (recordOldPhone.next()) {
				oldPhoneNumber = recordOldPhone.getString("phone");
			}

			if (recordCheck.getInt("count") == 0) {
				prepareStatementAdd.executeUpdate();
				toSave.setId(atomicIntegerId.get());
				data.put(toSave.getId(), toSave);
			} else {
				updatePerson(dbHandler, toSave, data, oldPhoneNumber);
			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public void updatePerson(DbHandler dbHandler, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data, String oldPhone) {

		try (PreparedStatement preparedStmtUpdate = dbHandler.getConnection().prepareStatement(DbStatements.getUpdateQuery());
			 PreparedStatement preparedStmtUpdatePhone = dbHandler.getConnection().prepareStatement(DbStatements.getUpdateNoPhoneQuery());
			 PreparedStatement preparedStmtCheckPhone = dbHandler.getConnection().prepareStatement(DbStatements.getCheckByPhone())) {

			preparedStmtUpdate.setString(1, toSave.getName());
			preparedStmtUpdate.setString(2, toSave.getStreet());
			preparedStmtUpdate.setString(3, toSave.getCity());
			preparedStmtUpdate.setString(4, toSave.getCountry());
			preparedStmtUpdate.setString(5, toSave.getEmail());
			preparedStmtUpdate.setString(6, toSave.getPhone());
			preparedStmtUpdate.setInt(7, toSave.getId());

			preparedStmtUpdatePhone.setString(1, toSave.getName());
			preparedStmtUpdatePhone.setString(2, toSave.getStreet());
			preparedStmtUpdatePhone.setString(3, toSave.getCity());
			preparedStmtUpdatePhone.setString(4, toSave.getCountry());
			preparedStmtUpdatePhone.setString(5, toSave.getEmail());
			preparedStmtUpdatePhone.setInt(6, toSave.getId());

			preparedStmtCheckPhone.setString(1, toSave.getPhone());
			preparedStmtCheckPhone.setString(1, toSave.getPhone());

			ResultSet record = preparedStmtCheckPhone.executeQuery();
			record.next();

			if (record.getInt("count") > 0) {
				Notification.show("Phone Number Already Exists,did not updated", 5280, Notification.Position.TOP_CENTER);
				preparedStmtUpdatePhone.executeUpdate();

				data.put(toSave.getId(), new PersonInformations(toSave.getId(), toSave.getName(), toSave.getStreet(), toSave.getCity(), toSave.getCountry(),
																oldPhone,
																toSave.getEmail()));

			} else {
				synchronized (this) {
					preparedStmtUpdate.executeUpdate();
				}
			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public void deletePerson(PersonInformations toDelete, ConcurrentHashMap<Integer, PersonInformations> data) {

		try (PreparedStatement prepareStatementDelete = dbHandler.getConnection().prepareStatement(DbStatements.getDeleteQuery())) {

			prepareStatementDelete.setInt(1, toDelete.getId());

			prepareStatementDelete.executeUpdate();
			data.remove(toDelete.getId());

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}


}
