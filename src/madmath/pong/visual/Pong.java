package madmath.pong.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import madmath.pong.logic.Game;
import madmath.pong.logic.Mode;

public class Pong implements KeyListener,WindowListener{
	private Mode mode;
	private JFrame frame;
	private PnlGameShower panel;
	private DlgModeSelector dlgMode;
	private Game game;
	private Timer timer;
	public Pong() {
		frame=new JFrame("Pong");
		frame.addWindowListener(this);
		frame.setFocusable(true);
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setResizable(false);
		frame.setBackground(Color.BLACK);
		frame.setVisible(true);
		dlgMode=new DlgModeSelector(frame);
		dlgMode.setVisible(true);
		dlgMode.validate();
		mode=dlgMode.getMode();
		addPnl();
		addUtils();
		game=panel.getGame();
		frame.addKeyListener(this);
		timer=new Timer(10,(e)->{
			game.update();
			panel.update();
		});
		timer.start();
		frame.validate();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Pong pong=new Pong();
					pong.game.set();
				}catch(Exception e) {
					e.printStackTrace();
				}
			};
		});
		return;
	}

	private void addUtils() {
		Container toAddTo=frame.getContentPane();
		Game game=panel.getGame();
		JPanel tmp=new JPanel();
		tmp.setFocusable(false);
		JButton btnClose=new JButton("Close");
		btnClose.addActionListener((e)->{
			frame.dispose();
			timer.stop();
			game.close();
		});
		/*JButton btnReset=new JButton("Reset score");
		btnReset.setFocusable(false);
		btnReset.addActionListener((e)->game.resetScore());*/
		JButton btnSetter;
		switch(mode) {
		case LOCAL:
			btnSetter=new JButton("Change names of the players");
			break;
		case CLIENT:
			btnSetter=new JButton("Change your name/server");
			break;
		case SERVER:
			btnSetter=new JButton("Change listening port");
			break;
		default:
			btnSetter=new JButton("Error occurring");	
		}
		btnSetter.setFocusable(false);
		btnSetter.addActionListener((e)->game.set());
		tmp.add(btnSetter);
		//tmp.add(btnReset);
		tmp.add(btnClose);
		tmp.setBackground(Color.BLACK);
		tmp.addKeyListener(this);
		toAddTo.add(tmp,BorderLayout.NORTH);
	}

	private void addPnl() {
		panel=new PnlGameShower(this,mode);
		frame.getContentPane().add(panel,BorderLayout.CENTER);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		game.keyTyped(e);
		//System.out.println(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		game.keyPressed(e);
		//System.out.println(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource().getClass().getName()=="madmath.pong.logic.ServerMain") {
			frame.removeKeyListener(this);
			e.setSource(frame);
			timer.stop();
			frame.dispose();
			game.close();
		}else
		game.keyReleased(e);
		//System.out.println(e);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.dispose();
		timer.stop();
		game.close();
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
	public JFrame getFrame() {
		return this.frame;
	}
}
