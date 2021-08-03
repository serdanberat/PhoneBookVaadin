package org.example.Dto;

import org.example.Dao.PersonInformationDao;
import org.example.Dao.PersonInformationDaoImpl;
import org.example.Entity.PersonInformations;

import java.util.concurrent.ConcurrentHashMap;

public class PersonInformationsDto {

	PersonInformationDao personInformationsDao = new PersonInformationDaoImpl();

	public ConcurrentHashMap<Integer, PersonInformations> getData() {
		return personInformationsDao.fetchData();
	}

}
