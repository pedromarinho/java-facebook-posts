package br.com.pedro;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import com.restfb.json.JsonArray;

public class TestPostRetriever {

	JdbcDatabaseTester jdt;

	@Before
	public void setUp() throws Exception {
		jdt = new JdbcDatabaseTester("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres",
				"postgres");
		jdt.setDataSet(new FlatXmlDataSetBuilder().build(new File("dataSet.xml")));
		jdt.onSetup();
	}

	@Test
	public void testGetPosts() throws ParseException {
		String expected = "[{\"id\":\"000000000000000_1111111111111112\",\"message\":\"post2 message\",\"created_time\":\"2017-09-17\"},"
				+ "{\"id\":\"000000000000000_1111111111111113\",\"message\":\"post3 message\",\"created_time\":\"2017-09-18\"},"
				+ "{\"id\":\"000000000000000_1111111111111114\",\"message\":\"post4 message\",\"created_time\":\"2017-09-19\"},"
				+ "{\"id\":\"000000000000000_1111111111111115\",\"message\":\"post5 message\",\"created_time\":\"2017-09-19\"},"
				+ "{\"id\":\"000000000000000_1111111111111118\",\"message\":\"post8 message\",\"created_time\":\"2017-09-17\"}]";

		JsonArray result = PostRetriever.getPosts("20170917", "20170919");

		assertEquals(expected, result.toString());

		expected = "[{\"id\":\"000000000000000_1111111111111114\",\"message\":\"post4 message\",\"created_time\":\"2017-09-19\"},"
				+ "{\"id\":\"000000000000000_1111111111111115\",\"message\":\"post5 message\",\"created_time\":\"2017-09-19\"},"
				+ "{\"id\":\"000000000000000_1111111111111119\",\"message\":\"post9 message\",\"created_time\":\"2017-09-21\"},"
				+ "{\"id\":\"000000000000000_1111111111111120\",\"message\":\"post10 message\",\"created_time\":\"2017-09-21\"}]";

		assertEquals(expected, PostRetriever.getPosts("20170919", "20170921").toString());
		
		assertEquals("[]", PostRetriever.getPosts("20170910", "20170915").toString());
	}

	@Test
	public void testGetVolume() throws ParseException {
		String expected = "[{\"date\":\"20170917\",\"sum_posts\":\"2\"},"
				+ "{\"date\":\"20170918\",\"sum_posts\":\"1\"}," + "{\"date\":\"20170919\",\"sum_posts\":\"2\"},"
				+ "{\"date\":\"20170920\",\"sum_posts\":\"0\"}]";

		JsonArray result = PostRetriever.getVolume("20170917", "20170920");
		assertEquals(expected, result.toString());

		expected = "[{\"date\":\"20170916\",\"sum_posts\":\"3\"}]";
		assertEquals(expected, PostRetriever.getVolume("20170916", "20170916").toString());
		
		expected = "[{\"date\":\"20170910\",\"sum_posts\":\"0\"}]";
		assertEquals(expected, PostRetriever.getVolume("20170910", "20170910").toString());
	}
}