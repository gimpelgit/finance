package infrastructure.application.default_dialog;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;



public class DefaultListDialog<T> extends JDialog {
	private static final Font font = new Font("Times New Roman", Font.BOLD, 20);
	private static final EmptyBorder emptyBorder = new EmptyBorder(15, 15, 15, 15);
	private static final Dimension verticalMargin = new Dimension(0, 15);
	private String name;
	private String nameList;
	private List<T> list;
	
	protected DefaultListModel<T> dlmModel;
	protected JList<T> listModel;

	public DefaultListDialog(JFrame parent, String name, String nameList, List<T> list) {
		super(parent, nameList, true);
		init(name, nameList, list);
	}

	public DefaultListDialog(JDialog parent, String name, String nameList, List<T> list) {
		super(parent, nameList, true);
		init(name, nameList, list);
	}

	private void init(String name, String nameList, List<T> list) {
		this.name = name;
		this.nameList = nameList;
		this.list = list;
	}

	public void init() {
		JLabel lbTitle = new JLabel(nameList, SwingConstants.CENTER);
		lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbTitle.setFont(font);

		JButton btnAddModel = new JButton("Создать " + name);
		btnAddModel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAddModel.setFont(font);

		dlmModel = new DefaultListModel<>();

		if (list != null) {
			for (var elem : list) {
				dlmModel.addElement(elem);
			}
		}

		listModel = new JList<>();
		listModel.setModel(dlmModel);
		listModel.setFont(font);

		
		listModel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateOrDeleteModel(e);
			}
		}); 

		btnAddModel.addActionListener(e -> addModel());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(lbTitle);
		panel.add(Box.createRigidArea(verticalMargin));
		
		panel.add(btnAddModel);
		panel.add(Box.createRigidArea(verticalMargin));
		panel.add(new JScrollPane(listModel));
		panel.setBorder(emptyBorder);
		
		add(panel);

		setSize(500, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void addModel() {}

	public void updateOrDeleteModel(MouseEvent e) {}

}
