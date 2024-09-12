package madmath.pong.logic;

import static madmath.pong.protocol.ClientConnectionProtocol.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import madmath.pong.protocol.GameStatusSyntax;
import madmath.pong.visual.DlgNomiClient;

public class ClientGame implements KeyListener, Game{
	private String nomeGiocatore;
	private Future<Object[]> settingsReceived;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private static final Mode mode=Mode.CLIENT;
	private ExecutorService exec;
	private DlgNomiClient nomi;
	private boolean isGoing;
	private Player[] players;
	private ClientBall ball;
	private Bouncer[] bouncers=new Bouncer[2];
	private static final int[] keysRecognized= {KeyEvent.VK_UP,KeyEvent.VK_DOWN};
	private boolean[] movement= {false,false};
	private int[] resolution= {1600,1200};
	private Callable<Object[]> setter;

	public ClientGame(JFrame parent) {
		setter=(Callable<Object[]>)()->{
			nomi.setVisible(true);
			nomi.validate();
			String[] res=nomi.sendResult();
			nomeGiocatore=res[0];
			socket=connect(res[1]);
			try {
				//socket.setTcpNoDelay(true);

				in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out=new PrintWriter(socket.getOutputStream());
			}catch(Exception e) {
				e.printStackTrace();
			}
			//isGoing=true;
			Object[] toReturn= {out, res[0]};
			return toReturn;
		};
		exec=Executors.newCachedThreadPool();
		nomi=new DlgNomiClient(parent);
		isGoing=false;
		ball=new ClientBall(resolution[0],resolution[1],resolution[1]/50);
		bouncers[0]=new Bouncer(0,(int)(resolution[1]*(9.0/20)),resolution[0]/20,resolution[1]/10,Color.YELLOW,resolution[1]/100,
				(int)(resolution[1]*(9.0/10)));
		bouncers[1]=new Bouncer((int)(resolution[0]*(19.0/20)),(int)(resolution[1]*(9.0/20)),resolution[0]/20,resolution[1]/10,Color.BLUE,resolution[1]/100,
				(int)(resolution[1]*(9.0/10)));
		players= new Player[2];
		players[0]=new Player("You");
		players[1]=new Player("OnlineOpponent");
	}

	private static int keyRecognition(KeyEvent e) {
		int kc=e.getKeyCode();
		for(int i=0;i<keysRecognized.length;i++) {
			if(kc==keysRecognized[i]) {
				return i;
			}
		}
		return -1;
	}

	public void update() {
		if(socket!=null)
			try{
				String received=null;
				if(in!=null)while(in.ready())received=in.readLine();
				if(received!=null) {
					if(received.equals("resend")) {
						out.println(nomeGiocatore);
						out.flush();
					}else {
						isGoing=true;
						GameStatusSyntax.decode(received,this);
						dataSend(out,movement);
					}
				}else isGoing=false;
				//System.out.println(isGoing);
			}catch(SocketException e) {
				close();
			}catch(Exception e) {
				e.printStackTrace();
			}

	}

	public String[] getNames() {
		String[] res=new String[2];
		res[0]=players[0].getName();
		res[1]=players[1].getName();
		return res;
	}

	public int[] getScores() {
		int[] res=new int[2];
		res[0]=players[0].getScore();
		res[1]=players[1].getScore();
		return res;
	}
	
	public void set() {
		callSetter();
		sendConnectionData();
	}
	
	private void callSetter() {
		if(socket!=null)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		isGoing=false;
		nomi.unset();
		settingsReceived=exec.submit(setter);

	}

	public void keyTyped(KeyEvent e) {

	}


	public void keyPressed(KeyEvent e) {
		int key=keyRecognition(e);
		if(key!=-1)
			movement[key]=true;
		//System.out.println(e);
	}


	public void keyReleased(KeyEvent e) {
		int key=keyRecognition(e);
		if(key!=-1)
			movement[key]=false;
		//System.out.println(e);
	}

	public int[] getResolution() {
		return resolution;
	}

	public void close() {
		if(out!=null)
			out.println("quit");
		try {
			socket.shutdownInput();
			socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		exec.shutdown();
		try {
			exec.awaitTermination(10, TimeUnit.SECONDS);
			if(!exec.isTerminated()) {
				exec.shutdownNow();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Mode getMode() {
		return mode;
	}

	public boolean isGoing() {
		return isGoing;
	}

	public Bouncer[] getBouncers() {
		return bouncers;
	}

	public Ball getBall() {
		return this.ball;
	}
	private Socket connect(String input) {
		int port;
		try{
			port=Integer.parseInt(input.split(":")[1]);
		}catch(Exception e) {
			port=1618;
		}
		InetAddress server;
		String hostname=input.split(":")[0];
		try {
			server=InetAddress.getByAddress(IPParser(hostname));
		}catch(IOException e) {
			try{
				server=InetAddress.getByName(hostname);
			}catch(Exception exc) {
				server=null;
			}
		}
		Socket aux=connect(server,port);
		return aux;
	}

	private Socket connect(InetAddress server, int port) {
		Socket temp;
		try {
			temp=new Socket(server,port);
		}catch(IOException e) {
			temp=null;
		}
		return temp;
	}

	private byte[] IPParser(String IP) {
		String[] numbers=IP.replace('.',',').split(",");
		byte[] res=new byte[4];
		for(int i=0;i<4;i++) {
			res[i]=Byte.parseByte(numbers[i]);
		}
		return res;
	}
	public String[] getPlayers() {
		String[] res=new String[2];
		for(int i=0;i<2;i++) {
			res[i]=players[i].toString();
		}
		return res;
	}
	public void loadPlayers(String[] in) {
		for(int i=0;i<2;i++) {
			players[i].load(in[i]);
		}
	}

	@Override
	public ArrayList<Displayable> getDisplayables() {
		ArrayList<Displayable> res=new ArrayList<>();
		res.add(this.getBall());
		for(Bouncer b: this.getBouncers()) {
			res.add(b);
		}
		return res;
	}
	private void sendConnectionData() {
		try{
			exec.submit((Runnable)()->clientConnect(settingsReceived));
		}catch(IllegalArgumentException iae) {
			set();
		}
	}
}
