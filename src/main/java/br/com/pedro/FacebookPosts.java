package br.com.pedro;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;

import br.com.pedro.dao.PostDAO;
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

	private PostDAO postDAO;

	public FacebookPosts() {
		this.facebookClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_3);
		this.postDAO = new PostDAO();
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

		Connection<Post> messages = facebookClient.fetchConnection(page + "/feed", Post.class,
				Parameter.with("since", since), Parameter.with("until", until), Parameter.with("type", "post"));

		for (List<Post> feedConnectionPage : messages) {
			for (Post post : feedConnectionPage) {
				String id = post.getId();
				String message = post.getMessage();
				java.sql.Date date = new java.sql.Date(post.getCreatedTime().getTime());

				postDAO.save(new br.com.pedro.model.Post(id, message, date));
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