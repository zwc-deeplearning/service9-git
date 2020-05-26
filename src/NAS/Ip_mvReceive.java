package NAS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static NAS.cache.LocalCache.*;
import static NAS.getLocalIp.getLinuxConnCR;


public class Ip_mvReceive {
    //用来接收其他卫星发过来的用户信息，并发送消息检测，存入数据库和rtest
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    public static void main(String[] args) throws Exception {
//    ResourceBundle rb = ResourceBundle.getBundle("ConnInfo");
//    String ConnCR = rb.getString("ConnCR");
        String ConnCR = getLinuxConnCR();
        //1,创建DatagramSocket对象,并指定端口号
        DatagramSocket ds = new DatagramSocket(12308);
        //2,创建DatagramPacket对象, 创建一个空的仓库
        boolean temp = true;
        final byte[] data = new byte[1024];
        put("Access Terminel","Expire Date");
        new Thread(new Runnable() {
            @Override
            public void run() {
                put("Access Terminel","Expire Date 10s");

                DatagramSocket ds1 = null;
                try {
                    ds1 = new DatagramSocket(12310);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                boolean temp = true;
                final byte[] data = new byte[150];
                while(temp){
                    DatagramPacket dp1 = new DatagramPacket(data, data.length);
                    try {
                        ds1.receive(dp1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //发来了多少数据 getLenth()
                    int l = dp1.getLength();
                    String mac = new String(dp1.getData(), 0, l);
                    //s是发过来的mac地址,存放10s钟
                    put(mac,"1",10);
                }
            }
        }).start();
        while (temp) {
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            InetAddress ipAddress = dp.getAddress();
            String ip = ipAddress.getHostAddress();//获取到了IP地址
            int port = dp.getPort();
            //发来了多少数据 getLenth()
            int l = dp.getLength();
            String s = new String(dp.getData(), 0, l);
            System.out.println(ip + ":" + port + "  sent the packet as follows");
            System.out.println(s);
//            System.out.println(System.currentTimeMillis());
            String[] ss = s.split("\\|");
            System.out.println(ss[3]);
//          ping一下 ip ss[3]
//            boolean connection = new ping().ping(ss[3]);
//            if(connection && !JudgeExist.judge(ss[3])){
//            	System.out.println(System.currentTimeMillis());

            //获取到mac_insert 要判断下是否真的移动过来了
            String mac_insert=ss[5];
            boolean res=false;
                for (int i = 0; i < 6; i++) {
                    try {
                        Thread.sleep( 500); //设置暂停的时间
                        System.out.println("--循环执行第" + (i+1) + "次--");
                        res=containsKey(mac_insert);
                        if(res){
                            if (!JudgeExist.judge(ss[3])) {
                                String username_mv = ss[0];
                                String AID_mv = ss[1];
                                int level_mv = Integer.parseInt(ss[2]);
                                String IP_mv = ss[3];
                                int sl_mv = Integer.parseInt(ss[4]);
                                String mac_mv = ss[5];
                                new DoLogin().insert(username_mv, AID_mv, String.valueOf(level_mv), IP_mv, sl_mv, mac_mv);
                                Runtime run = Runtime.getRuntime();
                                //发送给连接的cr
                                String LocalIp = new getLocalIp().getLinuxLocalIp();
                                System.out.println(System.currentTimeMillis());
                                //new Send2CrAdd(IP_mv,LocalIp,level_mv,sl_mv,AID_mv,ConnCR).send();
                                try {
                                    run.exec(new String[]{"rtest", "-am", IP_mv, AID_mv, String.valueOf(level_mv), String.valueOf(sl_mv)});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }


        //ds.close();
    }
}

