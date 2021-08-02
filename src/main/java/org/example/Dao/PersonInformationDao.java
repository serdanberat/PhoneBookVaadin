package org.example.Dao;

import org.example.DbHandler;
import org.example.Entity.PersonInformations;

import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface PersonInformationDao {

	public void fetchData(ConcurrentHashMap<Integer, PersonInformations> data);

	public void addPerson(AtomicInteger atomicIntegerId, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data);

	public void updatePerson(DbHandler dbHandler, PersonInformations toSave, ConcurrentHashMap<Integer, PersonInformations> data, String oldPhone);

	public void deletePerson(PersonInformations toDelete, ConcurrentHashMap<Integer, PersonInformations> data);

}
