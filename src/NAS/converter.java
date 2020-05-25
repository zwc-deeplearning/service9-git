package NAS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class converter {
    public static byte[] TwoInt2Byte(int i,int j)
    {
        byte[] res=new byte[2];
        res[0]=(byte)i;
        res[1]=(byte)j;
        return res;
    }

    public static byte[] ip2Byte(String ip) {
        byte[] res=new byte[4];
        String[] ips = ip.split("\\.");
        for(int i=0;i<ips.length;i++)
        {
            int k=Integer.valueOf(ips[i]);
            res[i]=(byte)k;
        }
        return res;
    }
    public static byte[] Fix() {
        byte[] res=new byte[6];
        res[0]=(byte)35;
        res[1]=(byte)0;
        res[2]=(byte)0;
        res[3]=(byte)0;
        res[4]=(byte)0;
        res[5]=(byte)2;
        return res;
    }

    public static byte[] merge(byte[] byteFix, byte[] byteTag, byte[] byteIP, byte[] byteNas, byte[] byteLevel, byte[] bytePad, byte[] byteAid) throws IOException {
        ByteArrayOutputStream os= new ByteArrayOutputStream();
        os.write(byteFix);
        os.write(byteTag);
        os.write(byteIP);
        os.write(byteNas);
        os.write(byteNas);
        os.write(byteLevel);
        os.write(bytePad);
        os.write(byteAid);
        return os.toByteArray();
    }
}
