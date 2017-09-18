package br.com.pedro;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.pedro.dao.PostDAO;
import br.com.pedro.model.Post;
import br.com.pedro.utils.Utils;

public class TestPostDAO {
	
	JdbcDatabaseTester jdt;
	PostDAO postDao;

	@Before
	public void setUp() throws Exception {
		jdt = new JdbcDatabaseTester("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres",
				"postgres");
		this.postDao = new PostDAO();
		jdt.setDataSet(new FlatXmlDataSetBuilder().build(new File("dataSet.xml")));
		jdt.onSetup();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void listAll() throws ParseException {
		List<Post> posts = postDao.list();
		assertEquals(4, posts.size());
		assertEquals("000000000000000_1111111111111111", posts.get(0).getId());
		assertEquals("post2 message", posts.get(1).getMessage());
		assertEquals("2017-09-18", posts.get(2).getCreatedTime().toString());
	}
	
	@Test
	public void insert() throws SQLException, Exception {
		Post post = new Post("000000000000000_1111111111111115", "post5 message",
				Utils.formatToSqlDate("2017-09-20", "yyyy-MM-dd"));
		
		postDao.save(post);

		// Fetch database data after executing your code
		IDataSet databaseDataSet = jdt.getConnection().createDataSet();
		ITable actualTable = databaseDataSet.getTable("posts");

		// Load expected data from an XML dataset
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("expectedDataSet.xml"));
		ITable expectedTable = expectedDataSet.getTable("posts");

		// Assert actual database table match expected table
		Assertion.assertEquals(expectedTable, actualTable);
	}

}
