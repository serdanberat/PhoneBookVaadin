package org.example.Services;

import org.example.Dao.PersonInformationDao;
import org.example.Dao.PersonInformationDaoImpl;
import org.example.DbHandler;
import org.example.Entity.PersonInformations;
import org.example.Model.DbStatements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCrudService {

	public static AtomicInteger atomicIntegerId = new AtomicInteger(0);

	private final Set<String> setCopyMap = new HashSet<>();

	private final PersonInformationDao personInformationDao = new PersonInformationDaoImpl();

	private final DbHandler dbHandler = new DbHandler();

	public MyCrudService() {
	}

	public boolean isPhoneUnique(String v) {
		return !setCopyMap.contains(v);
	}

	public void copyData(ConcurrentHashMap<Integer, PersonInformations> data) {
		Set set = data.entrySet();
		for (Object o : set) {
			Map.Entry me = (Map.Entry) o;
			PersonInformations phone = (PersonInformations) me.getValue();
			setCopyMap.add(phone.getPhone());
		}
	}

	public void setMaxAtomicID(AtomicInteger atomicIntegerId) {

		try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(DbStatements.getFetchMaxId())) {

			ResultSet resultSet = stmt.executeQuery();

			resultSet.next();
			atomicIntegerId.set(resultSet.getInt("max_id"));

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	public void createTestData() {

		int id = 20;
		int i = 0;

		try {

			while (i < 10000000) {
				PreparedStatement preparedStatementAdd = DbHandler.getConnection().prepareStatement(
						"insert into phonebook.person_info (id,name,street,city,country,phone,mail) values (" + id + ",'Berat','Str1','C1','Co1'," + id +
						",'berat@gmail.com');");
				id++;
				PreparedStatement preparedStatementAdd2 = DbHandler.getConnection().prepareStatement(
						"insert into phonebook.person_info (id,name,street,city,country,phone,mail) values (" + id + ",'Test','Str2','C2','Co2'," + id + ",'test');");
				id++;

				preparedStatementAdd.executeUpdate();
				preparedStatementAdd2.executeUpdate();

				i++;
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public void addPerson(AtomicInteger atomicIntegerId, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data) {

		personInformationDao.addPerson(atomicIntegerId, toSave, data);

	}

	public void deletePerson(PersonInformations toDelete, ConcurrentHashMap<Integer, PersonInformations> data) {

		personInformationDao.deletePerson(toDelete, data);


	}
}
