package br.com.pedro.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.pedro.model.FbPost;

public class FbPostDAO {

	private String url = "jdbc:postgresql://localhost:5432/postgres";
	private String user = "postgres";
	private String pass = "postgres";

	private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS posts " + "(id text NOT NULL, " + "message text, "
			+ "created_time date, " + "CONSTRAINT pk_id PRIMARY KEY (id))";
	private final String SELECT_SQL = "SELECT * FROM posts";
	private final String INSERT_SQL = "INSERT INTO posts (id, message, created_time) VALUES (?, ?, ?)";
	private final String SELECT_BY_INTERVAL = "SELECT * FROM posts WHERE created_time BETWEEN ? AND ?";

	public FbPostDAO() {
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			createTable();
			System.out.println("connected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void save(FbPost post) {
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			PreparedStatement stmt = connection.prepareStatement(INSERT_SQL);

			stmt.setString(1, post.getId());
			stmt.setString(2, post.getMessage());
			stmt.setDate(3, post.getCreatedTime());

			stmt.executeUpdate();
			stmt.close();

			System.out.println("saved");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<FbPost> list() throws ParseException {
		List<FbPost> posts = new ArrayList<FbPost>();
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			PreparedStatement stmt = connection.prepareStatement(SELECT_SQL);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				posts.add(new FbPost(rs.getString("id"), rs.getString("message"), rs.getDate("created_time")));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;

	}

	public List<FbPost> listByDateInterval(Date since, Date until) throws ParseException {
		List<FbPost> posts = new ArrayList<FbPost>();
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			PreparedStatement stmt = connection.prepareStatement(SELECT_BY_INTERVAL);
			stmt.setDate(1, since);
			stmt.setDate(2, until);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				posts.add(new FbPost(rs.getString("id"), rs.getString("message"), rs.getDate("created_time")));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;
	}

	private void createTable() {
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			PreparedStatement stmt = connection.prepareStatement(CREATE_TABLE);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}