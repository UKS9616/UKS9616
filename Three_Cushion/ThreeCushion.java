
package hw2;

import api.PlayerPosition;

import api.BallType;

import static api.PlayerPosition.*;

import static api.BallType.*;

/**
 * Class that models the game of three-cushion billiards.
 * 
 * This class keeps track of the current state of the game, including the positions of the balls, the scores of the players, and whose turn it is. It also
 * has methods for making shots and handling fouls.
 * 
 * @author Udip Shrestha
 */
public class ThreeCushion {

		
		
		//The cue ball that will be used in the game.
		private BallType cueBall;
		
		//A boolean value that indicates whether or not the current shot is a bank shot.
		private boolean bankShot = false;
		
		//An integer representing the score of Player A.
		private int PLAYER_AScore = 0;	
		
		//An integer representing the score of Player B.
		private int PLAYER_BScore = 0;
		
		//An integer representing the current inning of the game.
		private int inning = 1;
		
		//A boolean value that indicates whether or not the current shot is the break shot.
		private boolean breakShot = false;
		
		//A boolean value that indicates whether or not the current inning has started.
		private boolean inningStarted = false;
		
		// The player who is currently playing in the current inning.   
        private PlayerPosition inningPlayer = null;
        
		// The player who won the lag at the beginning of the game.
		private PlayerPosition lagWinner;
		
		//A boolean value that indicates whether or not a shot has started
		private boolean shotStarted;
		
		//A boolean value that indicates whether or not the game is over.
		private boolean gameOver = false;
		
		//A boolean value that indicates whether or not the cue ball touches a red ball.
		private boolean TouchesRedBall = false;
		
		//A boolean value that indicates whether or not the cue ball touches a new ball.
		private boolean TouchesNewBall= false;
		
		//A boolean value that indicates whether or not the current shot is valid.
		private boolean shotValid = false;
		
		// An integer representing the number of cushions touched by the cue ball during the shot.
		private int CushionsTouched = 0;
		
		//An integer representing the number of points required to win the game.
		private int PointsToWin;
		
		// A boolean value that indicates whether or not the current shot is a valid shot.
		private boolean ValidShot = false;
		
		//A boolean value that indicates whether or not the current shot is a bank shot.
		private boolean BankShot = false;
		
		/**
	     * Class that models the game of three-cushion billiards.
	     * 
	     * @param lagWinner the player who won the lag
	     * @param pointsToWin the number of points required to win the game
	     */

		public ThreeCushion(PlayerPosition lagWinner, int pointsToWin) {
		    setPointsToWin(pointsToWin);
		    setLagWinner(lagWinner);
		}

		private void setPointsToWin(int pointsToWin) {
		    PointsToWin = pointsToWin;
		}

		private void setLagWinner(PlayerPosition lagWinner) {
		    this.lagWinner = lagWinner;
		}

		/**
	     *Indicates the given ball has impacted the given cushion.
	     */
		public void cueBallImpactCushion() {
		    if (getInningPlayer() == null || isGameOver()) {
		        return; // does nothing if there's no inning player or the game is over
		    }
		    
		    CushionsTouched++;
		    
		    if (breakShot && !TouchesRedBall) {
		        foul();
		        return; // does nothing else if it's a break shot and the cue ball doesn't touch a red ball
		    }
		    
		    if (CushionsTouched >= 3 && !TouchesRedBall && !TouchesNewBall) {
		        BankShot = true;
		    }
		}
	
		
	

		/**
		 * Called when the cue ball strikes a ball during a shot.
		 * @param Ball the type of ball that was struck
		 */
		public void cueBallStrike(BallType Ball) {
			if (getInningPlayer() != null) {
				if (!isGameOver()) {
					if (CushionsTouched >= 3 && (TouchesRedBall || TouchesNewBall) && !(TouchesRedBall && TouchesNewBall)) {
						ValidShot = true;
					}
						if (Ball == RED) {
							TouchesRedBall = true;
						} else {
							TouchesNewBall = true;
								}
	            if (CushionsTouched >= 3 && TouchesRedBall && TouchesNewBall) {
	                if (ValidShot) {
	                    shotValid = true;
	                }
	            }
	            if (breakShot) {
	                if (!TouchesRedBall && CushionsTouched == 0) {
	                    foul();
	                }
	            }
	        }
	    }
	}
		/**
		 * Indicates the cue stick has struck the given ball.
		 * 
		 */
	public void cueStickStrike(BallType Ball) {
		if (getInningPlayer() == null || isGameOver()) {
			return;
		}
		
		if (Ball == cueBall && !shotStarted) {
			shotStarted = true;
			inningStarted = true;
			bankShot = false;
		} else {
			foul();
		}
	}
	
	/**
	 * A foul immediately ends the player's inning, even if the current shot has not yet ended.
	 * It increments the fouls counter for the opponent and switches the turn to the other player. It also
	 * checks if the player has committed three fouls in a row
	 */
	public void foul() {
	    if (getInningPlayer() == null || isGameOver()) {
	        return; // exit the method if there is no inning player or the game is already over
	    }
	    
	    // swap the ball and player, and reset inning and shot status
	    cueBall = ballSwap();
	    inningPlayer = playerSwap();
	    inningStarted = false;
	    breakShot = false;
	    bankShot = false;
	    inning++;
	}

	/**
	 * Indicates that all balls have stopped motion.
	 * @return true if the turn is over and it is now the other player's turn, false otherwise.
	 */
	public void endShot() {
	    if (inningPlayer == null || isGameOver()) {
	        return;
	    }

	    if (!inningStarted) {
	        return;
	    }

	    if (shotValid) {
	        if (BankShot) {
	            bankShot = true;
	        }

	        if (inningPlayer == PLAYER_A) {
	            PLAYER_AScore++;
	        } else {
	            PLAYER_BScore++;
	        }
	    } else {
	        inning++;
	        cueBall = ballSwap();
	        inningPlayer = playerSwap();
	    }

	    // Reset variables for next shot
	    shotValid = false;
	    TouchesRedBall = false;
	    TouchesNewBall = false;
	    CushionsTouched = 0;
	    breakShot = false;
	    shotStarted = false;
	    inningStarted = false;

	    // Check for game over
	    if (PLAYER_AScore == PointsToWin || PLAYER_BScore == PointsToWin) {
	        gameOver = true;
	    }
	}
	
	/*
	 * Gets the cue ball of the current player.
	 */
	public BallType getCueBall() {
		
		return cueBall;

    }
    /*
     *Gets the inning number.
     */
	public int getInning() {

		return inning;

}
	/*
	 * Gets the current player.
	 */
	public PlayerPosition getInningPlayer() {

		return inningPlayer;

}
	/**
	 * Gets the number of points scored by Player A.
	 */
	public int getPlayerAScore() {
	
		return PLAYER_AScore;
	
	}
	/**
	 * Gets the number of points scored by Player B.
	 * @return PLAYER_BScore
	 */
	public int getPlayerBScore() {
	
		return PLAYER_BScore;
	
	}
	/**
	 * Swaps the current ball type and returns the new ball type.
	 * @return WHITE.
	 */
	private BallType ballSwap() {
	    return cueBall == YELLOW ? WHITE : YELLOW;
	}
		
	
	/*
	 * Swaps the current player's position with the other player's position.
	 */
	private PlayerPosition playerSwap() {
	    return (inningPlayer == PLAYER_A) ? PLAYER_B : PLAYER_A;
	}
	
	
	/*
	 * Returns true if and only if the most recently completed shot was a bank shot.
	 *  @return true if the last shot was a bank shot, false otherwise		
	 */
	public boolean isBankShot() {
	
		return bankShot;
	
	}
	/*
	 * Returns true if and only if this is the break shot (i.e., the first shot of the game).
	 *@return true if the current shot is a break shot, false otherwise.
	 */
	public boolean isBreakShot() {
	
		return breakShot;
	
	}
	/*
	 * Returns true if the game is over (i.e., one of the players has reached the designated number of points to win).
	 */
	public boolean isGameOver() {
	
		return gameOver;
	
	}
	/*
	 * Returns true if the shooting player has taken their first shot of the inning.
	 */
	public boolean isInningStarted() {
	
		return inningStarted;
	
	}
	/*
	 * Returns true if a shot has been taken (see cueStickStrike()), but not ended (see endShot()).
	 */
	public boolean isShotStarted() {
	
		return shotStarted;
	
	}
	/*
	 * Sets whether the player that won the lag chooses to break (take first shot), or chooses the other player to break.
	 */
	public void lagWinnerChooses(boolean selfBreak, BallType cueBall) {
		if (getInningPlayer() != null) {
			return;
		}
		
		breakShot = true;
		this.cueBall = cueBall;
		
		if (selfBreak) {
			inningPlayer = lagWinner;
		} else {
			inningPlayer = (lagWinner == PLAYER_A) ? PLAYER_B : PLAYER_A;
			this.cueBall = ballSwap();
		}
	}


public String toString() {

String fmt = "Player A%s: %d, Player B%s: %d, Inning: %d %s%s";

String playerATurn = "";

String playerBTurn = "";

String inningStatus = "";

String gameStatus = "";

if (getInningPlayer() == PLAYER_A) {

playerATurn = "*";

} else if (getInningPlayer() == PLAYER_B) {

playerBTurn = "*";

}

if (isInningStarted()) {

inningStatus = "started";

} else {

inningStatus = "not started";

}

if (isGameOver()) {

gameStatus = ", game result final";

}

return String.format(fmt, playerATurn, getPlayerAScore(), playerBTurn, getPlayerBScore(), getInning(),

inningStatus, gameStatus);

}

}

