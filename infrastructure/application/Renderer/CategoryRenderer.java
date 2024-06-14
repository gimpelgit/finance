package infrastructure.application.Renderer;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.*;

import core.domain.Category;
import core.domain.Transaction;

public class CategoryRenderer implements ListCellRenderer<Category> {
	private Map<Integer, BigDecimal> sumByCategoryId = Collections.emptyMap();
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private JLabel nameLabel = new JLabel();
	private JLabel sumLabel = new JLabel();

	public CategoryRenderer(List<Transaction> transactions) {
		if (transactions == null) return;
		nameLabel.setFont(font);
		sumLabel.setFont(font);
		sumByCategoryId = transactions.stream().collect(
			Collectors.groupingBy(Transaction::getCategoryId, 
			Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Category> list, Category category, int index, 	
			boolean isSelected, boolean cellHasFocus)
	{
		JPanel panel = new JPanel(new BorderLayout());
		if (isSelected) {
			panel.setBackground(list.getSelectionBackground());
			panel.setForeground(list.getSelectionForeground());
			nameLabel.setForeground(list.getSelectionForeground());
			sumLabel.setForeground(list.getSelectionForeground());
		} else {
			panel.setBackground(list.getBackground());
			panel.setForeground(list.getForeground());
			nameLabel.setForeground(list.getForeground());
			sumLabel.setForeground(list.getForeground());
		}
		if (category != null) {
			BigDecimal sum = sumByCategoryId.getOrDefault(category.getId(), BigDecimal.ZERO);
			nameLabel.setText(category.getName());
			sumLabel.setText(sum.toString());
		}
		panel.add(nameLabel, BorderLayout.WEST);
    panel.add(sumLabel, BorderLayout.EAST);    
		return panel;
	}
}
