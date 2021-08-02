package org.example.Dto;

import com.mysql.cj.protocol.Resultset;
import org.example.Dao.PersonInformationDao;
import org.example.Dao.PersonInformationDaoImpl;
import org.example.Entity.PersonInformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class PersonInformationsDto {

	PersonInformationDao personInformationsDao = new PersonInformationDaoImpl();

	private static ConcurrentHashMap<Integer, PersonInformations> data = new ConcurrentHashMap<>();


	public ConcurrentHashMap<Integer, PersonInformations> getData() {
		return data;
	}

	public void setData() {
		personInformationsDao.fetchData(data);
	}

}
