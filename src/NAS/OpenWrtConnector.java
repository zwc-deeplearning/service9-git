package NAS;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;



public class OpenWrtConnector {
    private static String open_ip;
    private static String open_uname;
    private static String open_pw;
    private static String cmd;
    private static String cmd0;
    private static String interval;
    private static int sequence=0;
    private static ArrayList<String> res;
    static{
        ResourceBundle rb=ResourceBundle.getBundle("open");
        open_ip = rb.getString("open_ip");
        open_uname = rb.getString("open_uname");
        open_pw= rb.getString("open_pw");
        cmd0=rb.getString("cmd0");
        cmd =rb.getString("cmd");
        interval=rb.getString("interval");
    }
 // 这块注释了  static String NextNasIp;

    public static void main(String[] args) throws Exception{
//   这块注释了     NextNasIp=System.getProperty("nextIp");
//        if(NextNasIp==null){
//            NextNasIp="127.0.0.1";
//        }
    res = new ArrayList<String>();
        init();
        while(true){
            sequence=getSyslog(sequence,res);
            System.out.println("begin:"+sequence);
            Thread.sleep(Integer.valueOf(interval));
            DoLogin d=new DoLogin();
            d.deleteAndSend(res);
            res=new ArrayList<String>();
            Thread.sleep(500);
        }
    }

    public static void init()  {
        String hostname =open_ip;
        String username=open_uname;
        String password=open_pw;
        try {
            //建立连接
            Connection conn = new Connection(hostname);
            //     System.out.println("set up connections");
            conn.connect();
            //利用用户名和密码进行授权
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if (isAuthenticated == false) {
                //       System.out.println("--------");
                throw new IOException("Authorication failed");
            }
            //打开会话
            Session sess = conn.openSession();
            //    System.out.println("cmd----");
            //执行命令OpenWrtConnector
//            sess.execCommand(cmd);


            sess.execCommand(cmd0);
            sess.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getSyslog(int sequence,ArrayList<String> res) {
        int seq=0;
        String hostname =open_ip;
        String username=open_uname;
        String password=open_pw;
        try{
            //建立连接
            Connection conn= new Connection(hostname);
            //     System.out.println("set up connections");
            conn.connect();
            //利用用户名和密码进行授权
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if(isAuthenticated ==false)
            {
                //       System.out.println("--------");
                throw new IOException("Authorication failed");
            }
            //打开会话
            Session sess = conn.openSession();
            //    System.out.println("cmd----");  
            //执行命令OpenWrtConnector
//            sess.execCommand(cmd);


            sess.execCommand(cmd);
            //     System.out.println("The execute command output is:");
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while(true)
            {
                String line = br.readLine();
                String pattern_mac = ".*AP.STA.DISCONNECTED.*";
                String pattern_seq=".*authpriv.notice dropbear.*";
                //if ()
                String tmp = line;
               if (tmp!=null&&tmp.matches(pattern_seq)){
                  //54 System.out.println(tmp);
                    String sl1[] = tmp.split(" ");
                  // System.out.println(sl1[7]);
                   //6 to 7
                    String sl2[] =sl1[6].split("\\[|\\]");


                    seq = Integer.valueOf(sl2[1]);
                    System.out.println(seq);
                }

               if (seq>=sequence&&tmp!=null&&tmp.matches(pattern_mac)){
                    String sl1[] = tmp.split(" ");
                    //9 to 10
                    String mac = sl1[9];
                    res.add(mac);
                    System.out.println(mac);

                }

                if(line==null) break;
              // System.out.println(line);
            }
            //   System.out.println("Exit code "+sess.getExitStatus());
            sess.close();
            conn.close();
            //     System.out.println("Connection closed");

        }catch(IOException e)
        {
            System.out.println("can not access the remote machine");
        }
        System.out.println("end");
        return seq;
    }


}  