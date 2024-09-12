package madmath.pong.logic;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Bouncer implements Displayable{
	private int x;
	private int y;
	private int w;
	private int h;
	public final Color color;
	private int speed;
	private int maxY;
	private Shape shape;
	private AffineTransform at;
	
	public Bouncer(int x, int y, int w, int h, Color c, int speed,int maxY) {
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
		this.color=c;
		this.speed=speed;
		this.maxY=maxY;
		this.shape=new Rectangle2D.Double(x,y,w,h);
		this.at=new AffineTransform();
	}

	public int getX() {
		return x;
	}

	public int getH() {
		return h;
	}

	public int getW() {
		return w;
	}

	public int getY() {
		return y;
	}

	public int getSpeed() {
		return speed;
	}
	
	public void goUp() {
		int oldY=y;
		y=Math.max(y-speed, 0);
		at.translate(0,y-oldY);
	}
	public void goDown() {
		int oldY=y;
		y=Math.min(y+speed,maxY);
		at.translate(0,y-oldY);
	}
	public int[] middlePoint() {
		int[] res= {this.x+this.w/2,this.y+this.h/2};
		return res;
	}
	public Area getArea() {
		Area toReturn=new Area(shape);
		toReturn.transform(at);
		return toReturn;
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	public void teleport(int y) {
		int aux=this.y;
		this.y=y;
		at.translate(0, y-aux);
	}
}
