package NAS;

import java.io.IOException;

public class Switch {
    public static void main(String[] args) throws IOException {
    	Runtime run = Runtime.getRuntime();
    	run.exec("iwconfig wlan0 essid \"SAT4\"");
    	System.out.println(System.currentTimeMillis());
    }
}
