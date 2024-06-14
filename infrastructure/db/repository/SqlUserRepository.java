package infrastructure.db.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import core.domain.User;
import core.dto.user.CreateUserDto;
import core.dto.user.UpdateUserDto;
import core.repository.UserRepository;
import infrastructure.db.function.ResultSetToT;
import infrastructure.db.helper.SqlHelper;


public class SqlUserRepository implements UserRepository {
	private SqlHelper dbHelper = new SqlHelper();

	@Override
	public int createUser(CreateUserDto createUserDto) {
		String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
		return dbHelper.executeInsert(
			sql,
			createUserDto.name,
			createUserDto.email,
			createUserDto.password
		);
	}

	@Override
	public boolean updateUser(UpdateUserDto updateUserDto) {
		String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
		return dbHelper.executeUpdate(
			sql,
			updateUserDto.name,
			updateUserDto.email,
			updateUserDto.password,
			updateUserDto.id
		);
	}

	@Override
	public boolean deleteUserById(int id) {
		String sql = "DELETE FROM users WHERE id = ?";
		return dbHelper.executeDelete(sql, id);
	}

	@Override
	public User findUserById(int id) {
		String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
		ResultSetToT<User> converter = (ResultSet rs) -> {
			User user = null;
			if (rs.next()) {
				int userId = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String password = rs.getString("password");
				user = new User(userId, name, email, password);
			}
			return user;
		};
		var user = dbHelper.executeSelect(sql, converter, id);
		return user;
	}

	@Override
	public User findUserByEmail(String email) {
		String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
		ResultSetToT<User> converter = (ResultSet rs) -> {
			User user = null;
			if (rs.next()) {
				int userId = rs.getInt("id");
				String name = rs.getString("name");
				String userEmail = rs.getString("email");
				String password = rs.getString("password");
				user = new User(userId, name, userEmail, password);
			}
			return user;
		};
		var user = dbHelper.executeSelect(sql, converter, email);
		return user;
	}

	@Override
	public List<User> getAllUser() {
		String sql = "SELECT id, name, email, password FROM users";
		ResultSetToT<List<User>> converter = (ResultSet rs) -> {
			List<User> users = new ArrayList<>();
			while (rs.next()) {
				int userId = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String password = rs.getString("password");
				users.add(new User(userId, name, email, password));
			}
			return users;
		};
		var users = dbHelper.executeSelect(sql, converter);
		return users;
	}
}
