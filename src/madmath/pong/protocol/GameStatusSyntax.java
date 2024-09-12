package madmath.pong.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import madmath.pong.logic.Ball;
import madmath.pong.logic.Bouncer;
import madmath.pong.logic.ClientBall;
import madmath.pong.logic.ClientGame;
import madmath.pong.logic.Game;

public class GameStatusSyntax {
	private static final String pattern="\\{\\d+, \\d+, -?\\d+, -?\\d+, .+:\\d+, .+:\\d+\\}";
	private static final Pattern syntax=Pattern.compile(pattern); 

	public static boolean checkSyntax(String in) {
		Matcher m=syntax.matcher(in);
		return m.matches();
	}

	public static void decode(String in, ClientGame caller) throws IllegalArgumentException {
		if(checkSyntax(in)) {
			String[] toBeParsed=in.substring(1, in.length()-1).split(", ");
			Bouncer[] bounce=caller.getBouncers();
			for(int i=0;i<2;i++) {
				Bouncer b=bounce[i];
				b.teleport(Integer.parseInt(toBeParsed[i]));
			};
			ClientBall b=(ClientBall)caller.getBall();
			b.teleport(Integer.parseInt(toBeParsed[2]),Integer.parseInt(toBeParsed[3]));
			String[] playerdata= {toBeParsed[4],toBeParsed[5]};
			caller.loadPlayers(playerdata);
		}else {
			System.out.println(in);
			throw new IllegalArgumentException();
		}
	}

	public static String encode(Game caller) {
		Bouncer[] boun={(Bouncer)caller.getDisplayables().get(1),(Bouncer)caller.getDisplayables().get(2)};
		Ball b=(Ball)caller.getDisplayables().get(0);
		String[] play=caller.getNames();
		int[] scores=caller.getScores();
		String temp="{"+boun[0].getY()+", "+boun[1].getY()+", "+b.getX()+", "+b.getY()+", "+play[0]+":"+scores[0]+", "+play[1]+":"+scores[1]+"}";
		try {
			if(!checkSyntax(temp))
				throw new IllegalArgumentException();
		}catch(Exception e) {
				e.printStackTrace();
			}
		return temp;
	}
}
