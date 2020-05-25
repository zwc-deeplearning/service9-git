package NAS;

import jdk.management.resource.ResourceRequest;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * 采用ThreadLocal封装Connection
 *
 * @author Administrator
 *
 */
public class ConnectionManager {
    public static void main(String[] args) throws SQLException {
        //listenning 开启了个线程认证一个线程，切换一个
        Connection connection = getConnection();//set +get 到了

        ConnectionManager.beginTransaction(connection);
        ConnectionManager.commitTransaction(connection);
        //这两个东西是有时候会用到有时候用不到的。

        PreparedStatement ps = connection.prepareStatement("");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){};
        close(rs);
        close(ps);
        close(connection);
        closeConnection();
    }

    //定义ThreadLocal静态变量，确定存取类型为Connection
    public static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();
    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        //如果在当前线程中没有绑定相应的Connection
        if (conn == null) {
            try {
                //这个东西要和dbinfo.properties 连起来
                ResourceBundle rb = ResourceBundle.getBundle("dbinfo");
                Class.forName(rb.getString("driverClass"));
                String url = rb.getString("url");
                String username = rb.getString("user");
                String password = rb.getString("password");
                conn = DriverManager.getConnection(url, username, password);
                //将Connection设置到ThreadLocal
                connectionHolder.set(conn);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    /**
     * 关闭数据库连接方法
     * @return
     */
    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
                //从ThreadLocal中清除Connection
                connectionHolder.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭数据库连接方法
     * @return
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs ) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 事务开启
     * @return
     */
    public static void beginTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (conn.getAutoCommit()) {
                    conn.setAutoCommit(false); //手动提交
                }
            }
        }catch(SQLException e) {}
    }

    /**
     * 事务提交
     * @return
     */
    public static void commitTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.commit();
                }
            }
        }catch(SQLException e) {}
    }

    /**
     * 事务回滚
     * @return
     */
    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            }
        }catch(SQLException e) {}
    }
}
