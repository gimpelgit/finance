# Учет расходов и доходов
Вы задавались такими вопросами как: Сколько денег потратили неделю/месяц назад? На что вы тратите свои кровные каждый день? Сколько денег у вас есть сейчас? И сколько у вас остается после окончания месяца? На все эти вопросы поможет ответить мое приложение "Учет расходов и доходов".
# Автор: Кирилл 253505
## Описание
Какие возможности предоставляет данная программа:
- Добавление/изменение/удаление счета с выбором валюты
- Добавление/изменение/удаление категории расхода/дохода
- Добавление/изменение/удаление расхода/дохода по выбранной категории
- Отображение списка расходов/доходов по выбранной категории и по текущему дню
- Отображение списка расходов/доходов по выбранной категории и по текущему месяцу
- Отображение списка расходов/доходов по выбранной категории и по текущему году
- Отображение списка категорий по текущему дню
- Отображение списка категорий по текущему месяцу
- Отображение списка категорий по текущему году

## Диаграмма классов
![Диаграмма классов](/img/class_diagram.png)

## User
Класс представляющий пользователя, который хранит имя пользователя, все категории расходов/доходов данного пользователя и его счета в разных валютах.

## Category
Класс категории расхода или же дохода. Например категория "еда", "одежда", "обувь".

## MoneyAccount
Класс счета в определенной валюте. Он содержит имя счета, валюта, а текже список всех транзакций, проведенных по данному счету. 

## Transaction
Класс транзакции представляет собой финансовую операцию, связанную с расходами или доходами. Он содержит сумму, дату и описание операции, а также категорию к которой эта операция относится.