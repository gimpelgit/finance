package infrastructure.application.dialog;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import core.domain.Category;
import core.domain.Transaction;

public class TransactionDialog extends JDialog {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private static final Insets insets = new Insets(5, 5, 5, 5);
	private JTextField tfAmount;
	private JTextField tfDescription;
	private JTextField tfDate;
	private JComboBox<String> cmbCategory;
	private JButton addOrUpdateButton;
	private JButton cancelOrDeleteButton;

	private boolean isCreate = false;
	private boolean isUpdate = false;
	private boolean isDelete = false;
	private List<Category> listCategory;

	private BigDecimal amount;
	private String description;
	private int categoryId;
	private Date date;
	
	public TransactionDialog(JFrame parent, List<Category> listCategory, Transaction transaction) {
		super(parent, "Добавить транзакцию", true);
		this.listCategory = listCategory;
		
		cmbCategory = new JComboBox<>();
		cmbCategory.setFont(font);

		if (listCategory != null && !listCategory.isEmpty()) {
			for (var cat : listCategory) {
				cmbCategory.addItem(cat.getName());
			}
		} else {
			JOptionPane.showMessageDialog(
				TransactionDialog.this,
				"Нельзя добавить/обновить/удалить транзакцию, если нет ни одной категории",
				"Добавьте сначала хотя бы одну категорию",
				JOptionPane.ERROR_MESSAGE
			);
			dispose();
			return;
		}

		JLabel lbAmount = new JLabel("Сумма");
		JLabel lbCategory = new JLabel("Категория");
		JLabel lbDescription = new JLabel("Описание");
		
		tfAmount = new JTextField(10);
		tfDescription = new JTextField(20);
		tfDate = new JTextField(10);
		tfDate.setText(dateFormat.format(new Date()));

		if (transaction == null) {
			addOrUpdateButton = new JButton("Добавить");
			cancelOrDeleteButton = new JButton("Отмена");
		} else {
			addOrUpdateButton = new JButton("Обновить");
			cancelOrDeleteButton = new JButton("Удалить");
			
			tfAmount.setText(transaction.getAmount().toString());
			tfDescription.setText(transaction.getDescription());
			tfDate.setText(dateFormat.format(transaction.getDate()));
			for (var i = 0; i < listCategory.size(); ++i) {
				if (listCategory.get(i).getId() == transaction.getCategoryId()) {
					cmbCategory.setSelectedIndex(i);
					break;
				}
			}
		}



		lbAmount.setFont(font);
		lbDescription.setFont(font);
		lbCategory.setFont(font);

		tfAmount.setFont(font);
		tfDescription.setFont(font);
		tfDate.setFont(font);

		addOrUpdateButton.setFont(font);
		cancelOrDeleteButton.setFont(font);




		setLayout(new GridBagLayout());

		var formPanel = new JPanel(new GridBagLayout());
		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		formPanel.add(lbAmount, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		formPanel.add(lbCategory, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		formPanel.add(lbDescription, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		
		formPanel.add(tfAmount, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		formPanel.add(cmbCategory, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		formPanel.add(tfDate, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		formPanel.add(tfDescription, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		
		buttonPanel.add(cancelOrDeleteButton);
		buttonPanel.add(addOrUpdateButton);

		addOrUpdateButton.addActionListener(e -> {
			if (createTransactionDto()) {
				if (transaction == null) {
					isCreate = true;
				} else {
					isUpdate = true;
				}
				dispose();
			}
		});

		cancelOrDeleteButton.addActionListener(e -> {
			if (transaction != null) {
				isDelete = true;
			}
			dispose();
		});

		add(formPanel, new GridBagConstraints(0, 0, 6, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 1, 4, 1, 1.0, 0.0, 
		GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

	
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}


	public boolean isCreate() {
		return isCreate;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Date getDate() {
		return date;
	}

	public int getCategoryId() {
		return categoryId;
	}

	private boolean createTransactionDto() {
		boolean ok = false;
		try {
			amount = new BigDecimal(tfAmount.getText());
			description = tfDescription.getText();
			if (tfDate.getText().length() != 10) {
				throw new ParseException("Слишком большая строка", 11);
			}
			date = dateFormat.parse(tfDate.getText());
			if (cmbCategory.getSelectedIndex() == -1) {
				throw new Exception();
			}
			categoryId = listCategory.get(cmbCategory.getSelectedIndex()).getId();
			ok = true;
		} catch (ParseException ex) {
			showMessage("Неккоректный формат даты", "Пример корректного формата даты: 12.08.2023");
		} catch (NumberFormatException ex) {
			showMessage("Введите число в поле 'Сумма'", "Не удалось преобразовать '" + tfAmount.getText() + "' в число");
		} catch (Exception ex) {}
		return ok;
	}

	private void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(
			TransactionDialog.this,
			message,
			title,
			JOptionPane.WARNING_MESSAGE
		);
	}
}
