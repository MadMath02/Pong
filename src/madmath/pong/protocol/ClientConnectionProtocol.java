package madmath.pong.protocol;

import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientConnectionProtocol {
	public static void dataSend(PrintWriter out, boolean[] in) {
		try {
			if((in[0]&&in[1])||(!in[0]&&!in[1])) {
				out.println("null");
			}else if(in[0]) {
				out.println("up");
			}else {
				out.println("down");
			}
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void clientConnect(Future<Object[]> toWait)  throws IllegalArgumentException {
		try {
			Object[] temp=toWait.get();
			PrintWriter out=(PrintWriter) temp[0];
			String name=(String) temp[1];
			if(out==null)throw new IllegalArgumentException();
			out.println(name);
			//System.out.println("Name sent:"+name);
			out.flush();
		}catch(ExecutionException e) {
			e.printStackTrace();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
