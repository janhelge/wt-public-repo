package no.webtech.serialize.coreapi;

import java.util.Map;

public interface RowMapper<T> {
	T mapRow(Map<String,String> row);
}
