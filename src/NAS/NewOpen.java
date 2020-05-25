package NAS;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NewOpen {
    static String NextNasIp;
    static String[] neighbours;
    public static void main(String[] args) throws IOException, InterruptedException {
        NextNasIp=System.getProperty("nextIp");
        if(NextNasIp==null){
            NextNasIp="127.0.0.1";
        }
        /*
        新添加了重启openwrt命令,删除本地ar日志
         */
        /*
        NextNasIp nextIp=10.0.0.1;10.0.0.2;10.0.0.3 传入一个带;号的字符串
         */
        neighbours = NextNasIp.split(";");

        Runtime run = Runtime.getRuntime();
        //run.exec(new String[]{"echo",">","/var/log/devicelog/OpenWrt/hostapd.log"});
        clearInfoForFile("/var/log/devicelog/OpenWrt/hostapd.log");
        run.exec(new String[]{"systemctl","restart","rsyslog"});
        System.out.println("restart openwrt log");
        
        LogVIew.LogView view = new LogVIew.LogView();
        final File tmpLogFile = new File("/var/log/devicelog/OpenWrt/hostapd.log");
        view.realtimeShowLog(tmpLogFile);
    }
    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
