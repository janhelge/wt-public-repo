package no.webtech.serialize.coreapi;

import java.util.List;

public interface Template<C extends SessionController> {

	String queryForString(C sessionController, String query);

	List<String> queryForListOfStrings(C sessionController, String query);

	<T> List<T> queryForList(C sessionController, RowMapper<T> rowMapper, String query);

	<T> T queryForObject(C sessionController, RowMapper<T> rowMapper, String query);

}