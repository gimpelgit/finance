package infrastructure.application.period;

import java.util.Calendar;

public enum Period {
	DAY(1),
	WEEK(Calendar.DAY_OF_WEEK),
	MONTH(Calendar.DAY_OF_MONTH),
	YEAR(Calendar.DAY_OF_YEAR),
	ALL(Integer.MAX_VALUE);

	private final int value;

	Period(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
