package madmath.pong.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import madmath.pong.logic.*;

public class PnlGameShower extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Game toShow;
	private String[] names;
	private int[] scores;

	public PnlGameShower(Pong pong, Mode mode) {
		switch(mode) {
		case LOCAL:
			toShow=new LocalGame(pong.getFrame());
			break;
		case CLIENT:
			toShow=new ClientGame(pong.getFrame());
			break;
		case SERVER:
			toShow=new ServerMain(pong);
			break;
		default:
			toShow=null;
			System.exit(ERROR);
		}
		this.setBackground(Color.BLACK);
	}

	protected void paintComponent(Graphics g) {
		names=toShow.getNames();
		scores=toShow.getScores();
		int[] actualRes= {1600,1200};
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double scaling=Math.min(this.getWidth()/(double)actualRes[0], this.getHeight()/(double)actualRes[1]);
		int[] offset= {(int)(this.getWidth()-actualRes[0]*scaling)/2, (int)(this.getHeight()-actualRes[1]*scaling)/2};
		g2.scale(scaling, scaling);
		g2.translate(offset[0],offset[1]);
		g2.setColor(Color.WHITE);
		if(!names[0].equals("")) {
			g2.drawString(names[0]+"="+scores[0], 50, 100);
			g2.drawString(names[1]+"="+scores[1], this.getWidth()/2+50, 100);
			g2.drawRect( 0,0,actualRes[0], actualRes[1]);
		}
		ArrayList<Displayable> items=toShow.getDisplayables();
		for(Displayable d:items) {
			g2.setColor(d.getColor());
			g2.fill(d.getArea());
		}
	}

	public void update() {
		repaint();
	}

	public Game getGame() {
		return this.toShow;
	}
}
