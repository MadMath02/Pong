package madmath.pong.logic;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class LocalBall implements Ball{
	private Random random;
	private int startingX;
	private int startingY;
	private int startingSpd;
	private double x;
	private double y;
	private int r;
	private static final Color COLOR=Color.red;
	private int maxX;
	private int maxY;
	private double speed;
	private Shape shape;
	private AffineTransform at;
	protected double[] direction;
	
	public double getSpeed() {
		return speed;
	}
	
	public LocalBall(int x, int y,int r) {
		this.random=new Random();
		this.maxX=x;
		this.maxY=y;
		this.r=r;
		this.startingX=x/2;
		this.startingY=y/2;
		this.x=x/2.0;
		this.y=y/2.0;
		this.startingSpd=x*3/1000;
		speed=startingSpd;
		resetPosition();
		this.shape=new Ellipse2D.Double(this.x-r,this.y-r,2*r,2*r);
		this.at=new AffineTransform();
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getR() {
		return this.r;
	}
	
	public void speedUp() {
		speed*=1.1;
		speed=Math.min(speed, 20);
	}
	
	public void moveH(double input) {
		x+=input;
		at.translate(input, 0);
	}
	
	public void setDirection(double[] direction) {
		if(Math.abs(direction[0])<Math.abs(direction[1])) {
			direction[0]*=Math.abs(direction[1]/direction[0]);
		}
		double module=Math.sqrt(direction[0]*direction[0]+direction[1]*direction[1]);
		direction[0] /=module;
		direction[1] /=module;
		this.direction = direction;
	}
	public double[] getDirection() {
		return this.direction;
	}
	
	public void move() {
		double oldX=this.x,oldY=this.y;
		this.x+=speed*direction[0];
		this.y+=speed*direction[1];
		if(y<r) {
			y-=r;
			y*=-1;
			y+=r;
			direction[1]*=-1;
		}else if(y>maxY-r) {
			y-=2*(y-maxY+r);
			direction[1]*=-1;
		}
		at.translate(this.x-oldX, this.y-oldY);
	}
	
	public void resetPosition() {
		this.at=new AffineTransform();
		this.x=startingX;
		this.y=startingY;
		this.speed=this.startingSpd;
		if(random.nextBoolean()) {
			double[] toGive= {1,0};
			setDirection(toGive);
		}else {
			double[] toGive= {-1,0};
			setDirection(toGive);
		}
	}
	
	protected int[] initialConditions() {
		int[] aux= {maxX,maxY,r};
		return aux;
	}
	public int checkScoring() {
		if(x<0) {
			resetPosition();
			return 2;
		}else if(x>maxX){
			resetPosition();
			return 1;
		}else {
			return 0;
		}
	}
	public Area getArea() {
		Area res=new Area(shape);
		res.transform(at);
		return res;
	}
	public Color getColor() {
		return COLOR;
	}
}
