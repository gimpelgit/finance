package infrastructure.application.form;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

import core.domain.User;
import core.dto.user.CreateUserDto;
import core.repository.UserRepository;
import core.services.UserService;
import infrastructure.db.repository.SqlUserRepository;


public class LoginForm extends JFrame {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private UserRepository userRepository = new SqlUserRepository();
	private UserService userService = new UserService(userRepository);
	
	private JLabel lbTitleLoginForm;
	private JLabel lbEmail;
	private JLabel lbPassword;
	private JLabel lbName;

	private JTextField tfEmail;
	private JTextField tfName;
	private JPasswordField pfPassword;

	private JCheckBox cbIsCreateUser; 

	private JButton btnLogin;
	private JButton btnCancel;

	private JPanel formPanel;
	private JPanel buttonsPanel;


	public void init() {
		lbTitleLoginForm = new JLabel("Войти", SwingConstants.CENTER);
		lbTitleLoginForm.setFont(font);

		lbName = new JLabel("Имя");
		lbName.setFont(font);

		tfName = new JTextField();
		tfName.setFont(font);

		lbEmail = new JLabel("Почта");
		lbEmail.setFont(font);
		
		tfEmail = new JTextField();
		tfEmail.setFont(font);

		lbPassword = new JLabel("Пароль");
		lbPassword.setFont(font);

		pfPassword = new JPasswordField();
		pfPassword.setFont(font);

		cbIsCreateUser = new JCheckBox("Создать пользователя");
		cbIsCreateUser.setFont(font);
		cbIsCreateUser.addActionListener(e -> toggleNameField());

		formPanel = new JPanel();
		formPanel.setLayout(new GridLayout(0, 1, 15, 15));
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
		formPanel.add(lbTitleLoginForm);
		formPanel.add(lbEmail);
		formPanel.add(tfEmail);
		formPanel.add(lbPassword);
		formPanel.add(pfPassword);
		formPanel.add(cbIsCreateUser);
		add(formPanel, BorderLayout.NORTH);

		btnLogin = new JButton("Войти");
		btnLogin.setFont(font);
		btnLogin.addActionListener(e -> loginOrCreateUser());

		btnCancel = new JButton("Отмена");
		btnCancel.setFont(font);
		btnCancel.addActionListener(e -> dispose());

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 2, 15, 15));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		buttonsPanel.add(btnLogin);
		buttonsPanel.add(btnCancel);
		add(buttonsPanel, BorderLayout.SOUTH);

		setTitle("Войти");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setSize(450, 550);
		setResizable(false);
		// setMinimumSize(new Dimension(400, 500));
		// setMaximumSize(new Dimension(500, 600));

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void loginOrCreateUser() {
		String email = tfEmail.getText();
		String password = String.valueOf(pfPassword.getPassword());
		boolean isCreateUser = cbIsCreateUser.isSelected();

		User user = userService.findUserByEmail(email);
		if (user != null) {
			if (isCreateUser) {
				showMessageTryAgain("Пользователь с такой почтой уже зарегистрирован");
			}
			else if (password.equals(user.getPassword())) {
				MainForm mainForm = new MainForm();
				mainForm.init(user);
				dispose();
			} else {
				showMessageTryAgain("Пароль введен неверно");
			}
		} else if (!isCreateUser) {
			showMessageTryAgain("Пользователь не найден");
		} else {
			String name = tfName.getText();
			int id = userService.createUser(new CreateUserDto(name, email, password));
			if (id == -1) {
				showMessageTryAgain("Не получилось создать пользователя");
			} else {
				user = new User(id, name, email, password);
				MainForm mainForm = new MainForm();
				mainForm.init(user);
				dispose();
			}
		}
	}

	private void showMessageTryAgain(String message) {
		JOptionPane.showMessageDialog(
			LoginForm.this,
			message,
			"Попробуйте еще раз",
			JOptionPane.WARNING_MESSAGE
		);
	}

	private void setLoginText() {
		setTitle("Войти");
		lbTitleLoginForm.setText(getTitle());
		btnLogin.setText(getTitle());
	}

	private void setRegistrationText() {
		setTitle("Регистрация");
		lbTitleLoginForm.setText(getTitle());
		btnLogin.setText("Создать");
	}

	private void toggleNameField() {
		if (cbIsCreateUser.isSelected()) {
			formPanel.add(lbName, 1);
			formPanel.add(tfName, 2);
			setRegistrationText();
		} else {
			formPanel.remove(lbName);
			formPanel.remove(tfName);
			setLoginText();
		}
		formPanel.revalidate();
		formPanel.repaint();
	}
}
