package madmath.pong.logic;

import java.awt.Color;

public interface Ball extends Displayable{
	public static final Color COLOR=Color.RED;
	public int getX();
	public int getR();
	public int getY();
}
