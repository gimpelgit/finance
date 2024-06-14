package infrastructure.db.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.domain.Transaction;
import core.dto.transaction.CreateTransactionDto;
import core.dto.transaction.UpdateTransactionDto;
import core.repository.TransactionRepository;
import infrastructure.db.function.ResultSetToListT;
import infrastructure.db.function.ResultSetToTransaction;
import infrastructure.db.helper.SqlHelper;

public class SqlTransactionRepository implements TransactionRepository {
	private static List<Transaction> transactions = new ArrayList<>();
	private static final ResultSetToTransaction converter = new ResultSetToTransaction();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SqlHelper dbHelper = new SqlHelper();

	@Override
	public int createTransaction(CreateTransactionDto createTransactionDto) {
		String sql = "INSERT INTO transactions " +
			"(user_id, category_id, money_account_id, amount, date, description, type) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";
		return dbHelper.executeInsert(
			sql,
			createTransactionDto.userId,
			createTransactionDto.categoryId,
			createTransactionDto.moneyAccountId,
			createTransactionDto.amount,
			dateFormat.format(createTransactionDto.date),
			createTransactionDto.description,
			createTransactionDto.type.toString()
		);
	}

	@Override
	public boolean updateTransaction(UpdateTransactionDto updateTransactionDto) {
		String sql = "UPDATE transactions SET category_id=?, amount=?, date=?, " + 
			"description=?, type=? WHERE id = ?";
		return dbHelper.executeUpdate(
			sql,
			updateTransactionDto.categoryId,
			updateTransactionDto.amount,
			dateFormat.format(updateTransactionDto.date),
			updateTransactionDto.description,
			updateTransactionDto.type.toString(),
			updateTransactionDto.id
		);
	}

	@Override
	public boolean deleteTransactionById(int id) {
		String sql = "DELETE FROM transactions WHERE id = ?";
		return dbHelper.executeDelete(sql, id);
	}

	@Override
	public Transaction findTransactionById(int id) {
		String sql = "SELECT * FROM transactions WHERE id = ?";
		var transaction = dbHelper.executeSelect(sql, converter, id);
		return transaction;
	}

	@Override
	public List<Transaction> getAllTransactionsByUserId(int userId) {
		String sql = "SELECT * FROM transactions WHERE user_id = ?";
		var listConverter = new ResultSetToListT<>(converter);
		var transactions = dbHelper.executeSelect(sql, listConverter, userId);
		return transactions;
	}

	@Override
	public List<Transaction> getAllTransactionsByMoneyAccountId(int moneyAccountId) {
		String sql = "SELECT * FROM transactions WHERE money_account_id = ?";
		var listConverter = new ResultSetToListT<>(converter);
		var transactions = dbHelper.executeSelect(sql, listConverter, moneyAccountId);
		return transactions;
	}
	
	
	@Override
	public List<Transaction> getAllTransactionsByDate(int moneyAccountId, Date start, Date end) {
		String sql = "SELECT * FROM transactions WHERE money_account_id = ? AND date BETWEEN ? AND ?";
		var listConverter = new ResultSetToListT<>(converter);
		return dbHelper.executeSelect(
			sql,
			listConverter,
			moneyAccountId,
			dateFormat.format(start),
			dateFormat.format(end)
		);
	}

	//////////////////////////////////// delete
	@Override
	public boolean updateAllTransactionsByCategoryId(int oldCategoryId, int newCategoryId) {
		transactions.forEach(transaction -> {
			if (transaction.getCategoryId() == oldCategoryId) {
				transaction.setCategoryId(newCategoryId);
			}
		});
		return true;
	}

	@Override
	public boolean updateAllTransactionsByMoneyAccountId(int oldMoneyAccountId, int newMoneyAccountId) {
		transactions.forEach(transaction -> {
			if (transaction.getMoneyAccountId() == oldMoneyAccountId) {
				transaction.setMoneyAccountId(newMoneyAccountId);
			}
		});
		return true;
	}

	////////////////////////////////// end delete


	@Override
	public boolean deleteAllTransactionsByCategoryId(int categoryId) {
		String sql = "DELETE FROM transactions WHERE category_id = ?";
		return dbHelper.executeDelete(sql, categoryId);
	}
}
