package br.com.pedro;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Date;
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

import br.com.pedro.dao.FbPostDAO;
import br.com.pedro.model.FbPost;
import br.com.pedro.utils.Utils;

public class TestFbPostDAO {
	
	JdbcDatabaseTester jdt;
	FbPostDAO fbPostDAO;

	@Before
	public void setUp() throws Exception {
		jdt = new JdbcDatabaseTester("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres",
				"postgres");
		this.fbPostDAO = new FbPostDAO();
		jdt.setDataSet(new FlatXmlDataSetBuilder().build(new File("dataSet.xml")));
		jdt.onSetup();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void listAll() throws ParseException {
		List<FbPost> posts = fbPostDAO.list();
		assertEquals(11, posts.size());
		assertEquals("000000000000000_1111111111111111", posts.get(0).getId());
		assertEquals("post2 message", posts.get(1).getMessage());
		assertEquals("2017-09-18", posts.get(2).getCreatedTime().toString());
	}
	
	@Test
	public void insert() throws SQLException, Exception {
		FbPost post = new FbPost("000000000000000_1111111111111122", "post12 message",
				Utils.formatToSqlDate("2017-09-29", "yyyy-MM-dd"));
		
		fbPostDAO.save(post);

		// Fetch database data after executing your code
		IDataSet databaseDataSet = jdt.getConnection().createDataSet();
		ITable actualTable = databaseDataSet.getTable("posts");

		// Load expected data from an XML dataset
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("expectedDataSet.xml"));
		ITable expectedTable = expectedDataSet.getTable("posts");

		// Assert actual database table match expected table
		Assertion.assertEquals(expectedTable, actualTable);
	}
	
	@Test
	public void listByInterval() throws ParseException {
		Date since = Utils.formatToSqlDate("2017-09-17", "yyyy-MM-dd");
		Date until = Utils.formatToSqlDate("2017-09-18", "yyyy-MM-dd");
		
		List<FbPost> posts = fbPostDAO.listByDateInterval(since, until);
		
		assertEquals(3, posts.size());
		assertEquals("000000000000000_1111111111111112", posts.get(0).getId());
		assertEquals("post3 message", posts.get(1).getMessage());
		
		fbPostDAO.save(new FbPost("000000000000000_1111111111111123", "post13 message",
				Utils.formatToSqlDate("2017-09-18", "yyyy-MM-dd")));
		
		posts = fbPostDAO.listByDateInterval(since, until);
		
		assertEquals(4, posts.size());
	}

}