package madmath.pong.logic;

public class Player {
	private String defaultName;
	private String name;
	private int score;
	public Player(String name) {
		this.defaultName=name;
		this.name=defaultName;
		this.setScore(0);
	}
	public String getName() {
		return name;
	}
	public void rename(String name) {
		this.name = (name.equals(""))? defaultName:name;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void scored() {
		score++;
	}
	public String toString() {
		return this.name+":"+this.score;
	}
	public void load(String data) {
		this.rename(data.split(":")[0]);
		this.setScore(Integer.parseInt(data.split(":")[1]));
	}
}
