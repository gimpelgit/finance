package infrastructure.db.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import core.domain.Category;
import core.dto.category.CreateCategoryDto;
import core.dto.category.UpdateCategoryDto;
import core.repository.CategoryRepository;
import infrastructure.db.function.ResultSetToCategory;
import infrastructure.db.function.ResultSetToListT;
import infrastructure.db.helper.SqlHelper;


public class SqlCategoryRepository implements CategoryRepository {
	private SqlHelper dbHelper = new SqlHelper();
	private ResultSetToCategory converter = new ResultSetToCategory();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public int createCategory(CreateCategoryDto createCategoryDto) {
		String sql = "INSERT INTO categories (user_id, name, type) VALUES (?, ?, ?)";
		return dbHelper.executeInsert(
			sql,
			createCategoryDto.userId,
			createCategoryDto.name,
			createCategoryDto.type.toString()
		);
	}

	@Override
	public boolean updateCategory(UpdateCategoryDto updateCategoryDto) {
		String sql = "UPDATE categories SET name = ? WHERE id = ?";
		return dbHelper.executeUpdate(
			sql,
			updateCategoryDto.name,
			updateCategoryDto.id
		);
	}

	@Override
	public boolean deleteCategoryById(int id) {
		String sql = "DELETE FROM categories WHERE id = ?";
		return dbHelper.executeDelete(sql, id);
	}

	@Override
	public Category findCategoryById(int id) {
		String sql = "SELECT * FROM categories WHERE id = ?";
		var category = dbHelper.executeSelect(sql, converter, id);
		return category;
	}

	

	@Override
	public List<Category> getAllCategoriesByUserId(int userId) {
		String sql = "SELECT * FROM categories WHERE user_id = ?";
		var listConverter = new ResultSetToListT<>(converter);
		var categories = dbHelper.executeSelect(sql, listConverter, userId);
		return categories;
	}

	@Override
	public List<Category> getAllCategoryByDate(int moneyAccountId, Date start, Date end) {
		String sql = "SELECT DISTINCT c.id, c.user_id, c.name, c.type " + 
			"FROM categories c JOIN transactions t ON c.id = t.category_id " + 
			"WHERE t.money_account_id = ? AND t.date BETWEEN ? AND ?";
		var listConverter = new ResultSetToListT<>(converter);
		return dbHelper.executeSelect(
			sql,
			listConverter,
			moneyAccountId,
			dateFormat.format(start),
			dateFormat.format(end)
		);
	}
}
