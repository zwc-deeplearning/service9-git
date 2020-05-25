package NAS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LinuxRuntime {
    private static String open_ip;
    private static String nas_pw;
    private static String open_pw;
    static{
        ResourceBundle rb=ResourceBundle.getBundle("open");
        open_ip = rb.getString("open_ip");
        open_pw= rb.getString("open_pw");
    }

    public static void openwrt(){
        Runtime run = Runtime.getRuntime();
        try{
            Process process = run.exec(new String[]{"ssh","192.168.9.1","\n",});
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            List<String> list = new ArrayList<String>();
            String result = null;
            while ((result = bs.readLine()) != null) {
               System.out.println("job result [" + result + "]");
            }
            in.close();
            process.destroy();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        openwrt();
    }
}
