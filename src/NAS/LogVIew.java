package NAS;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogVIew {

    public static class LogView {
        private long lastTimeFileSize = 0; //上次文件大小
        String pattern_mac = ".*AP-STA-DISCONNECTED.*";
        String pattern_mac1=".*AP-STA-CONNECTED.*";
        /**
         * 实时输出日志信息
         * @param logFile 日志文件
         * @throws IOException
         */
        public void realtimeShowLog(File logFile) throws IOException {
            //指定文件可读可写
            final RandomAccessFile randomFile = new RandomAccessFile(logFile,"rw");
            //启动一个线程每10秒钟读取新增的日志信息
            ScheduledExecutorService exec =
                    Executors.newScheduledThreadPool(1);
            exec.scheduleWithFixedDelay(new Runnable(){
                public void run() {
                    try {
                        //获得变化部分的
                        randomFile.seek(lastTimeFileSize);
                        String tmp = "";
                        ArrayList<String> res=new ArrayList<>();
                        String conMac="";

                        while( (tmp = randomFile.readLine())!= null) {
                            //System.out.println(new String(tmp.getBytes("ISO8859-1")));
                            String s=new String(tmp.getBytes("ISO8859-1"));
                            if(s!=null&&s.matches(pattern_mac)){
                                String sl1[] = tmp.split(" ");
                                //9 to 10
                                String mac = sl1[sl1.length-1];
                                res.add(mac);
                                System.out.println("Disconnected"+mac);
                               // System.out.println(System.currentTimeMillis());
                            }

                            if(s!=null&&s.matches(pattern_mac1)){
                                String sl1[] = tmp.split(" ");
                                //9 to 10
                                String mac = sl1[sl1.length-1];
                                conMac=mac;
                                System.out.println("Connected"+conMac);
                                // System.out.println(System.currentTimeMillis());
                            }
                        }
                        //res 里存了mac
                        if(res.size()!=0){
                            DoLogin d=new DoLogin();
                            d.deleteAndSend(res);
                        }
                        if(conMac!=""){
                            //conDevice 是接入的mac地址
                            new Thread(new UDPSend("127.0.0.1",12310,conMac.getBytes())).start();
                        }

                        lastTimeFileSize = randomFile.length();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            
        }

        public static void main(String[] args) throws Exception {
            LogView view = new LogView();
            final File tmpLogFile = new File("D:\\google_download\\weather_mini");
            view.realtimeShowLog(tmpLogFile);
        }

    }
}
