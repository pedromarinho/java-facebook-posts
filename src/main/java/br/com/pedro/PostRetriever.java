package br.com.pedro;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

import br.com.pedro.dao.PostDAO;
import br.com.pedro.model.Post;
import br.com.pedro.utils.Utils;

/**
 * 
 * @author Pedro Marinho Medeiros
 *
 */
public class PostRetriever {

	private static PostDAO postDAO = new PostDAO();

	/**
	 * Get collected posts in a date range.
	 * 
	 * @param since
	 * @param until
	 * @return
	 * @throws ParseException
	 */
	public static JsonArray getPosts(String since, String until) throws ParseException {
		Date sinceDate = Utils.formatToSqlDate(since, "yyyyMMdd");
		Date untilDate = Utils.formatToSqlDate(until, "yyyyMMdd");

		List<Post> posts = postDAO.listByDateInterval(sinceDate, untilDate);

		JsonArray result = new JsonArray();

		for (Post post : posts) {
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
	 * @param until
	 * @return
	 * @throws ParseException
	 */
	public static JsonArray getVolume(String since, String until) throws ParseException {

		JsonArray result = new JsonArray();

		Calendar gcal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date start = new Date(sdf.parse(since).getTime());
		Date end = new Date(sdf.parse(until).getTime());
		gcal.setTime(start);

		List<Post> posts = postDAO.listByDateInterval(start, end);

		Map<Date, Integer> dateMap = new HashMap<Date, Integer>();

		for (Post post : posts) {
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

	public static void main(String[] args) throws ParseException {

		System.out.println(PostRetriever.getPosts("20160916", "20170929"));

		System.out.println(PostRetriever.getVolume("20160916", "20170919"));
	}

}