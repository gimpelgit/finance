package infrastructure.db.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetToListT<T> implements ResultSetToT<List<T>> {
	private ResultSetToT<T> converter;
	
	public ResultSetToListT(ResultSetToT<T> converter) {
		this.converter = converter;
	}

	@Override
	public List<T> convert(ResultSet rs) throws SQLException {
		List<T> list = new ArrayList<>();
		T element;
		do {
			element = converter.convert(rs);
			list.add(element);
		} while (element != null);
		list.remove(list.size() - 1);
		return list;	
	}
}
