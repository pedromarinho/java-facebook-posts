package br.com.pedro.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.pedro.model.Post;
import br.com.pedro.utils.Utils;

public class PostDAO {

	private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS posts " + "(id text NOT NULL, "
			+ "message text, " + "created_time date, " + "CONSTRAINT pk_id PRIMARY KEY (id))";
	private final String SELECT_SQL = "SELECT * FROM posts";
	private final String INSERT_SQL = "INSERT INTO posts (id, message, created_time) VALUES (?, ?, ?)";

	public PostDAO() {
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
				"postgres", "postgres")) {
			createTable();
			System.out.println("connected");
		} catch (SQLException e) {
			System.out.println("Erro ao conectar ao BD");
			new RuntimeException("Erro ao conectar ao BD");
		}
	}

	public void save(Post post) {
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
				"postgres", "postgres")) {
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

	public List<Post> list() throws ParseException {
		List<Post> posts = new ArrayList<Post>();
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
				"postgres", "postgres")) {
			PreparedStatement stmt = connection.prepareStatement(SELECT_SQL);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				posts.add(new Post(rs.getString("id"), rs.getString("message"),
						Utils.formatToSqlDate(rs.getString("created_time"), "yyyy-MM-dd")));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;

	}

	private void createTable() {
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
				"postgres", "postgres")) {
			PreparedStatement stmt = connection.prepareStatement(CREATE_TABLE);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}