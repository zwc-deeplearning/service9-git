package NAS;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class getLocalIp {

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    public static String getLinuxLocalIp() throws SocketException {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                         enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();

                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
//                            System.out.println(ipaddress);
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:")
                                    && !ipaddress.contains("fe80") && !ipaddress.contains(":") && ipaddress.contains("172")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("获取ip地址异常");
            ex.printStackTrace();
        }
        return ip;
    }
    public static String getLinuxConnCR() throws SocketException {
        String s;
        s=getLinuxLocalIp();//172.16.2.1
        s = s.substring(0,s.length()-1)+"2";
        return s;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(getLinuxLocalIp());
        System.out.println(getLinuxConnCR());
    }



}
