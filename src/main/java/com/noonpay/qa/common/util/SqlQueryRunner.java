package com.noonpay.qa.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

public class SqlQueryRunner {

	private static SqlQueryRunner instance;

	private Connection connection;

	private SqlQueryRunner() {
		connection = ApplicationDatabaseMysql.getConnection();
	}

	public static SqlQueryRunner getInstance() {
		if (instance == null) {
			synchronized (SqlQueryRunner.class) {
				if (instance == null) {
					instance = new SqlQueryRunner();
				}

			}
		}

		return instance;
	}

	public List<Map<String, Object>> selectQuery(String sql) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(sql);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();

			String[] columnNames = new String[columnCount];
			String[] columnTypes = new String[columnCount];

			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = metaData.getColumnName(i+1);
				columnTypes[i] = metaData.getColumnTypeName(i+1);
			}

			while (resultSet.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					rowMap.put(columnNames[i], getRowData(resultSet, columnNames[i], columnTypes[i]));
				}
				data.add(rowMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return CollectionUtils.isEmpty(data) ? null : data;
	}

	private Object getRowData(ResultSet resultSet, String column, String type) throws SQLException {
		Object data = null;
		switch (type) {
		case "VARCHAR":
			data = resultSet.getString(column);
			break;
		case "BOOLEAN":
			data = resultSet.getBoolean(column);
			break;
		case "INTEGER":
			data = resultSet.getInt(column);
			break;
		case "BIGINT":
			data = resultSet.getLong(column);
			break;
		case "DECIMAL":
			data = resultSet.getDouble(column);
			break;
		case "CHAR":
			data = resultSet.getString(column);
			break;
		default:
			break;
		}

		return data;
	}

}
