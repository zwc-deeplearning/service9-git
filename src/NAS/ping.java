package NAS;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ping {

    public static boolean ping(String ipAddress) throws Exception {
        int  timeOut =  3000 ;  //超时应该在30钞以上
        boolean temp=false;
        int count =60;

        while (!temp && count>0){
            try{temp=InetAddress.getByName(ipAddress).isReachable(timeOut); } catch (Exception e) {
                 //System.out.println(count);
            }finally {
                Thread.sleep(3000);
                count--;
            }
        }
        System.out.println(temp);
        return temp;
//        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
//        return status;
    }
    public static void main(String[] args) throws Exception {
        //ping("192.168.1.1");
        Map<String,String> map=new HashMap<>();
        map.put("aha","hhhha");
        ThreadLocal<Map> local=new ThreadLocal<>();
        local.set(map);
        Map map1 = local.get();
        System.out.println(map1);
    }
}
