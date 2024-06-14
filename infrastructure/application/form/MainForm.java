package infrastructure.application.form;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import core.domain.*;
import core.dto.transaction.CreateTransactionDto;
import core.dto.transaction.UpdateTransactionDto;
import core.repository.*;
import core.services.*;
import infrastructure.application.Renderer.CategoryRenderer;
import infrastructure.application.Renderer.TransactionRenderer;
import infrastructure.application.dialog.DefaultListCategory;
import infrastructure.application.dialog.DefaultListMoneyAccount;
import infrastructure.application.dialog.TransactionDialog;
import infrastructure.application.period.Period;
import infrastructure.application.period.TimePeriod;
import infrastructure.db.repository.*;


public class MainForm extends JFrame {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private static final Insets insets = new Insets(5, 5, 5, 5);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private static final Color colorSelectPeriod = Color.GREEN;
	private static final Color colorDefaultPeriod = new JButton().getBackground();

	private static final Color colorSelectType = Color.CYAN;
	private static final Color colorDefaultType = new JButton().getBackground();


	private MoneyAccountRepository moneyAccountRepository = new SqlMoneyAccountRepository();
	private TransactionRepository transactionRepository = new SqlTransactionRepository();
	private CategoryRepository categoryRepository = new SqlCategoryRepository();

	private MoneyAccountService moneyAccountService = new MoneyAccountService(moneyAccountRepository);
	private TransactionService transactionService = new TransactionService(transactionRepository);
	private CategoryService categoryService = new CategoryService(categoryRepository);

	private User user;
	private MoneyAccount curMoneyAccount;
	private TimePeriod timePeriod;

	private boolean isExpense = true;
	private List<Category> typedCategories;
	private List<Transaction> transactions;
	private List<MoneyAccount> moneyAccounts;
	
	private JButton curPeriod;
	private JList<Category> listCategory;
	private JList<Transaction> listTransaction;
	private DefaultListModel<Category> dlmCategory;
	private DefaultListModel<Transaction> dlmTransaction;
	

	private JComboBox<String> cmbMoneyAccounts;
	private JLabel lbAmountMoneyAccount;
	private JLabel lbCurrentDate;
	private JLabel lbAmountCurPeriod;


	public void init(User user) {
		this.user = user;
		
		setLayout(new GridBagLayout());
		
		JButton btnDay = new JButton("День");
		JButton btnWeek = new JButton("Неделя");
		JButton btnMonth = new JButton("Месяц");
		JButton btnYear = new JButton("Год");
		JButton btnAll = new JButton("Всё время");

		timePeriod = TimePeriod.getTimePeriod(new Date(), Period.MONTH);
		curPeriod = btnMonth;
		curPeriod.setBackground(colorSelectPeriod);

		lbCurrentDate = new JLabel();
		lbCurrentDate.setHorizontalAlignment(SwingConstants.CENTER);
		lbCurrentDate.setFont(font);

		lbAmountMoneyAccount = new JLabel();
		lbAmountMoneyAccount.setHorizontalAlignment(SwingConstants.CENTER);
		lbAmountMoneyAccount.setFont(font);

		btnDay.setFont(font);
		btnWeek.setFont(font);
		btnMonth.setFont(font);
		btnYear.setFont(font);
		btnAll.setFont(font);

		cmbMoneyAccounts = new JComboBox<>();
		cmbMoneyAccounts.setFont(font);
		((JLabel) cmbMoneyAccounts.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnIncome = new JButton("Доход");
		JButton btnExpense = new JButton("Расход");

		btnExpense.setBackground(colorSelectType);

		btnIncome.setFont(font);
		btnExpense.setFont(font);

		lbAmountCurPeriod = new JLabel();
		lbAmountCurPeriod.setFont(font);
		lbAmountCurPeriod.setHorizontalAlignment(SwingConstants.CENTER);

		JButton btnAddTransaction = new JButton("<html>Добавить<br>транзакцию</html>");
		btnAddTransaction.setHorizontalAlignment(SwingConstants.CENTER);
		btnAddTransaction.setFont(font);


		dlmCategory = new DefaultListModel<>();
		dlmTransaction = new DefaultListModel<>();
		
		listCategory = new JList<>();
		listCategory.setModel(dlmCategory);
		listCategory.setFont(font);

		listTransaction = new JList<>();
		listTransaction.setModel(dlmTransaction);
		listTransaction.setFont(font);
		listTransaction.setFixedCellHeight(60);

		JToolBar toolBar = new JToolBar();
		JButton btnCategory = new JButton("Категории");
		JButton btnMoneyAccount = new JButton("Счета");
		
		toolBar.setFloatable(false);

		btnMoneyAccount.setFont(font);
		btnCategory.setFont(font);

		toolBar.add(btnCategory);
		toolBar.add(btnMoneyAccount);
		

		var transactionPanel = new JPanel(new GridBagLayout());
		var buttonPanel = new JPanel(new GridBagLayout());
		var periodPanel = new JPanel(new GridBagLayout());
		var listDataPanel = new JPanel(new GridBagLayout());


		updateLabelCurrentDate();
		if (checkMoneyAccount()) {
			updateTotalSumMoneyAccount();
			updateDatePeriod(btnMonth, Period.MONTH);
		}

		listCategory.getSelectionModel().addListSelectionListener(e -> {
			selectCategory(listCategory.getSelectedValue());
		});

		listTransaction.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateOrDeleteTransaction(e);
			}
		}); 

		cmbMoneyAccounts.addActionListener(e -> {
			int index = cmbMoneyAccounts.getSelectedIndex();
			if (index != -1) {
				curMoneyAccount = moneyAccounts.get(index);
				updateDataList(timePeriod);
				updateTotalSumMoneyAccount();
			}
		});


		btnDay.addActionListener(e -> updateDatePeriod(btnDay, Period.DAY));
		btnWeek.addActionListener(e -> updateDatePeriod(btnWeek, Period.WEEK));
		btnMonth.addActionListener(e -> updateDatePeriod(btnMonth, Period.MONTH));
		btnYear.addActionListener(e -> updateDatePeriod(btnYear, Period.YEAR));
		btnAll.addActionListener(e -> updateDatePeriod(btnAll, Period.ALL));

		btnAddTransaction.addActionListener(e -> addTransaction());

		btnIncome.addActionListener(e -> {
			btnExpense.setBackground(colorDefaultType);
			btnIncome.setBackground(colorSelectType);
			isExpense = false;
			updateDataList(timePeriod);
		});

		btnExpense.addActionListener(e -> {
			btnIncome.setBackground(colorDefaultType);
			btnExpense.setBackground(colorSelectType);
			isExpense = true;
			updateDataList(timePeriod);
		});



		btnCategory.addActionListener(e -> {
			var dialog = new DefaultListCategory(this, user, getAllTypeCategory(), isExpense);
			dialog.init();
			if (dialog.isUpdateForm()) {
				updateDataList(timePeriod);
				updateTotalSumMoneyAccount();
			}
		});

		btnMoneyAccount.addActionListener(e -> {
			var dialog = new DefaultListMoneyAccount(this, user, moneyAccountService.getAllMoneyAccountsByUserId(user.getId()));
			dialog.init();
			if (dialog.isUpdateForm()) {
				updateMoneyAccountComboBox();
				updateTotalSumMoneyAccount();
			}
		});
		
		// отображение выбранного счета а также суммы по этому счету
		transactionPanel.add(cmbMoneyAccounts, new GridBagConstraints(4, 0, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		transactionPanel.add(lbAmountMoneyAccount, new GridBagConstraints(4, 1, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));


		// кнопки отвечающие за период отображения
		buttonPanel.add(btnDay, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		buttonPanel.add(btnWeek, new GridBagConstraints(2, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		buttonPanel.add(btnMonth, new GridBagConstraints(4, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		buttonPanel.add(btnYear, new GridBagConstraints(6, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		buttonPanel.add(btnAll, new GridBagConstraints(8, 2, 2, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));


		periodPanel.add(lbCurrentDate, new GridBagConstraints(2, 3, 6, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		periodPanel.add(lbAmountCurPeriod, new GridBagConstraints(2, 4, 6, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		periodPanel.add(btnAddTransaction, new GridBagConstraints(7, 4, 1, 1, 0.1, 0.0, 
			GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
		// periodPanel.add(btnCategory, new GridBagConstraints(0, 4, 1, 1, 0.1, 0.0, 
		// 	GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

		periodPanel.add(btnIncome, new GridBagConstraints(3, 5, 3, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		periodPanel.add(btnExpense, new GridBagConstraints(6, 5, 3, 1, 1.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));


		listDataPanel.add(new JScrollPane(listCategory), new GridBagConstraints(0, 7, 5, 0, 1.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		listDataPanel.add(new JScrollPane(listTransaction), new GridBagConstraints(5, 7, 5, 0, 1.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		// Служит чисто для того чтобы сдвинуть элементы вверх
		// add(new JLabel(), new GridBagConstraints(0, 3, 0, 0, 1.0, 1.0,
		// 	GridBagConstraints.NORTH, GridBagConstraints.BOTH, insets, 0, 0));
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		add(transactionPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		add(periodPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		add(listDataPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,  
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		
		setTitle("Финансы");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 700);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateTotalSumMoneyAccount() {
		var allAmount = transactionService.getTotalSumByMoneyAccount(curMoneyAccount.getId());
		lbAmountMoneyAccount.setText(allAmount.toString() + " " + curMoneyAccount.getCurrency());
	}

	private void updateLabelCurrentDate() {
		var str = dateFormat.format(timePeriod.getStart()) + " --- "
			+ dateFormat.format(timePeriod.getEnd()); 
		lbCurrentDate.setText(str);
	}

	private void selectButtonPeriod(JButton button) {
		curPeriod.setBackground(colorDefaultPeriod);
		button.setBackground(colorSelectPeriod);
		curPeriod = button;
	}

	private void updateDatePeriod(JButton button, Period period) {
		selectButtonPeriod(button);
		timePeriod = TimePeriod.getTimePeriod(new Date(), period);
		updateLabelCurrentDate();
		updateDataList(timePeriod);
	}

	private void updateLabelAmountCurPeriod() {
		BigDecimal sum = BigDecimal.ZERO;
		if (transactions != null) {
			for (var transaction : transactions) {
				sum = sum.add(transaction.getAmount());
			}
		}
		lbAmountCurPeriod.setText(sum.toString() + " " + curMoneyAccount.getCurrency());
	}

	
	private void updateDataList(TimePeriod timePeriod) {
		if (!checkMoneyAccount()) return;

		dlmCategory.clear();
		dlmTransaction.clear();

		if (isExpense) {
			typedCategories = categoryService.getAllExpenseCategoriesByDate(
				curMoneyAccount.getId(),
				timePeriod.getStart(),
				timePeriod.getEnd());
			transactions = transactionService.getAllExpenseTransactionsByDate(
				curMoneyAccount.getId(),
				timePeriod.getStart(),
				timePeriod.getEnd());
		}
		else {
			typedCategories = categoryService.getAllIncomeCategoriesByDate(
				curMoneyAccount.getId(),
				timePeriod.getStart(),
				timePeriod.getEnd());
			transactions = transactionService.getAllIncomeTransactionsByDate(
				curMoneyAccount.getId(),
				timePeriod.getStart(),
				timePeriod.getEnd());
		}

		if (typedCategories != null && !typedCategories.isEmpty()) {
			for (var category : typedCategories) {
				dlmCategory.addElement(category);
			}
			listCategory.setCellRenderer(new CategoryRenderer(transactions));
		}
		updateLabelAmountCurPeriod();	
	}

	private void selectCategory(Category category) {
		if (category == null) return;
		dlmTransaction.clear();
		if (transactions != null) {
			for (var transaction : transactions) {
				if (transaction.getCategoryId() == category.getId()) {
					dlmTransaction.addElement(transaction);
				}
				listTransaction.setCellRenderer(new TransactionRenderer());
			}
		}
	}

	private void addTransaction() {
		if (!checkMoneyAccount()) {
			JOptionPane.showMessageDialog(
				MainForm.this,
				"Нельзя добавить транзакцию, если нет ни одного счета",
				"Добавьте сначала хотя бы одни счет",
				JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		List<Category> categories = getAllTypeCategory();
		TransactionDialog dialog = new TransactionDialog(this, categories, null);
		if (dialog.isCreate()) {
			transactionService.createTransaction(new CreateTransactionDto(
				user.getId(),
				dialog.getCategoryId(),
				curMoneyAccount.getId(),
				dialog.getAmount(),
				dialog.getDate(),
				dialog.getDescription(),
				(isExpense ? TransactionType.EXPENSE : TransactionType.INCOME)
			));
			updateDataList(timePeriod);
			updateTotalSumMoneyAccount();
		}
	}

	private void updateOrDeleteTransaction(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (!checkMoneyAccount()) return;
			List<Category> categories = getAllTypeCategory();
			int index = listTransaction.locationToIndex(e.getPoint());
			Transaction curTransaction;
			if (index != -1) {
				curTransaction = listTransaction.getModel().getElementAt(index);
			} else {
				return;
			}
			TransactionDialog dialog = new TransactionDialog(this, categories, curTransaction);
			if (dialog.isUpdate()) {
				transactionService.updateTransaction(new UpdateTransactionDto(
					curTransaction.getId(),
					dialog.getCategoryId(),
					dialog.getAmount(),
					dialog.getDate(),
					dialog.getDescription(),
					(isExpense ? TransactionType.EXPENSE : TransactionType.INCOME)
				));
			} else if (dialog.isDelete()) {
				transactionService.deleteTransactionById(curTransaction.getId());
			}
			if (dialog.isUpdate() || dialog.isDelete()) {
				updateDataList(timePeriod);
				updateTotalSumMoneyAccount();
			}
		}
	} 

	private List<Category> getAllTypeCategory() {
		if (isExpense) {
			return categoryService.getAllExpenseCategoriesByUserId(user.getId());
		} else { 
			return categoryService.getAllIncomeCategoriesByUserId(user.getId());
		}
	}

	private boolean checkMoneyAccount() {
		if (curMoneyAccount == null) {
			moneyAccounts = moneyAccountService.getAllMoneyAccountsByUserId(user.getId());
			if (moneyAccounts != null && !moneyAccounts.isEmpty()) {
				curMoneyAccount = moneyAccounts.get(0);
				for (MoneyAccount account : moneyAccounts) {
					cmbMoneyAccounts.addItem(account.getName());
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

	private void updateMoneyAccountComboBox() {
		moneyAccounts = moneyAccountService.getAllMoneyAccountsByUserId(user.getId());
		if (moneyAccounts != null) {
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbMoneyAccounts.getModel();
			model.removeAllElements();
			for (MoneyAccount account : moneyAccounts) {
				cmbMoneyAccounts.addItem(account.getName());
			}
		}
	}
}