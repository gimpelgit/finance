package infrastructure.db.function;

import java.sql.ResultSet;
import java.sql.SQLException;

import core.domain.Category;
import core.domain.TransactionType;

public class ResultSetToCategory implements ResultSetToT<Category> {

	@Override
	public Category convert(ResultSet rs) throws SQLException {
		Category category = null;
		if (rs.next()) {
			int id = rs.getInt("id");
			int userId = rs.getInt("user_id");
			String name = rs.getString("name");
			String typeStr = rs.getString("type");
			TransactionType type = TransactionType.valueOf(typeStr);
			category = new Category(id, userId, name, type);
		}
		return category;
	}
}
