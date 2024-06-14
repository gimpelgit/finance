package infrastructure.application.dialog;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import core.domain.Category;
import core.domain.TransactionType;
import core.domain.User;
import core.dto.category.CreateCategoryDto;
import core.dto.category.UpdateCategoryDto;
import core.repository.CategoryRepository;
import core.services.CategoryService;
import infrastructure.application.default_dialog.DefaultDialog;
import infrastructure.application.default_dialog.DefaultListDialog;
import infrastructure.db.repository.SqlCategoryRepository;

public class DefaultListCategory extends DefaultListDialog<Category> {
	private CategoryRepository categoryRepository = new SqlCategoryRepository();
	private CategoryService categoryService = new CategoryService(categoryRepository);
	private boolean isExpense;
	private User user;

	private boolean isUpdateForm = false;


	public DefaultListCategory(JFrame parent, User user, List<Category> list, boolean isExpense) {
		super(parent, "категорию", "Список категорий " + (isExpense ? "расходов" : "доходов"), list);
		this.user = user;
		this.isExpense = isExpense;
	}

	@Override
	public void addModel() {
		DefaultDialog dialog = new DefaultDialog(
			this,
			"Добавить категорию",
			Arrays.asList("Название"),
			null
		);
		if (dialog.isCreate()) {
			var categoryDto =  new CreateCategoryDto(
				user.getId(),
				dialog.getField(0),
				(isExpense ? TransactionType.EXPENSE : TransactionType.INCOME)
			);
			int id = categoryService.createCategory(categoryDto);
			dlmModel.addElement(new Category(id, categoryDto.userId, categoryDto.name, categoryDto.type));
		}
	}

	@Override
	public void updateOrDeleteModel(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int index = listModel.locationToIndex(e.getPoint());
			Category curCategory;
			if (index != -1) {
				curCategory = listModel.getModel().getElementAt(index);
			} else {
				return;
			}
			DefaultDialog dialog = new DefaultDialog(
				this,
				"Обновить категорию",
				Arrays.asList("Название"),
				Arrays.asList(curCategory.getName())
			);
			if (dialog.isUpdate()) {
				var categoryDto = new UpdateCategoryDto(
					curCategory.getId(),
					dialog.getField(0)
				);
				categoryService.updateCategory(categoryDto);
				curCategory.setName(dialog.getField(0));
				isUpdateForm = true;
			} else if (dialog.isDelete()) {
				categoryService.deleteCategoryById(curCategory.getId());
				dlmModel.remove(index);
				isUpdateForm = true;
			}
		}
	}

	public boolean isUpdateForm() {
		return isUpdateForm;
	}
}