package NAS;

import java.io.IOException;


public class Send2CrAdd {
    String ip,nasIp,aid,ConnCR;
    int ulevel;
    int slevel;
    public Send2CrAdd(String ip,String nasIp,int ulevel,int slevel,String aid,String ConnCR) {
        this.ip=ip;
        this.nasIp=nasIp;
        this.ulevel=ulevel;
        this.slevel=slevel;
        this.aid=aid;
        this.ConnCR=ConnCR;
    }
    public void send(){
        try {
            byte[] ByteFix=converter.Fix();
            byte[] ByteTag=converter.TwoInt2Byte(0,1);
            byte[] ByteIP =converter.ip2Byte(ip);
            byte[] ByteNas=converter.ip2Byte(nasIp);//nas鐨勫湴鍧�
            byte[] ByteLevel=converter.TwoInt2Byte(ulevel,slevel);
            byte[] BytePad=converter.TwoInt2Byte(0,0);
            byte[] ByteAid=aid.getBytes();
            byte[] data = converter.merge(ByteFix,ByteTag,ByteIP,ByteNas,ByteLevel,BytePad,ByteAid);
            new Thread(new UDPSend(ConnCR,2222,data)).start();
            System.out.println("send to" +ConnCR+"successfully (add)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Send2CrAdd("127.0.0.1","127.0.0.1",1,1,"11","127.0.0.1").send();
    }
}

