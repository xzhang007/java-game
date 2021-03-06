/*************************************************************************************
 *
 *  This program is used to test PJ4.PokerGame class
 *  More info are given in Readme file
 * 
 *  PJ4 class allows user to run program as follows:
 *
 *    	java PJ4		// default credit is $100
 *  or 	java PJ4 NNN		// set initial credit to NNN
 *
 *  Do not modify this file!
 *
 **************************************************************************************/

import PJ4.PokerGame;

class TestPokerGame {

    public static void main(String args[]) 
    {
	PokerGame pokergame;
	if (args.length > 0)
		pokergame = new PokerGame(Integer.parseInt(args[0]));
	else
		pokergame = new PokerGame();
	pokergame.play();
    }
}
