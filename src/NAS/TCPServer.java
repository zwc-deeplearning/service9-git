package NAS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        //创建socket，绑定到65000端口
        ServerSocket serverSocket=new ServerSocket(65002);
        //循环用以监听
        while (true){//这是多线程的，通过循环创建多个socket来实现
            //监听65000端口，直到有客户端信息发过来
            Socket socket=serverSocket.accept();
            //执行相关操作
            new CaluteLength(socket).start();
        }
    }
}
