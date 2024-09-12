package madmath.pong.logic;

public class ServerBall extends LocalBall{
	private double[] errorDirection= {0,1};
	public ServerBall(int x, int y,int r) {
		super(x,y,r);
	}
	public void setDirection(double[] newDirection) {
		if(newDirection.equals(errorDirection)) {
			this.direction=errorDirection;
		}else {
			super.setDirection(newDirection);
		}
	}

}
