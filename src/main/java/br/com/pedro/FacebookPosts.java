package br.com.pedro;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;

import br.com.pedro.dao.FbPostDAO;
import br.com.pedro.model.FbPost;
import br.com.pedro.utils.Utils;

/**
 * Java Facebook Posts
 * 
 * @author Pedro Marinho Medeiros
 *
 */
public class FacebookPosts {
	/**
	 * RestFB Graph API client.
	 */
	private FacebookClient facebookClient;

	private FbPostDAO fbPostDAO;

	public FacebookPosts(String accessToken) {
		this.facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_3);
		this.fbPostDAO = new FbPostDAO();
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
		if (days < 0 || page == null || page.isEmpty()) {
			throw new IllegalArgumentException("Invalid data!");
		}

		Date until = new Date();

		Date since = Utils.decreaseDays(days, until);

		System.out.println("retrieving posts from " + since + " to " + until.toString());

		Connection<Post> messages = facebookClient.fetchConnection(page + "/feed", Post.class,
				Parameter.with("since", since), Parameter.with("until", until), Parameter.with("type", "post"));

		for (List<Post> feedConnectionPage : messages) {
			for (Post post : feedConnectionPage) {
				String id = post.getId();
				String message = post.getMessage();
				java.sql.Date date = new java.sql.Date(post.getCreatedTime().getTime());

				fbPostDAO.save(new FbPost(id, message, date));
			}
		}

		System.out.println("finished");
	}

	/**
	 * Enter three arguments: the number of days to collect posts, the page id
	 * and a valid Graph API access token.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			throw new IllegalArgumentException(
					"Enter three arguments: the number of days to collect posts, the page id and a valid Graph API access token.");
		}
		try {
			FacebookPosts fp = new FacebookPosts(args[2]);
			fp.retrievePosts(Integer.parseInt(args[0]), args[1]);
		} catch (NumberFormatException nfe) {
			System.err.println("The first argument is not an integer.");
		}
	}
}