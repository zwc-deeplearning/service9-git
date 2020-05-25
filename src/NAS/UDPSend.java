package NAS;

import java.io.IOException;
import java.net.*;


public class UDPSend implements Runnable{
    String ip;
    int port;
    byte[] data;

    public UDPSend(String ip,int port,byte[] data){
        this.ip=ip;
        this.port=port;
        this.data=data;

    }
    public void run(){

        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            InetAddress inet = InetAddress.getByName(ip);
            DatagramPacket dp = new DatagramPacket(data, data.length, inet, port);
            ds.send(dp);
            System.out.println("send to "+ip+":"+port+" successfully");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ds.close();
    }
    public static void main(String[] args) throws IOException {
        new Thread(new UDPSend("127.0.0.1",12308,"haha|1111aaaa1111aaaa|1|192.168.1.1".getBytes())).start();
//        DatagramSocket ds = new DatagramSocket();
//        //映射服务器地址
//        InetAddress inet = InetAddress.getByName("127.0.5.1");
//        int ulevel=1,slevel=1;
//        String ip="192.168.1.1";
//        String aid="1111aaaa1111aaaa";
//        boolean temp=true;

//        while(temp)
//        {
//            //获取收到的ip string ，aid string ，服务等级，用户等级，将要发送的nas的ip地址
//            byte[] ByteFix=converter.Fix();
//            byte[] ByteTag=converter.TwoInt2Byte(0,1);
//            byte[] ByteIP =converter.ip2Byte(ip);
//            byte[] ByteNas=converter.ip2Byte(inet.getHostAddress());//nas的地址
//            byte[] ByteLevel=converter.TwoInt2Byte(ulevel,slevel);
//            byte[] BytePad=converter.TwoInt2Byte(0,0);
//            //byte[] ByteAid=res.get(2).getBytes();
//            byte[] ByteAid=aid.getBytes();
//            byte[] data = converter.merge(ByteFix,ByteTag,ByteIP,ByteNas,ByteLevel,BytePad,ByteAid);
//            DatagramPacket dp = new DatagramPacket(data, data.length, inet, 12306);
//            ds.send(dp);
//            temp=false;
////        }
        //ds.close();
    }
}

