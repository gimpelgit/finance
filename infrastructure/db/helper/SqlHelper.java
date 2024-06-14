package infrastructure.db.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import infrastructure.db.function.ResultSetToT;



public class SqlHelper {
	public static final String DB_URL = "jdbc:mysql://localhost/finance?serverTimezone=UTC";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "";


	private Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
	}

	public <T> T executeSelect(String sql, ResultSetToT<T> converter, Object... params) {
		T result = null;
		try (Connection connection = connect();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			setParameters(statement, params);
			ResultSet resultSet = statement.executeQuery();
			result = converter.convert(resultSet);
		} catch (SQLException e) {}
		return result;
	}

	public int executeInsert(String sql, Object... params) {
		int result = -1;
		try (Connection connection = connect();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			setParameters(statement, params);
			statement.executeUpdate();
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					result = generatedKeys.getInt(1);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	// Выполнение запросов UPDATE, DELETE (изменение данных)
	public boolean executeUpdate(String sql, Object... params) {
		boolean ok = true;
		try (Connection connection = connect();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			setParameters(statement, params);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			ok = false;
		}
		return ok;
	}

	public boolean executeDelete(String sql, Object... params) {
		return executeUpdate(sql, params);
	}

	private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
		if (params == null) return;
		for (int i = 0; i < params.length; ++i) {
			statement.setObject(i + 1, params[i]);
		}
  }
}