package NAS.cache;

import static NAS.cache.LocalCache.get;
import static NAS.cache.LocalCache.put;

public class Test1 {
    static int i=1;
    //开发人员二次修改的
    //开发人员三次修改的

    public static void main(String[] args) throws InterruptedException {
        System.out.println(i);
        put("gaga","gaga",2);
        put("jili","gulu");
        System.out.println(get("gaga"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                put("hihi","hihi");
            }
        }).start();
        Thread.sleep(3000);
        System.out.println(get("gaga"));
        System.out.println(get("hihi"));

    }

}
