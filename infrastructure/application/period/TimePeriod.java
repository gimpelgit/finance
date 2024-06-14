package infrastructure.application.period;

import java.util.Calendar;
import java.util.Date;

public class TimePeriod {
	private Date start;
	private Date end;

	public TimePeriod(Date start, Date end) {
		this.start = start;
		this.end = end;
	}


	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public static TimePeriod getTimePeriod(Date date, Period period) {
		if (period == Period.DAY) {
			return new TimePeriod(date, date);
		} else if (period == Period.ALL) {
			return new TimePeriod(new Date(0, 0, 1), new Date());
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int value = period.getValue();

		Date start, end;
		if (period != Period.WEEK) {
			calendar.set(value, calendar.getActualMinimum(value));
			start = calendar.getTime();
			calendar.set(value, calendar.getActualMaximum(value));
			end = calendar.getTime();
		} else {
			calendar.set(value, calendar.getActualMinimum(value));
			end = calendar.getTime();
			calendar.add(Calendar.DATE, -6);
			start = calendar.getTime();
		}
		return new TimePeriod(start, end);
	}
}
