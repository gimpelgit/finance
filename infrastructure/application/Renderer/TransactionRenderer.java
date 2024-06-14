package infrastructure.application.Renderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import core.domain.Transaction;

public class TransactionRenderer implements ListCellRenderer<Transaction> {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	private JLabel dateLabel = new JLabel();
	private JLabel descriptionLabel = new JLabel();
	private JLabel sumLabel = new JLabel();


	@Override
	public Component getListCellRendererComponent(JList<? extends Transaction> list, Transaction transaction, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		JPanel panel = new JPanel(new BorderLayout());
		JPanel textPanel = new JPanel(new GridLayout(2, 1));

		sumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		dateLabel.setFont(font);
		descriptionLabel.setFont(font);
		sumLabel.setFont(font);

		textPanel.add(dateLabel);
		textPanel.add(descriptionLabel);

		if (transaction != null) {
			dateLabel.setText(dateFormat.format(transaction.getDate()));
			descriptionLabel.setText(transaction.getDescription());
			sumLabel.setText(transaction.getAmount().toString());
		}


		if (isSelected) {
			panel.setBackground(list.getSelectionBackground());
			panel.setForeground(list.getSelectionForeground());
			textPanel.setBackground(list.getSelectionBackground());
			textPanel.setForeground(list.getSelectionForeground());
			dateLabel.setForeground(list.getSelectionForeground());
			descriptionLabel.setForeground(list.getSelectionForeground());
			sumLabel.setForeground(list.getSelectionForeground());
		} else {
			panel.setBackground(list.getBackground());
			panel.setForeground(list.getForeground());
			textPanel.setBackground(list.getBackground());
			textPanel.setForeground(list.getForeground());
			dateLabel.setForeground(list.getForeground());
			descriptionLabel.setForeground(list.getForeground());
			sumLabel.setForeground(list.getForeground());
		}

		panel.add(textPanel, BorderLayout.WEST);
		panel.add(sumLabel, BorderLayout.EAST);

		return panel;
	}
	
}
