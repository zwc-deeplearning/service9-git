package NAS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CaluteLength extends Thread{
    //以Socket为成员变量
    private Socket socket;
    public CaluteLength(Socket socket){this.socket=socket;}

    @Override
    public void run(){
        try {
            //获取Socket输出流
            OutputStream outputStream=socket.getOutputStream();
            //获取输入流
            InputStream inputStream=socket.getInputStream();
            int ch=0;
            byte[] buff=new byte[1024];
            //buff用来读取输入的内容,ch用来获取数组长度
            ch=inputStream.read(buff);
            String content=new String(buff,0,ch);//把字节流转为字符串
            System.out.println(content);
            String res=DoLogin.logout(content);
            System.out.println("这是客户端:"+content+":");
            outputStream.write(new String(res).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
