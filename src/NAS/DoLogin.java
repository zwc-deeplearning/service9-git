package NAS;


import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static NAS.NewOpen.NextNasIp;
import static NAS.NewOpen.neighbours;
import static NAS.getLocalIp.getLinuxConnCR;

public class DoLogin {
//    ResourceBundle rb = ResourceBundle.getBundle("ConnInfo");
//    String ConnCR = rb.getString("ConnCR");

    public static String ConnCR;

    static {
        try {
            ConnCR = getLinuxConnCR();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void insert(String username, String AID, String level, String IP, int sl, String mac) throws Exception {
        DBUtils d = new DBUtils();
        Date date = new Date(System.currentTimeMillis());
        Timestamp timeStamp = new Timestamp(date.getTime());
        Connection conn = d.getConnection();
        PreparedStatement stm1 = null;
        String sql1 = "insert into cacheTable(username,AID,userLevel,IP,serviceLevel,accessDate,mac) values(?,?,?,?,?,?,?)";
        stm1 = conn.prepareStatement(sql1);
        stm1.setString(1, username);
        stm1.setString(2, AID);
        stm1.setString(3, level);
        stm1.setString(4, IP);
        stm1.setInt(5, sl);
        stm1.setTimestamp(6, timeStamp);
        stm1.setString(7, mac);
        int i = stm1.executeUpdate();
        if (i > 0) {
            System.out.println("cache table inserted");
        }

        d.closeAll(null, stm1, conn);
    }

    public void deleteAndSend(ArrayList<String> res) {
        try {
            Runtime run = Runtime.getRuntime();
            DBUtils d = new DBUtils();
            Connection conn = d.getConnection();
            PreparedStatement stm2 = null, stm3 = null;
            ResultSet rs = null;
            String sql2 = "delete from cacheTable where mac=?";
            stm2 = conn.prepareStatement(sql2);
            String sql3 = "select * from cacheTable where mac=?";
            stm3 = conn.prepareStatement(sql3);


            //1.先查到ip地址,数据库取出ip_del的用户信息存储成一条udp
            for (String mac : res) {
                String IP_mv = null;
                stm3.setString(1, mac);
                rs = stm3.executeQuery();//执行sql语句
                if (rs == null) continue;
                while (rs.next()) {
                    String username_mv = rs.getString("username");
                    String AID_mv = rs.getString("AID");
                    int level_mv = rs.getInt("userLevel");
                    IP_mv = rs.getString("IP");
                    int sl_mv = rs.getInt("serviceLevel");
                    String mac_mv = rs.getString("mac");
                    //发送给连接的cr
                    String LocalIp = new getLocalIp().getLinuxLocalIp();
                    //   new Send2CrDel(IP_mv,LocalIp,level_mv,sl_mv,AID_mv,ConnCR).send();
                    sendOther(username_mv, AID_mv, level_mv, IP_mv, sl_mv, mac_mv);
                }

                //2.执行删除rtest和删除数据库中用户信息
                try {
                    run.exec(new String[]{"rtest", "-dm", IP_mv});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stm2.setString(1, mac);//给mac字段指定数据
                int i = stm2.executeUpdate();

                if (i > 0) {
                    System.out.println("ip_mv delete successfully");
                }
                rs = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendOther(String username_mv, String aid_mv, int level_mv, String ip_mv, int sl_mv, String mac_mv) throws IOException {
        DatagramSocket ds = new DatagramSocket();

//      ResourceBundle rb = ResourceBundle.getBundle("ConnInfo");
//      String NextNasIp = rb.getString("NextNasIp");

		/*
		neighbours是链表
		 */
        String[] ips = neighbours;
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < ips.length; i++) {
            final int index = i;
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    InetAddress inet = null;//下一颗卫星的地址
                    try {
                        inet = InetAddress.getByName(ips[index]);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    String data_mv = username_mv + "|" + aid_mv + "|" + level_mv + "|" + ip_mv + "|" + sl_mv + "|" + mac_mv;
                    byte[] data = data_mv.getBytes();
                    DatagramPacket dp = new DatagramPacket(data, data.length, inet, 12308);//下一颗卫星的port
                    try {
                        ds.send(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //只发一次就行
                    System.out.println("send success");
                }
            });
        }


//		InetAddress inet = InetAddress.getByName(NextNasIp);//下一颗卫星的地址
//        boolean temp=true;
//        while(temp)
//        {
//            String data_mv=username_mv+"|"+aid_mv+"|"+level_mv+"|"+ip_mv+"|"+sl_mv+"|"+mac_mv;
//            byte[] data = data_mv.getBytes();
//            DatagramPacket dp = new DatagramPacket(data, data.length, inet, 12308);//下一颗卫星的port
//            ds.send(dp);
//            System.out.println("send success");
//            temp=false;
//        }
    }

    public static String logout(String AID) {

        try {
            Runtime run = Runtime.getRuntime();
            DBUtils d = new DBUtils();
            Connection conn = d.getConnection();
            PreparedStatement stm1, stm2 = null;

            //select ip from aid ,rtest -dm
            String sql1 = "select * from cacheTable where AID=?";
            stm1 = conn.prepareStatement(sql1);
            stm1.setString(1, AID);
            ResultSet rs = stm1.executeQuery();//执行sql语句
            while (rs.next()) {
                String username_mv = rs.getString("username");
                String AID_mv = rs.getString("AID");
                int level_mv = rs.getInt("userLevel");
                String IP_mv = rs.getString("IP");
                int sl_mv = rs.getInt("serviceLevel");
                String mac_mv = rs.getString("mac");
                //发送给连接的cr
                String LocalIp = new getLocalIp().getLinuxLocalIp();
                new Send2CrDel(IP_mv, LocalIp, level_mv, sl_mv, AID_mv, ConnCR).send();

                System.out.println(IP_mv);
                run.exec(new String[]{"rtest", "-dm", IP_mv});
            }


            String sql2 = "delete from cacheTable where AID=?";
            stm2 = conn.prepareStatement(sql2);
            stm2.setString(1, AID);//给mac字段指定数据
            int i = stm2.executeUpdate();
            if (i > 0) {
                System.out.println("logout successfully");
                return "success";
            } else {
                //add func
                return "fail";
            }

        } catch (Exception e) {
            e.printStackTrace();
            //add func
            return "fail";
        }
    }


    public static void main(String[] args) throws Exception {
        DoLogin d = new DoLogin();
        d.logout("123");

//      d.insert("123", "go91XtIGVXusgyez", 1,"192.168.2.2");
//		ArrayList<String> res = new ArrayList<String>();
//		res.add("123456");
//		res.add("54321");
//		d.delete(res);
    }
}
