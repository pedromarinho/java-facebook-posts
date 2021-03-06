package br.com.pedro;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

import br.com.pedro.dao.FbPostDAO;
import br.com.pedro.model.FbPost;
import br.com.pedro.utils.Utils;

/**
 *
 * @author Pedro Marinho Medeiros
 *
 */
public class PostRetriever {

	private static FbPostDAO postDAO = new FbPostDAO();

	/**
	 * Get collected posts in a date range.
	 *
	 * @param since
	 *            Star date.
	 * @param until
	 *            End date.
	 * @return A json containing the posts.
	 * @throws Exception
	 */
	public static JsonArray getPosts(String since, String until) throws Exception {
		if (since == null || until == null) {
			throw new IllegalArgumentException("Invalid Date");
		}
		Date sinceDate = Utils.formatToSqlDate(since, "yyyyMMdd");
		Date untilDate = Utils.formatToSqlDate(until, "yyyyMMdd");

		if (sinceDate.after(untilDate)) {
			throw new IllegalArgumentException("Start date is longer than end date!");
		}

		List<FbPost> posts = postDAO.listByDateInterval(sinceDate, untilDate);

		JsonArray result = new JsonArray();

		for (FbPost post : posts) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("id", post.getId());
			jsonObject.add("message", post.getMessage());
			jsonObject.add("created_time", post.getCreatedTime().toString());
			result.add(jsonObject);
		}

		return result;
	}

	/**
	 * Get number of posts per day in a date range.
	 *
	 * @param since
	 *            Star date.
	 * @param until
	 *            End date.
	 * @return A json containing the number of posts per day.
	 * @throws Exception
	 */
	public static JsonArray getVolume(String since, String until) throws Exception {
		if (since == null || until == null) {
			throw new IllegalArgumentException("Invalid Date!");
		}

		JsonArray result = new JsonArray();

		Calendar gcal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date start = new Date(sdf.parse(since).getTime());
		Date end = new Date(sdf.parse(until).getTime());

		if (start.after(end)) {
			throw new IllegalArgumentException("Start date is longer than end date!");
		}

		gcal.setTime(start);

		List<FbPost> posts = postDAO.listByDateInterval(start, end);

		Map<Date, Integer> dateMap = new HashMap<Date, Integer>();

		for (FbPost post : posts) {
			Date date = post.getCreatedTime();
			if (dateMap.containsKey(date)) {
				dateMap.replace(date, dateMap.get(date) + 1);
			} else {
				dateMap.put(date, 1);
			}
		}

		for (gcal.getTime(); !gcal.getTime().after(end); gcal.add(Calendar.DATE, 1)) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("date", sdf.format(gcal.getTime()));
			if (dateMap.containsKey(gcal.getTime())) {
				jsonObject.add("sum_posts", String.valueOf(dateMap.get(gcal.getTime())));
			} else {
				jsonObject.add("sum_posts", "0");
			}
			result.add(jsonObject);
		}
		return result;
	}
}