package NAS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

import static NAS.getLocalIp.getLinuxConnCR;

public class Listenning {

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

    public static void listenning() throws Exception{


            Runtime run = Runtime.getRuntime();
            Process process = run.exec(new String[]{"radiusd","-X"});
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            String result=null;
            String tmp=null;
            HashSet<String> set = new HashSet<String>();
            List<String> res = new ArrayList<String>();
            DatagramSocket ds = new DatagramSocket();

        while (true){
                while ((result = bs.readLine()) != null) {
                    tmp = result;
                    System.out.println(result);
                    String pattern_user =".*Adding.Stripped-User-Name.*";
                    String pattern_AID = ".*WISPr-Location-ID.*";
                    String pattern_mac = ".*WISPr-Redirection-URL.*";
                    String pattern_result = "....Received.Access-.*";
                    String pattern_IP=".*Received Access-Request.*";
                    addAttribution(tmp, pattern_IP,res,set,6);
                    addAttribution(tmp, pattern_mac, res, set,-1);
                    addAttribution(tmp, pattern_AID, res, set,-1);
                    addAttribution(tmp, pattern_user, res, set,-1);

                    if (tmp.matches(pattern_result)){
                        String sl[] = tmp.split(" ");
                        tmp = sl[2];
                        if(!tmp.equals("Access-Request")){
                            System.out.println("[jobs]"+tmp);
                            set.add(tmp);
                            res.add(tmp);
                        }
                    }

                    // System.out.println("size="+res.size());
                    if (res.size()==5&&res.get(4).equals("Access-Accept")){
                        String a = res.get(2);
                        //AID contains the user & service level
                        String ulevel = a.charAt(a.length()-2)+"";
                        int slevel = 0;
                        String IP = res.get(0).split(":")[0];

                        if(!JudgeExist.judge(IP)){

                        DoLogin d=new DoLogin();
                        d.insert(res.get(3),res.get(2), ulevel,IP,slevel,res.get(1));

                        //send udp to trigger
                        String LocalIp= new getLocalIp().getLinuxLocalIp();
                        new Send2CrAdd(IP,LocalIp,Integer.valueOf(ulevel),slevel,res.get(2),ConnCR).send();

                        //User information is  accessDate

                        try {
                            run.exec(new String[]{"rtest","-am",IP,res.get(2),String.valueOf(ulevel),String.valueOf(slevel)});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      }
                        set.clear();
                    }
                    if(res.size()==5||(res.size()>4&&res.get(4).equals("Access-Reject"))){
                        res=new ArrayList<String>();
                        set.clear();
                    }

                }
            }




    }
    public static void addAttribution(String tmp,String pattern,List<String>res,HashSet<String> set,int index){
        if (tmp.matches(pattern)){
            String sl[] = tmp.split(" ");
            int len = index;
            if(index==-1){
                index=sl.length-1;
            }
            tmp = sl[index];
            tmp=tmp.replace("\"", "");
            if(!set.contains(tmp)){
                System.out.println("[jobs:]"+tmp);
                set.add(tmp);
                res.add(tmp);
            }
        }
    }

    public static String toHexString(String s) {
           String str = "";
           for (int i = 0; i < s.length(); i++) {
                 int ch = (int) s.charAt(i);
                 String s4 = Integer.toHexString(ch);
                 str = str + s4;
                }
            return str;
    }

    public static void main(String[] args) throws Exception{
        listenning();
    }

}




