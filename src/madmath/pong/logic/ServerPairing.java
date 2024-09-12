package madmath.pong.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static madmath.pong.protocol.GameStatusSyntax.encode;

public class ServerPairing {
	private ServerMain parent;
	private int[] counters= {0,0};
	private boolean alreadyPaused=false;
	private boolean closed=false;
	private Socket[] sockets;
	private ServerGame game;
	private BufferedReader inOne;
	private PrintWriter outOne;
	private BufferedReader inTwo;
	private PrintWriter outTwo;
	private ArrayList<Future<String>> names=new ArrayList<Future<String>>();

	private Callable<String> readNameOne=(Callable<String>)()->{String aux=inOne.readLine();
	System.out.println("Player one("+aux+"):ready");
	return aux;
	};
	private Callable<String> readNameTwo=(Callable<String>)()->{String aux=inTwo.readLine();
	System.out.println("Player two("+aux+"):ready");
	return aux;
	};

	public ServerPairing(Socket one,Socket two,ExecutorService exec, ServerMain parent) throws Exception{
		this.parent=parent;
		Socket[] aux= {one,two};
		sockets=aux;
		try {
			System.out.println("Creating Pairing");
			//one.setTcpNoDelay(true);
			inOne=new BufferedReader(new InputStreamReader(one.getInputStream()));
			outOne=new PrintWriter(one.getOutputStream());
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			//two.setTcpNoDelay(true);
			inTwo=new BufferedReader(new InputStreamReader(two.getInputStream()));
			outTwo=new PrintWriter(two.getOutputStream());
		}catch(Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Streams created");
		try{
			names.add(exec.submit(readNameOne));
			names.add(exec.submit(readNameTwo));
		}catch(Exception e) {
			e.printStackTrace();
			close();
		}
		String[] received=new String[2];
		try{
			 received[0]=names.get(0).get(1, TimeUnit.SECONDS);
		}catch(TimeoutException toe) {
			parent.kick(one);
			close();
			return;
		}
		try{
			 received[1]=names.get(1).get(1, TimeUnit.SECONDS);
		}catch(TimeoutException toe) {
			parent.kick(two);
			close();
			return;
		}
		this.game=new ServerGame(received, exec, this);
	}
	private Runnable tick=()->{
		String[] received=new String[2];
		try{
			if(!inOne.ready())counters[0]++;
			while(inOne.ready()) {
				received[0]=inOne.readLine().trim();
				counters[0]=0;
			}
			if(!inTwo.ready())counters[1]++;
			while(inTwo.ready()) {
				received[1]=inTwo.readLine().trim();
				counters[1]=0;
			}
			game.setMoves(receivedToBoolArray(received));
		}catch(SocketException e) {
			close();
		}catch(IOException e) {
			close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		dataSend();
		if(counters[0]>10||counters[1]>10)close();
	};
	private boolean[] receivedToBoolArray(String[] in) {
		boolean[] out=new boolean[4];
		for(int i=0;i<2;i++) {
			if(in[i]!=null) {
				switch(in[i]) {
				case "null":
					out[2*i]=false;
					out[2*i+1]=false;
					break;
				case "up":
					out[2*i]=true;
					out[2*i+1]=false;
					break;
				case "down":
					out[2*i]=false;
					out[2*i+1]=true;
					break;
				case "quit":
					close();
					try {
						wait(100);
					}catch(Exception e) {
						e.printStackTrace();
					}
					parent.kick(sockets[i]);
					if(i==0) {
						outTwo.println("resend");
						outTwo.flush();
					}else {
						outOne.println("resend");
						outOne.flush();
					}
					break;
				}
			}else {
				out[2*i]=false;
				out[2*i+1]=false;
			}
		}
		return out;
	}
	public Runnable tick() {
		return this.tick;
	}
	public ServerGame getGame() {
		return this.game;
	}
	private void pause() {
		outOne.println("pause");
		outOne.flush();
		outTwo.println("pause");
		outTwo.flush();
	}
	private void dataSend() {
		if(game.isGoing()) {
			outOne.println(encode(game));
			outTwo.println(encode(game));
			outOne.flush();
			outTwo.flush();
		}else {
			if(!alreadyPaused) {
				pause();
				alreadyPaused=true;
			}
		}
	}
	public void close() {
		try{
			inOne.close();
			inTwo.close();
			outOne.close();
			outTwo.close();
			if(game!=null)game.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		closed=true;
	}
	public boolean contains(Socket s) {
		return s==sockets[0]||s==sockets[1];
	}
	public boolean isClosed() {
		return closed;
	}
	public ArrayList<Socket> getSockets() {
		ArrayList<Socket> toReturn=new ArrayList<>();
		for(int i=0;i<2;i++) {
			toReturn.add(sockets[i]);
		}
		return toReturn;
	}
}
