package br.com.pedro.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utils
 * 
 * @author Pedro Marinho Medeiros
 *
 */
public class Utils {

	/**
	 * Format a string date do java.sql.Date
	 * 
	 * @param date
	 *            The date. ex: "01-01-2017".
	 * @param format
	 *            The date format. ex: "dd-MM-yyyy".
	 * @return java.sql.Date.
	 * @throws ParseException
	 */
	public static Date formatToSqlDate(String date, String format) throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		return new Date(formater.parse(date).getTime());
	}

	/**
	 * Decrease days from a date
	 * 
	 * @param days
	 *            The number of days to decrease.
	 * @param date
	 *            A date.
	 * @return The entry date minus x days.
	 */
	public static java.util.Date decreaseDays(int days, java.util.Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -days);
		return calendar.getTime();
	}

}
