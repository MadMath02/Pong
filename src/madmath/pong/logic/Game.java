package madmath.pong.logic;

import java.awt.event.KeyListener;
import java.util.ArrayList;

public interface Game extends KeyListener{
	public Mode getMode();
	public String[] getNames();
	public int[] getScores();
	public void update();
	public void set();
	public void close();
	public ArrayList<Displayable> getDisplayables();
}
