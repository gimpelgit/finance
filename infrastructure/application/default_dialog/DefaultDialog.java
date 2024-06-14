package infrastructure.application.default_dialog;

import java.util.ArrayList;
import java.util.List;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.*;

public class DefaultDialog extends JDialog {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private static final Insets insets = new Insets(5, 5, 5, 5);
	
	private JButton addOrUpdateButton;
	private JButton cancelOrDeleteButton;
	private List<JTextField> listTextFields = new ArrayList<>();

	private boolean isCreate = false;
	private boolean isUpdate = false;
	private boolean isDelete = false;
	
	public DefaultDialog(JFrame parent, String title, 
			List<String> textInLabels, List<String> textInTextFields)
	{
		super(parent, title, true);
		init(textInLabels, textInTextFields);
	}

	public DefaultDialog(JDialog parent, String title, 
			List<String> textInLabels, List<String> textInTextFields)
	{
		super(parent, title, true);
		init(textInLabels, textInTextFields);
	}

	private void init(List<String> textInLabels, List<String> textInTextFields) {
		if (textInTextFields == null) {
			addOrUpdateButton = new JButton("Добавить");
			cancelOrDeleteButton = new JButton("Отмена");
		} else {
			addOrUpdateButton = new JButton("Обновить");
			cancelOrDeleteButton = new JButton("Удалить");
		}

		addOrUpdateButton.setFont(font);
		cancelOrDeleteButton.setFont(font);

		setLayout(new GridBagLayout());

		var formPanel = new JPanel(new GridBagLayout());
		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		
		for (int i = 0; i < textInLabels.size(); ++i) {
			JLabel label = new JLabel();
			JTextField textField = new JTextField(20);
			label.setFont(font);
			textField.setFont(font);

			if (textInTextFields != null) {
				textField.setText(textInTextFields.get(i));
			}
			label.setText(textInLabels.get(i));

			listTextFields.add(textField);
			
			formPanel.add(label, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
			formPanel.add(textField, new GridBagConstraints(1, i, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		}
		
		buttonPanel.add(cancelOrDeleteButton);
		buttonPanel.add(addOrUpdateButton);

		addOrUpdateButton.addActionListener(e -> {
			if (createObject()) {
				if (textInTextFields == null) {
					isCreate = true;
				} else {
					isUpdate = true;
				}
				dispose();
			}
		});

		cancelOrDeleteButton.addActionListener(e -> {
			if (textInTextFields != null) {
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


	public boolean createObject() {
		for (var textField : listTextFields) {
			if (textField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(
					DefaultDialog.this,
					"Ни одно поле не должно быть пустым",
					"Заполните все поля",
					JOptionPane.WARNING_MESSAGE
				);
				return false;
			}
		}
		return true;
	}

	public String getField(int index) {
		return listTextFields.get(index).getText();
	}
}
