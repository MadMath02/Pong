package madmath.pong.logic;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.awt.Shape;
import madmath.pong.visual.*;


public class ServerMain implements Game{
	private ArrayList<Socket> sockets=new ArrayList<>();
	private boolean isSetup=false;
	private int toShow=-1;
	private DlgServerConfigurator setupper;
	private Pong pong;
	private boolean closing=false;
	private HashMap<ServerGame,ServerPairing> map=new HashMap<>();
	private ExecutorService exec;
	private ServerSocket server;
	private ArrayList<Socket> toBePaired=new ArrayList<>();
	private int port=-1;
	private Runnable listener=(Runnable)()->{
		while(!closing) {
			if(isSetup) {
				try{
					Socket aux=null;
					server.setSoTimeout(100);
					try{
						aux=server.accept();
					}catch(Exception exc) {

					}
					if(aux!=null) {
						synchronized(toBePaired) {
							toBePaired.add(aux);
						}
						sockets.add(aux);
						System.out.println("Client connection accepted");
						System.out.println("Clients to be paired:"+toBePaired.size());
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				//System.out.println("Check reached");
				if(toBePaired.size()>=2) {
					//System.out.println("Check passed");
					Runnable aux=(Runnable)()->generatePairing(toBePaired.get(0),toBePaired.get(1));
					Future<?> checker=exec.submit(aux);
					try{
						checker.get(2, TimeUnit.SECONDS);
					}catch(Exception e) {
						e.printStackTrace();
					}
					//System.out.println("Pair matched");
				}else {
					//System.out.println("Too few clients");
				}
			}
		}
		try {
			//System.out.println("Server in chiusura");
			server.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	};


	public ServerMain(Pong pong) {
		exec=Executors.newCachedThreadPool();
		setupper=new DlgServerConfigurator(pong.getFrame());
		this.pong=pong;
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println(e);
		if (e.getExtendedKeyCode()==KeyEvent.VK_Q) {
			e.setSource(this);
			pong.keyReleased(e);
			//System.out.println(e.isConsumed());
		}

	}

	@Override
	public Mode getMode() {
		return Mode.SERVER;
	}

	@Override
	public String[] getNames() {
		String[] res={"",""};
		return res;
	}

	@Override
	public int[] getScores() {
		return null;
	}

	@Override
	public void update() {
		for(ServerGame g:map.keySet()) {
			g.update();
			if(g.isClosed()) {
				map.remove(g);
			}
		}
		ArrayList<Socket> toBeRemoved=new ArrayList<>();
		for(Socket s:sockets) {
			if(s.isClosed()||s.isInputShutdown()||s.isOutputShutdown()) {
				toBeRemoved.add(s);
				if(toBePaired.contains(s))toBePaired.remove(s);
				for(ServerPairing p:map.values()) {
					if(p.contains(s))p.close();
				}
			}
		}
		sockets.removeAll(toBeRemoved);
	}

	@Override
	public void set() {
		isSetup=false;
		exec.submit((Runnable)()->{	
			try{
				if(server!=null)if(server.isBound()&&!server.isClosed())server.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			setupper.setVisible(true);
			setupper.validate();
			while(!setupper.hasBeenSet());
			port=setupper.sendResult();
			try {
				server=new ServerSocket(port);
				isSetup=true;
			}catch(Exception e) {
				e.printStackTrace();
			}
			exec.submit(listener);
		});
		setupper.unset();

	}

	@Override
	public void close() {
		closing=true;
		exec.shutdown();
		try {
			exec.awaitTermination(10, TimeUnit.SECONDS);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(!map.isEmpty()) {
			for(ServerPairing g: map.values()) {
				g.close();
			}
		}
		if(!exec.isTerminated()) {
			System.out.println("Error closing server, bruteforce shutdown");
			System.exit(0);
		}
		for(Socket s:sockets) {
			try{
				s.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//System.exit(0);
	}

	@Override
	public ArrayList<Displayable> getDisplayables() {
		if(map.isEmpty()) {
			toShow=-1;
			ArrayList<Displayable> res=new ArrayList<>();
			res.add(new Displayable(){
				public Color getColor() {
					if(server==null)
						return Color.RED;
					else return Color.GREEN;
				}
				public Area getArea() {
					Shape aux=new Rectangle2D.Float(300,300,300,300);
					return new Area(aux);
				};
			});
			return res;
		}else {
			if(toShow==-1)toShow=0;
			return (new ArrayList<ServerGame>(map.keySet())).get(toShow).getDisplayables();
		}
	}

	public void endGame(ServerGame g) {
		map.remove(g);
	}

	private void generatePairing(Socket one, Socket two) {
		ServerPairing aux;
		try{
			aux=new ServerPairing(one,two,exec, this);
			map.put(aux.getGame(), aux);
			synchronized(toBePaired) {
				toBePaired.remove(one);
				toBePaired.remove(two);
			}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
	public void kick(Socket s) {
		System.out.println("Client kicked for inactivity");
		sockets.remove(s);
		toBePaired.remove(s);
	}
}
