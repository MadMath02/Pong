package madmath.pong.logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import madmath.pong.visual.DlgNomiLocal;

public class LocalGame implements KeyListener, Game{
	private static final Mode mode=Mode.LOCAL;
	private ExecutorService exec;
	private DlgNomiLocal nomi;
	private boolean isGoing;
	private Player[] players;
	private LocalBall ball;
	private Bouncer[] bouncers=new Bouncer[2];
	private static final int[] keysRecognized= {KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_UP,KeyEvent.VK_DOWN};
	private boolean[] movement= {false,false,false,false};
	private int[] resolution={1600,1200};

	public LocalGame(JFrame parent) {
		exec=Executors.newCachedThreadPool();
		nomi=new DlgNomiLocal(parent);
		isGoing=false;
		ball=new LocalBall(resolution[0],resolution[1],resolution[1]/50);
		bouncers[0]=new Bouncer(0,(int)(resolution[1]*(9.0/20)),resolution[0]/20,resolution[1]/10,Color.YELLOW,resolution[1]/100,
				(int)(resolution[1]*(9.0/10)));
		bouncers[1]=new Bouncer((int)(resolution[0]*(19.0/20)),(int)(resolution[1]*(9.0/20)),resolution[0]/20,resolution[1]/10,Color.BLUE,resolution[1]/100,
				(int)(resolution[1]*(9.0/10)));
		players= new Player[2];
		players[0]=new Player("PlayerOne");
		players[1]=new Player("PlayerTwo");
		isGoing=true;
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
		if(isGoing){
			if(movement[0]) {
				bouncers[0].goUp();
			}
			if(movement[1]) {
				bouncers[0].goDown();
			}
			if(movement[2]) {
				bouncers[1].goUp();
			}
			if(movement[3]) {
				bouncers[1].goDown();
			}
			ball.move();
			checkHit(bouncers[0],ball,0);
			checkHit(bouncers[1],ball,1);
			int score=ball.checkScoring();
			if(score!=0) {
				players[score-1].scored();
			}
		}
	}

	private void checkHit(Bouncer b, LocalBall ball,int player) {
		int leftmost=ball.getX()-ball.getR();
		int rightmost=ball.getX()+ball.getR();
		Area collision=b.getArea();
		collision.intersect(ball.getArea());
		if(!collision.isEmpty()) {
			if(ball.getX()<b.getX()) {
				ball.moveH(b.getX()-rightmost);
			}else if(ball.getX()>b.getX()+b.getW()) {
				ball.moveH(b.getX()+b.getW()-leftmost);
			}else {
				fault(player);
			}
			double[] dir=ball.getDirection();
			dir[0]*=-1;
			dir[1]+=(((double)ball.getY()-b.middlePoint()[1])/(b.getH()/2.));
			ball.setDirection(dir);
			ball.speedUp();
			//System.out.println("Ball's direction is now ["+ball.getDirection()[0]+","+ball.getDirection()[1]+" and its speed is "+ball.getSpeed());
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
		isGoing=false;
		exec.submit((Runnable)()->{	
			nomi.setVisible(true);
			nomi.validate();
			String[] res=nomi.sendResult();
			players[0].rename(res[0]);
			players[1].rename(res[1]);
			isGoing=true;
		});
		resetScore();
		nomi.unset();
	}

	public void resetScore() {
		players[0].setScore(0);
		players[1].setScore(0);
		ball.resetPosition();
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

	private void manuallyScore(int i) {
		players[i].scored();
	}

	private void fault(int i) {
		if(i==0) {
			manuallyScore(1);
		}else if(i==1) {
			manuallyScore(0);
		}
		ball.resetPosition();
	}
	
	public void close() {
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

	public String[] getPlayers() {
		String[] res=new String[2];
		for(int i=0;i<2;i++) {
			res[i]=players[i].toString();
		}
		return res;
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
}
