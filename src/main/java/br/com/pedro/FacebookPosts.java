package br.com.pedro;

import java.util.Date;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;

import br.com.pedro.utils.Utils;

/**
 * Java Facebook Posts
 * 
 * @author Pedro Marinho Medeiros
 *
 */
public class FacebookPosts {

	/**
	 * A valid Graph API access token.
	 */
	private final String ACCESS_TOKEN = "INSERT A VALID ACCESS TOKEN";

	/**
	 * RestFB Graph API client.
	 */
	private FacebookClient facebookClient;

	public FacebookPosts() {
		this.facebookClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_3);
	}

	/**
	 * Retrieves posts from the last X days of a page Y.
	 * 
	 * @param days
	 *            The number of days.
	 * @param page
	 *            The page to retrieve posts.
	 */
	public void retrievePosts(int days, String page) {

		Date until = new Date();

		Date since = Utils.decreaseDays(days, until);

		JsonObject pageFeed = facebookClient.fetchObject(page + "/feed", JsonObject.class,
				Parameter.with("until", until), Parameter.with("since", since));

		JsonArray jsonArray = pageFeed.get("data").asArray();
		for (JsonValue jsonValue : jsonArray) {
			String id = jsonValue.asObject().get("id").toString();
			try {
				String message = jsonValue.asObject().get("message").toString();
				String createdTime = jsonValue.asObject().get("created_time").toString();
				System.out.println(id);
				System.out.println(message);
				System.out.println(createdTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Enter two arguments: the number of days to collect posts and the page id.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			throw new IllegalArgumentException(
					"Enter two arguments: the number of days to collect posts and the page id.");
		}
		try {
			FacebookPosts fp = new FacebookPosts();
			fp.retrievePosts(Integer.parseInt(args[0]), args[1]);
		} catch (NumberFormatException nfe) {
			System.err.println("The first argument is not an integer.");
		}
	}
}