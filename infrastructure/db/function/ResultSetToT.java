package infrastructure.db.function;

import java.sql.ResultSet;
import java.sql.SQLException;


@FunctionalInterface
public interface ResultSetToT<T> {
	T convert(ResultSet rs) throws SQLException;
}
