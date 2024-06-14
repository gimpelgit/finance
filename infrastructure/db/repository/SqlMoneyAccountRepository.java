package infrastructure.db.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import core.domain.MoneyAccount;
import core.dto.money_account.CreateMoneyAccountDto;
import core.dto.money_account.UpdateMoneyAccountDto;
import core.repository.MoneyAccountRepository;
import infrastructure.db.function.ResultSetToT;
import infrastructure.db.helper.SqlHelper;

public class SqlMoneyAccountRepository implements MoneyAccountRepository {
	private SqlHelper dbHelper = new SqlHelper();

	@Override
	public int createMoneyAccount(CreateMoneyAccountDto createMoneyAccountDto) {
		String sql = "INSERT INTO money_accounts (user_id, name, currency) VALUES (?, ?, ?)";
		return dbHelper.executeInsert(
			sql,
			createMoneyAccountDto.userId,
			createMoneyAccountDto.name,
			createMoneyAccountDto.currency
		);
	}

	@Override
	public boolean updateMoneyAccount(UpdateMoneyAccountDto updateMoneyAccountDto) {
		String sql = "UPDATE money_accounts SET name = ? WHERE id = ?";
		return dbHelper.executeUpdate(
			sql,
			updateMoneyAccountDto.name,
			updateMoneyAccountDto.id
		);
	}


	@Override
	public boolean deleteMoneyAccountById(int id) {
		String sql = "DELETE FROM money_accounts WHERE id = ?";
		return dbHelper.executeDelete(sql, id);
	}

	@Override
	public MoneyAccount findMoneyAccountById(int id) {
		String sql = "SELECT * FROM money_accounts WHERE id = ?";
		ResultSetToT<MoneyAccount> converter = (ResultSet rs) -> {
			MoneyAccount moneyAccount = null;
			if (rs.next()) {
				int moneyAccountId = rs.getInt("id");
				int userId = rs.getInt("user_id");
				String name = rs.getString("name");
				String currency = rs.getString("currency");
				moneyAccount = new MoneyAccount(moneyAccountId, userId, name, currency);
			}
			return moneyAccount;
		};
		var moneyAccount = dbHelper.executeSelect(sql, converter, id);
		return moneyAccount;
	}

	@Override
	public List<MoneyAccount> getAllMoneyAccountsByUserId(int userId) {
		String sql = "SELECT * FROM money_accounts WHERE user_id = ?";
		ResultSetToT<List<MoneyAccount>> converter = (ResultSet rs) -> {
			List<MoneyAccount> moneyAccounts = new ArrayList<>();
			while (rs.next()) {
				int moneyAccountId = rs.getInt("id");
				String name = rs.getString("name");
				String currency = rs.getString("currency");
				moneyAccounts.add(new MoneyAccount(moneyAccountId, userId, name, currency));
			}
			return moneyAccounts;
		};
		var moneyAccounts = dbHelper.executeSelect(sql, converter, userId);
		return moneyAccounts;
	}
}
