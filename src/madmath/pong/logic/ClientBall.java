package madmath.pong.logic;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class ClientBall implements Ball{
	private int x,y,r,initX,initY;

	private Shape shape;
	
	public ClientBall(int x,int y,int r) {
		this.x=x;
		initX=x;
		this.y=y;
		initY=y;
		this.r=r;
		this.shape=new Ellipse2D.Double(initX-r,initY-r,2*r,2*r);
	}
	
	public int getX() {
		return x;
	}

	public int getR() {
		return r;
	}

	public int getY() {
		return y;
	}

	public Area getArea() {
		AffineTransform at=new AffineTransform();
		at.translate(x-initX, y-initY);
		Area res=new Area(shape);
		res.transform(at);
		return res;
	}
	public void teleport(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public Color getColor() {
		return COLOR;
	}
}
