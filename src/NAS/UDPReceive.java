package NAS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPReceive {
    int port;
    public UDPReceive(int port){
        this.port=port;
    }
    public void Receive() throws IOException {
        //1,创建DatagramSocket对象,并指定端口号
            DatagramSocket ds = new DatagramSocket(12306);
            byte[] data = new byte[1024];
            byte[] data1;
            while(true) {
                DatagramPacket dp = new DatagramPacket(data, data.length);
                ds.receive(dp);
                InetAddress ipAddress = dp.getAddress();
                String ip = ipAddress.getHostAddress();//获取到了IP地址
                //发来了什么数据  getData()
                int port = dp.getPort();
                //发来了多少数据 getLenth()
                int length = dp.getLength();
                data1 = dp.getData();
                System.out.println(ip + ":" + port+"  sent the packet as returning");
            }
    }

    public static void main(String[] args) throws IOException {
        new UDPReceive(12306).Receive();
//        //1,创建DatagramSocket对象,并指定端口号
//        DatagramSocket ds = new DatagramSocket(12306);
//        //2,创建DatagramPacket对象, 创建一个空的仓库
//        byte[] data = new byte[1024];
//        while(true) {
//            DatagramPacket dp = new DatagramPacket(data, data.length);
//            //3,接收数据存储到DatagramPacket对象中
//            ds.receive(dp);
//            //4,获取DatagramPacket对象的内容
//            //谁发来的数据  getAddress()
//            InetAddress ipAddress = dp.getAddress();
//            String ip = ipAddress.getHostAddress();//获取到了IP地址
//            //发来了什么数据  getData()
//            int port = dp.getPort();
//            //发来了多少数据 getLenth()
//            int length = dp.getLength();
//            System.out.println(ip + ":" + port+"  sent the packet as follows");
//            for (int i=0;i<length;i++){
//                System.out.print(Integer.toHexString(data[i] & 0xff)+" ");
//            }
//
//        }
        //ds.close();
    }
}

