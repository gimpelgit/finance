package infrastructure.db.function;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import core.domain.Transaction;
import core.domain.TransactionType;

public class ResultSetToTransaction implements ResultSetToT<Transaction> {

	@Override
	public Transaction convert(ResultSet rs) throws SQLException {
		Transaction transaction = null;
		if (rs.next()) {
			int id = rs.getInt("id");
			int userId = rs.getInt("user_id");
			int categoryId = rs.getInt("category_id");
			int moneyAccountId = rs.getInt("money_account_id");
			BigDecimal amount = new BigDecimal(rs.getString("amount"));
			Date date = rs.getDate("date");
			String description = rs.getString("description");
			String typeStr = rs.getString("type");
			TransactionType type = TransactionType.valueOf(typeStr);
			transaction = new Transaction(id, userId, categoryId, moneyAccountId, amount, date, description, type);
		}
		return transaction;
	}
	
}
