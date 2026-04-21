package com.qf.common.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.qf.config.DBConfig;

public class DbUtils {
	// 从DBConfig获取数据库连接参数
private static final String driverclass = DBConfig.MYSQL_DRIVER;
private static final String dburl = DBConfig.MYSQL_URL;
private static final String username = DBConfig.MYSQL_USER;
private static final String password = DBConfig.MYSQL_PASSWORD;
	// 数据库连接池
	private static DruidDataSource ds;
	private static Connection conn;
	static {
		try {
			ds = new DruidDataSource();
			ds.setDriverClassName(driverclass);
			ds.setUrl(dburl);
			ds.setUsername(username);
			ds.setPassword(password);
			ds.setTimeBetweenEvictionRunsMillis(6000);
			ds.setInitialSize(5);
			ds.setMaxActive(20);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("数据库连接池初始化失败：" + e.getMessage());
		}
	}

	// 执行非查询语句
	public static int executeUpdate(String sql, Object... vlus) {
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0; i < vlus.length; i++) {
				ps.setObject(i + 1, vlus[i]);
			}
			int r = ps.executeUpdate();
			conn.close();
			return r;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	// 带事务 执行多条语句
	// 执行非查询语句
	/**
	 * 开启事务执行多条SQL语句
	 * 
	 * @param sqls
	 *            需要执行的sql语句 顺序和参数一一对应 如果没参数那是也需要添加一个null对象
	 * @param vals
	 *            对应sql语句的参数的值 不存在就是null
	 * @return 1 成功 0 失败
	 */
	public static int executeUpdateBatch(List<String> sqls, List<List<Object>> vals) {
		try {
			conn = ds.getConnection();
			try {
				// 开启事务
				conn.setAutoCommit(false);
				for (int i = 0; i < sqls.size(); i++) {
					//System.out.println(sqls.get(i) + "--->" + vals.get(i).size());
					PreparedStatement ps = conn.prepareStatement(sqls.get(i));
					if (vals.get(i) != null) {
						for (int j = 0; j < vals.get(i).size(); j++) {
							ps.setObject(j + 1, vals.get(i).get(j));
						}
					}
					ps.executeUpdate();
				}
				// 提交事务
				conn.commit();
				return 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					// 回滚事务
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return 0;
	}

	// 执行查询语句
	public static ResultSet executeQuery(String sql, Object... vlus) {
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0; i < vlus.length; i++) {
				ps.setObject(i + 1, vlus[i]);
			}
			return ps.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 查询的二次封装返回泛型 单个对象
	public static <T> T querySingle(String sql, Class<T> clz, Object... vlus) {
		try {
			// 获取数据库连接对象
			conn = ds.getConnection();
			// 获取操作sql对象 防止SQL注入
			PreparedStatement ps = conn.prepareStatement(sql);
			// 赋值 设置参数
			for (int i = 0; i < vlus.length; i++) {
				ps.setObject(i + 1, vlus[i]);
			}
			// 执行查询
			ResultSet rs = ps.executeQuery();
			// 从ResultSet中获取数据
			if (rs.next()) {
				// 通过反射创建类的对象
				T obj = clz.newInstance();
				// 获取类中所有的属性
				Field[] arrf = clz.getDeclaredFields();
				// 遍历属性
				for (Field f : arrf) {
					/*System.out.println(f.getName()+"-222--->"+rs.findColumn(f.getName()));
					if (rs.findColumn(f.getName()) > 0) {*/
						// 设置忽略访问校验
						f.setAccessible(true);
						try {
							if (f.getName().equals("price") || f.getName().equals("money")) {
								f.set(obj, rs.getLong(f.getName()) / 100.0);
							} else {
								// 赋值
								f.set(obj, getV(rs, f.getName()));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					/*}*/
				}
				// 关闭
				rs.close();
				ps.close();
				conn.close();

				return obj;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 根据数据库表的字段的数据类型 自动类型转换
	public static Object getV(ResultSet rs, String name) {
		ResultSetMetaData ma;
		Object res = null;
		try {
			ma = rs.getMetaData();

			String type = ma.getColumnTypeName(rs.findColumn(name));
			//System.out.println(type);
			switch (type) {
			case "DATE":
				Date date1 = rs.getDate(name);
				res = new SimpleDateFormat("yyyy-MM-dd").format(date1);
				break;
			case "DATETIME":
				Date date2 = rs.getDate(name);
				res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2);
				break;
			/*
			 * case "INT": res=rs.getInt(name); break;
			 */
			default:
				res = rs.getObject(name);
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}

	// 查询的二次封装返回泛型 集合
	// 查询的二次封装返回泛型 单个对象
	public static <T> List<T> queryList(String sql, Class<T> clz, Object... vlus) {
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0; i < vlus.length; i++) {
				ps.setObject(i + 1, vlus[i]);
			}
			ResultSet rs = ps.executeQuery();
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				// 通过反射创建类的对象
				T obj = clz.newInstance();
				// 获取类中所有的属性
				Field[] arrf = clz.getDeclaredFields();
				// 遍历属性
				for (Field f : arrf) {
					/*System.out.println(f.getName()+"---333---->"+rs.findColumn(f.getName()));
					if (rs.findColumn(f.getName()) > 0) {*/
						// 设置忽略访问校验
						f.setAccessible(true);
						// 赋值
						try {
							if (f.getName().equals("price") || f.getName().equals("money")) {
								f.set(obj, rs.getLong(f.getName()) / 100.0);
							} else {
								// 赋值
								f.set(obj, getV(rs, f.getName()));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
				//}

				list.add(obj);
			}
			rs.close();
			ps.close();
			conn.close();
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 查询数量 泛型整型

	// 获取数据库连接的公共方法
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	// 关闭连接的公共方法
	public static void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rs) {
		try {
			rs.close();
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
