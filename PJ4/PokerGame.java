package PJ4;
import java.util.*;

/*
 * Ref: http://en.wikipedia.org/wiki/Video_poker
 *      http://www.google.com/ig/directory?type=gadgets&url=www.labpixies.com/campaigns/videopoker/videopoker.xml
 *
 *
 * Short Description and Poker rules:
 *
 * Video poker is also known as draw poker. 
 * The dealer uses a 52-card deck, which is played fresh after each playerHand. 
 * The player is dealt one five-card poker playerHand. 
 * After the first draw, which is automatic, you may hold any of the cards and draw 
 * again to replace the cards that you haven't chosen to hold. 
 * Your cards are compared to a table of winning combinations. 
 * The object is to get the best possible combination so that you earn the highest 
 * payout on the bet you placed. 
 *
 * Winning Combinations
 *  
 * 1. Jacks or Better: a pair pays out only if the cards in the pair are Jacks, 
 * 	Queens, Kings, or Aces. Lower pairs do not pay out. 
 * 2. Two Pair: two sets of pairs of the same card denomination. 
 * 3. Three of a Kind: three cards of the same denomination. 
 * 4. Straight: five consecutive denomination cards of different suit. 
 * 5. Flush: five non-consecutive denomination cards of the same suit. 
 * 6. Full House: a set of three cards of the same denomination plus 
 * 	a set of two cards of the same denomination. 
 * 7. Four of a kind: four cards of the same denomination. 
 * 8. Straight Flush: five consecutive denomination cards of the same suit. 
 * 9. Royal Flush: five consecutive denomination cards of the same suit, 
 * 	starting from 10 and ending with an ace
 *
 */


/* This is the main poker game class.
 * It uses Deck and Card objects to implement poker game.
 * Please do not modify any data fields or defined methods
 * You may add new data fields and methods
 * Note: You must implement defined methods
 */



public class PokerGame {

    // default constant values
    private static final int startingBalance=100;
    private static final int numberOfCards=5;

    // default constant payout value and playerHand types
    private static final int[] multipliers={1,2,3,5,6,9,25,50,250};
    private static final String[] goodHandTypes={ 
	  "Royal Pair" , "Two Pairs" , "Three of a Kind", "Straight", "Flush	", 
	  "Full House", "Four of a Kind", "Straight Flush", "Royal Flush" };

    // must use only one deck
    private static final Deck oneDeck = new Deck(1);

    // holding current poker 5-card hand, balance, bet    
    private List<Card> playerHand;
    private int playerBalance;
    private int playerBet;

    /** default constructor, set balance = startingBalance */
    public PokerGame()
    {
	this(startingBalance);
    }

    /** constructor, set given balance */
    public PokerGame(int balance)
    {
	this.playerBalance= balance;
    }

    /** This display the payout table based on multipliers and goodHandTypes arrays */
    private void showPayoutTable()
    { 
	System.out.println("\n\n");
	System.out.println("Payout Table   	      Multiplier   ");
	System.out.println("=======================================");
	int size = multipliers.length;
	for (int i=size-1; i >= 0; i--) {
		System.out.println(goodHandTypes[i]+"\t|\t"+multipliers[i]);
	}
	System.out.println("\n\n");
    }

    /** Check current playerHand using multipliers and goodHandTypes arrays
     *  Must print yourHandType (default is "Sorry, you lost") at the end of function.
     *  This can be checked by testCheckHands() and main() method.
     */
    private void checkHands()
    {
        // implement this method!
        assert playerHand.size() == numberOfCards;

        int kindOfSuit = howManyDifferentValues("suit");    // add a private method to count how many different suits in hand
        int kindOfDenomination = howManyDifferentValues("rank");    //  // add a private method to count how many different denominations in hand

        if (kindOfSuit == 1) {  // all cards in hand are in only one suit
            if (containsRank(10) && containsRank(11) && containsRank(12) && containsRank(13) && containsRank(1)) {
                System.out.println("Royal Flush!");
                playerBalance *= multipliers[8];
            } else if (isConsecutive()) {
                System.out.println("Straight Flush!");
                playerBalance *= multipliers[7];
            } else {
                System.out.println("Flush!");
                playerBalance *= multipliers[4];
            }
        } else if (kindOfDenomination == 2) {   // all cards in hand are only 2 denominations
            if (getMaxFrequency() == 4) {
                System.out.println("Four of a Kind!");
                playerBalance *= multipliers[6];
            } else {
                System.out.println("Full House!");
                playerBalance *= multipliers[5];
            }
        } else if (isConsecutive()) {   // Straight!
            System.out.println("Straight!");
            playerBalance *= multipliers[3];
        } else if (kindOfDenomination == 3) {   // all cards in hand are only 3 denominations
            if (getMaxFrequency() == 3) {
                System.out.println("Three of a Kind!"); 
                playerBalance *= multipliers[2];
            } else {
                System.out.println("Two Pairs!");
                playerBalance *= multipliers[1];
            }
        } else if (kindOfDenomination == 4) {   // Royal Pair or Lost! (don't forget this case includes lost!)
            if (getRankFrequency(11) == 2 || getRankFrequency(12) == 2 || getRankFrequency(13) == 2 || getRankFrequency(1) == 2) {
                System.out.println("Royal Pair!");
                playerBalance *= multipliers[0];
            } else {
                System.out.println("Sorry, you lost!"); // this is important, since it includes lost!
                playerBalance -= playerBet;
            }
        } else {    // all other cases are lost
            System.out.println("Sorry, you lost!");
            playerBalance -= playerBet;
        }
    }

    /*************************************************
     *   add new private methods here ....
     *
     *************************************************/
     private int howManyDifferentValues(String field) {
        assert field.equals("rank") || field.equals("suit");
        int counter = 0;
        List<Integer> list = new ArrayList<Integer>();
        int n;
        for (int i = 0; i < numberOfCards; i++) {
            n = field.equals("rank") ? playerHand.get(i).getRank() : playerHand.get(i).getSuit();   // check if to count how many different ranks or suits
            if (!list.contains(n)) {
                counter++;
                list.add(n);
            }
        }

        return counter;
    }

    private boolean containsRank(int n) {
        boolean result = false;
        boolean flag = false;
        for (int i = 0; !flag && i < numberOfCards; i++) {
            if (n == playerHand.get(i).getRank()) {
                result = true;
                flag = true;
            }
        }

        return result;
    }

    private int getRankFrequency(int rank) {
        int counter = 0;

        for (int i = 0; i < numberOfCards; i++) {
            if (rank == playerHand.get(i).getRank())
                counter++;
        }

        return counter;
    }

    private int getMaxFrequency() {
        int result = 0; 
        int temp;
        for (int i = 0; i < numberOfCards; i++) {
            temp = getRankFrequency(playerHand.get(i).getRank());
            if (temp > result) 
                result = temp;
        }


        return result;
    }

    private boolean isConsecutive() {
        boolean result = false;

        @SuppressWarnings("unchecked")  
        List<Card> tempList = (List<Card>) ((ArrayList<Card>) playerHand).clone();  // This is very important!
        List<Card> list = tempList; 
        if (getMaxFrequency() == 1) {
            Collections.sort(list, new CompareRanks());
            if (list.get(4).getRank() - list.get(0).getRank() == 4) 
                result = true;
        }

        return result;
    }

    class CompareRanks implements Comparator<Card> {
        // overide compare() method
        public int compare(Card o1, Card o2) {
            return o1.getRank() - o2.getRank();
        }
    }


    public void play() 
    {
    /** The main algorithm for single player poker game 
     *
     * Steps:
     * 		showPayoutTable()
     *
     * 		++	
     * 		show balance, get bet 
     *		verify bet value, update balance
     *		reset deck, shuffle deck, 
     *		deal cards and display cards
     *		ask for positions of cards to keep  
     *          get positions in one input line
     *		update cards
     *		check hands, display proper messages
     *		update balance if there is a payout
     *		if balance = O:
     *			end of program 
     *		else
     *			ask if the player wants to play a new game
     *			if the answer is "no" : end of program
     *			else : showPayoutTable() if user wants to see it
     *			goto ++
     */

        // implement this method!
        boolean continueLoop = true;
        boolean showTable = true;
        do {
            if (showTable)
                showPayoutTable();
            System.out.println("-----------------------------------");
            System.out.println("Balance:" + playerBalance);
            System.out.print("Enter bet:");
            Scanner scanner = new Scanner(System.in);
            playerBet = scanner.nextInt();
            oneDeck.reset();
            oneDeck.shuffle();
            try {
                playerHand = oneDeck.deal(numberOfCards);   // if not enough cards to deal, throw PlayingCardException
                System.out.println("Hand:" + playerHand);
                System.out.print("Enter positions of cards to keep (e.g. 1 4 5 ):");
                scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                updatePlayerHand(input);    // add a private method to update cards according to your options
        
                System.out.println("Hand:" + playerHand);
        
                checkHands();
       
                if (playerBalance > 0) {    // playerBalance is positive, you are able to choose whether to play one more game
                    System.out.print("\nYour balance:" + playerBalance + ", one more game(y or n)?");
                    String choice = scanner.next();
                    if (choice.equals("n")) {   // exit
                        continueLoop = false;
                    } else {    // one more game
                        System.out.print("\nWant to see payout table (y or n)");
                        choice = scanner.next();
                        showTable = choice.equals("n") ? false : true;
                    }
                } else {    // playerBalance is 0 or negative, GAME OVER!
                    continueLoop = false;
                    System.out.println("\nYour balance is 0");
                    System.out.println("Bye!");
                }
            } catch (PlayingCardException e) {
                System.out.println("*** In catch block:PlayingCardException:Error Msg: "+e.getMessage());
            }
        } while (continueLoop);
    }

    // add a private method to update cards according to your input
    private void updatePlayerHand(String input) {
        List<Integer> keepPosition = new ArrayList<Integer>();
        
        if (input.matches("\\d(\\s+\\d)*\\s*")) {    // if we don't want to keep any cards, the str and keepPostition would be empty
            String []  str = input.split("\\s+");   // one or more blanks

            for (int i = 0; i < str.length; i++) 
                keepPosition.add(Integer.parseInt(str[i]));
        }
          
        List<Integer> remove = new ArrayList<Integer>();
        int keepSize = keepPosition.size();
        for (int i = 0; i < numberOfCards; i++) {
            boolean flag = false;
            for (int j = 0; !flag && j < keepSize; j++) {
                if (i + 1 == keepPosition.get(j)) 
                    flag = true;
            } // end for
            if (flag == false)
                remove.add(i);
        } // end for
        int removeSize = remove.size();
        try {
            List<Card> updateCards = oneDeck.deal(removeSize);
            for (int i = 0; i < removeSize; i++) {
                playerHand.set(remove.get(i), updateCards.get(i));
            }
        } catch (PlayingCardException e) {
            System.out.println("*** In catch block:PlayingCardException:Error Msg: "+e.getMessage());
        }
    }

    /*************************************************
     *   Do not modify methods below
    /*************************************************


    /** testCheckHands() is used to test checkHands() method
     *  checkHands() should print your current hand type
     */
    public void testCheckHands()
    {
      	try {
    		playerHand = new ArrayList<Card>();

		// set Royal Flush
		playerHand.add(new Card(1,4));
		playerHand.add(new Card(10,4));
		playerHand.add(new Card(12,4));
		playerHand.add(new Card(11,4));
		playerHand.add(new Card(13,4));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Straight Flush
		playerHand.set(0,new Card(9,4));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Straight
		playerHand.set(4, new Card(8,2));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Flush 
		playerHand.set(4, new Card(5,4));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// "Royal Pair" , "Two Pairs" , "Three of a Kind", "Straight", "Flush	", 
	 	// "Full House", "Four of a Kind", "Straight Flush", "Royal Flush" };

		// set Four of a Kind
		playerHand.clear();
		playerHand.add(new Card(8,4));
		playerHand.add(new Card(8,1));
		playerHand.add(new Card(12,4));
		playerHand.add(new Card(8,2));
		playerHand.add(new Card(8,3));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Three of a Kind
		playerHand.set(4, new Card(11,4));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Full House
		playerHand.set(2, new Card(11,2));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Two Pairs
		playerHand.set(1, new Card(9,2));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// set Royal Pair
		playerHand.set(0, new Card(3,2));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");

		// non Royal Pair
		playerHand.set(2, new Card(3,4));
		System.out.println(playerHand);
    		checkHands();
		System.out.println("-----------------------------------");
      	}
      	catch (Exception e)
      	{
		System.out.println(e.getMessage());
      	}
    }

    /* Quick testCheckHands() */
    public static void main(String args[]) 
    {
	PokerGame pokergame = new PokerGame();
	pokergame.testCheckHands();
    }
}
