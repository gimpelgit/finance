package infrastructure.application.dialog;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import core.domain.MoneyAccount;
import core.domain.User;
import core.dto.money_account.CreateMoneyAccountDto;
import core.dto.money_account.UpdateMoneyAccountDto;
import core.repository.MoneyAccountRepository;
import core.services.MoneyAccountService;
import infrastructure.application.default_dialog.DefaultDialog;
import infrastructure.application.default_dialog.DefaultListDialog;
import infrastructure.db.repository.SqlMoneyAccountRepository;

public class DefaultListMoneyAccount extends DefaultListDialog<MoneyAccount> {
	private MoneyAccountRepository moneyAccountRepository = new SqlMoneyAccountRepository();
	private MoneyAccountService moneyAccountService = new MoneyAccountService(moneyAccountRepository);
	private User user;

	private boolean isUpdateForm = false;


	public DefaultListMoneyAccount(JFrame parent, User user, List<MoneyAccount> list) {
		super(parent, "счет", "Список всех счетов", list);
		this.user = user;
	}

	@Override
	public void addModel() {
		DefaultDialog dialog = new DefaultDialog(
			this,
			"Добавить счет",
			Arrays.asList("Название", "Валюта"),
			null
		);
		if (dialog.isCreate()) {
			var moneyAccountDto =  new CreateMoneyAccountDto (
				user.getId(),
				dialog.getField(0),
				dialog.getField(1)
			);
			int id = moneyAccountService.createMoneyAccount(moneyAccountDto);
			dlmModel.addElement(new MoneyAccount(id, moneyAccountDto.userId, moneyAccountDto.name, moneyAccountDto.currency));
			isUpdateForm = true;
		}
	}

	@Override
	public void updateOrDeleteModel(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int index = listModel.locationToIndex(e.getPoint());
			MoneyAccount curMoneyAccount;
			if (index != -1) {
				curMoneyAccount = listModel.getModel().getElementAt(index);
			} else {
				return;
			}
			DefaultDialog dialog = new DefaultDialog(
				this,
				"Обновить счет",
				Arrays.asList("Название"),
				Arrays.asList(curMoneyAccount.getName())
			);
			if (dialog.isUpdate()) {
				var moneyAccountDto = new UpdateMoneyAccountDto(
					curMoneyAccount.getId(),
					dialog.getField(0)
				);
				moneyAccountService.updateMoneyAccount(moneyAccountDto);
				curMoneyAccount.setName(dialog.getField(0));
				isUpdateForm = true;
			} else if (dialog.isDelete()) {
				moneyAccountService.deleteMoneyAccountById(curMoneyAccount.getId());
				dlmModel.remove(index);
				isUpdateForm = true;
			}
		}
	}

	public boolean isUpdateForm() {
		return isUpdateForm;
	}
}