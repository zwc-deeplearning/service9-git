package NAS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JudgeExist {
    public static boolean judge(String ip) throws SQLException {
        DBUtils d =new DBUtils();
        Connection conn = d.getConnection();
        PreparedStatement stm=null;
        String sql="select count(IP) from cacheTable where IP=?";
        //select count(IP) from table where IP =
        stm = conn.prepareStatement(sql);
        stm.setString(1, ip);
        ResultSet rs = stm.executeQuery();//执行sql语句
        int i=0;
        while(rs.next()){
            i = rs.getInt("count(IP)");
        }
        if(i==0) return false;
        else {
            System.out.println("This user already exists");
            return true;
        }
    }

    public static void main(String[] args) throws SQLException {
        if(JudgeExist.judge("127.0.0.1")){
            //说明之前存在，忽略此条数据
        }else {
            //之前不存在，开始插入cacheTable
        }
    }
}
