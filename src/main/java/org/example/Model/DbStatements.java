package org.example.Model;

public class DbStatements {


	private static final String fetchAll = "select * from phonebook.person_info2;";

	private static final String checkByPhone = "select count(*) as count from phonebook.person_info2 where phone=?;";

	private static final String checkById = "select count(*) as count from phonebook.person_info2 where id=?;";

	private static final String fetchOldPhone = "select phone from phonebook.person_info2 where id=?;";

	private static final String updateQuery = "update phonebook.person_info2 set name=?, street=?, city=?, country=?, mail=?,phone=? where id=?;";

	private static final String updateNoPhoneQuery = "update phonebook.person_info2 set name=?, street=?, city=?, country=?, mail=? where id=?;";

	private static final String deleteQuery = "delete from phonebook.person_info2 where id=?;";

	private static final String addQuery = "insert into phonebook.person_info2 (id,name,street,city,country,phone,mail) values (?,?,?,?,?,?,?);";

	private static final String fetchMaxId = "select IFNULL(max(id),0)  as max_id from phonebook.person_info2;";

	public static String getFetchAll() {
		return fetchAll;
	}

	public static String getFetchMaxId() {
		return fetchMaxId;
	}

	public static String getCheckById() {
		return checkById;
	}

	public static String getFetchOldPhone() {
		return fetchOldPhone;
	}

	public static String getUpdateQuery() {
		return updateQuery;
	}

	public static String getUpdateNoPhoneQuery() {
		return updateNoPhoneQuery;
	}

	public static String getDeleteQuery() {
		return deleteQuery;
	}

	public static String getAddQuery() {
		return addQuery;
	}

	public static String getCheckByPhone() {
		return checkByPhone;
	}

}
