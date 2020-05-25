package NAS;

import java.util.concurrent.*;

public class Test3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadpool = Executors.newSingleThreadExecutor();
        Future<String> future = threadpool.submit(new CallableTest());
        String s = future.get();

    }
    public static class CallableTest implements Callable<String>{

        @Override
        public String call() throws Exception {
            return "helllo";
        }
    }
}
