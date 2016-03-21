package PJ4;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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



public class PokerGameGUI {

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

    // for GUI
    private JFrame frame;
    private JLabel labelA;
    private JLabel labelB;
    private JLabel labelC;
    private JLabel labelD;
    private JLabel labelE;
    private JTextField t0;
    private JTextField t1;
    private JTextField t2;
    private String pics;
    private ImageIcon icon;
    private List<Integer> keepPosition;
    private String handType;
    private boolean showHandType;   // This is for which deal you press the frame would show the result

    /** default constructor, set balance = startingBalance */
    public PokerGameGUI()
    {
	this(startingBalance);
    }

    /** constructor, set given balance */
    public PokerGameGUI(int balance)
    {
	this.playerBalance= balance;
    keepPosition = new ArrayList<Integer>();
    playerHand = new ArrayList<Card>();
    try {
        for (int i = 0; i < numberOfCards; i++)
            playerHand.add(new Card(1,1));
    } catch (PlayingCardException e) {
        System.out.println("PlayingCardException: "+e.getMessage());
    }
    showHandType = false;
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
                handType = "Royal Flush!";
                playerBalance *= multipliers[8];
            } else if (isConsecutive()) {
                handType = "Straight Flush!";
                playerBalance *= multipliers[7];
            } else {
                handType = "Flush!";
                playerBalance *= multipliers[4];
            }
        } else if (kindOfDenomination == 2) {   // all cards in hand are only 2 denominations
            if (getMaxFrequency() == 4) {
                handType = "Four of a Kind!";
                playerBalance *= multipliers[6];
            } else {
                handType = "Full House!";
                playerBalance *= multipliers[5];
            }
        } else if (isConsecutive()) {   // Straight!
            handType = "Straight!";
            playerBalance *= multipliers[3];
        } else if (kindOfDenomination == 3) {   // all cards in hand are only 3 denominations
            if (getMaxFrequency() == 3) {
                handType = "Three of a Kind!";
                playerBalance *= multipliers[2];
            } else {
                handType = "Two Pairs!";
                playerBalance *= multipliers[1];
            }
        } else if (kindOfDenomination == 4) {   // Royal Pair or Lost! (don't forget this case includes lost!)
            if (getRankFrequency(11) == 2 || getRankFrequency(12) == 2 || getRankFrequency(13) == 2 || getRankFrequency(1) == 2) {
                handType = "Royal Pair!";
                playerBalance *= multipliers[0];
            } else {
                handType = "Sorry, you lost!";
                playerBalance -= playerBet;
            }
        } else {    // all other cases are lost
            handType = "Sorry, you lost!";
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
        // override compare() method
        public int compare(Card o1, Card o2) {
            return o1.getRank() - o2.getRank();
        }
    }


    public void play() 
    {
   
        makeFrame();
        oneDeck.reset();
        oneDeck.shuffle();
    }

    // add a private method to update cards according to your input
    private void updatePlayerHand() {
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
            System.exit(0);
        }
    }


    // ---------------------------------------- GUI ---------------------------------------------------------------------------------//
    
    public void makeFrame() {
        frame = new JFrame("VIDEO POKER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setLayout(new GridBagLayout());

        JPanel n0 = new JPanel();
        n0.setBackground(Color.LIGHT_GRAY);
        n0.setLayout(new GridBagLayout());
        GridBagConstraints cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 0;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(n0, cns);

        JPanel n1 = new JPanel();
        n1.setBackground(Color.RED);
        n1.setLayout(new GridBagLayout());
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 1;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(n1, cns);

        JPanel n2 = new JPanel();
        n2.setBackground(Color.LIGHT_GRAY);
        n2.setLayout(new GridBagLayout());
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 2;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(n2, cns);

        /////////////////////////////////////////////////////////////////////////////////////////

        JLabel label0 = new JLabel("                                                                            Payout Table          Multiplier");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 0;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label0, cns);

        JLabel label1 = new JLabel("                                                                            =======================================");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 1;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label1, cns);

        JLabel label2 = new JLabel("                                                                            Royal Flush                        250");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 2;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label2, cns);

        JLabel label3 = new JLabel("                                                                            Straight Flush                      50");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 3;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label3, cns);

        JLabel label4 = new JLabel("                                                                            Four of a Kind                      25");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 4;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label4, cns);

        JLabel label5 = new JLabel("                                                                            Full House                              9");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 5;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label5, cns);
        
        JLabel label6 = new JLabel("                                                                            Flush                                       6");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 6;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label6, cns);

        JLabel label7 = new JLabel("                                                                            Straight                                  5");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 7;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label7, cns);

        JLabel label8 = new JLabel("                                                                            Three of a Kind                     3");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 8;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label8, cns);

        JLabel label9 = new JLabel("                                                                            Two Pairs                              2");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 9;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label9, cns);

        JLabel label10 = new JLabel("                                                                            Royal Pair                              1");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 10;
        cns.weightx = 1;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n0.add(label10, cns); 

   //////////////////////////////////////////////////////////////////////////////////////////////    

        pics = "../Pics/Back.jpg";
        icon = new ImageIcon(pics);
        labelA = new JLabel(icon);
        cns = new GridBagConstraints();
        cns.gridx = 1;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n1.add(labelA, cns);

        labelB = new JLabel(icon);
        cns = new GridBagConstraints();
        cns.gridx = 2;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n1.add(labelB, cns);

        labelC = new JLabel(icon);
        cns = new GridBagConstraints();
        cns.gridx = 3;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n1.add(labelC, cns);

        labelD = new JLabel(icon);
        cns = new GridBagConstraints();
        cns.gridx = 4;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n1.add(labelD, cns);

        labelE = new JLabel(icon);
        cns = new GridBagConstraints();
        cns.gridx = 5;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.BOTH;
        n1.add(labelE, cns);

        // ------------------------------------------------------------------------//
        JButton button0 = new JButton("HOLD");
        button0.addActionListener(new hold0Listener());
        cns = new GridBagConstraints();
        cns.gridx = 1;
        cns.gridy = 1;
        cns.weightx = 0.2;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n1.add(button0, cns);

        JButton button1 = new JButton("HOLD");
        button1.addActionListener(new hold1Listener());
        cns = new GridBagConstraints();
        cns.gridx = 2;
        cns.gridy = 1;
        cns.weightx = 0.2;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n1.add(button1, cns);

        JButton button2 = new JButton("HOLD");
        button2.addActionListener(new hold2Listener());
        cns = new GridBagConstraints();
        cns.gridx = 3;
        cns.gridy = 1;
        cns.weightx = 0.2;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n1.add(button2, cns);

        JButton button3 = new JButton("HOLD");
        button3.addActionListener(new hold3Listener());
        cns = new GridBagConstraints();
        cns.gridx = 4;
        cns.gridy = 1;
        cns.weightx = 0.2;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n1.add(button3, cns);

        JButton button4 = new JButton("HOLD");
        button4.addActionListener(new hold4Listener());
        cns = new GridBagConstraints();
        cns.gridx = 5;
        cns.gridy = 1;
        cns.weightx = 0.2;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n1.add(button4, cns);

        ///////////////////////////////////////////////////////////////////////////////////

        JLabel label11 = new JLabel("BALANCE");
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n2.add(label11, cns);

        t0 = new JTextField();
        t0.setText(Integer.toString(playerBalance));
        cns = new GridBagConstraints();
        cns.gridx = 1;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.HORIZONTAL;
        n2.add(t0, cns);

        JLabel label12 = new JLabel("BET");
        cns = new GridBagConstraints();
        cns.gridx = 2;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n2.add(label12, cns);

        t1 = new JTextField();
        t1.addActionListener(new InputListener());
        cns = new GridBagConstraints();
        cns.gridx = 3;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.HORIZONTAL;
        n2.add(t1, cns);

        JLabel label13 = new JLabel("HAND TYPE");
        cns = new GridBagConstraints();
        cns.gridx = 4;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n2.add(label13, cns);

        t2 = new JTextField();
        t2.setText("Please enter bet (and then press ENTER key)!");
        cns = new GridBagConstraints();
        cns.gridx = 5;
        cns.gridy = 0;
        cns.weightx = 0.5;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.HORIZONTAL;
        n2.add(t2, cns);

        JButton button5 = new JButton("DEAL");
        button5.addActionListener(new dealListener());
        cns = new GridBagConstraints();
        cns.gridx = 6;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n2.add(button5, cns); 

        JButton button6 = new JButton("EXIT");
        button6.addActionListener(new exitListener());
        cns = new GridBagConstraints();
        cns.gridx = 7;
        cns.gridy = 0;
        cns.weightx = 0.125;
        cns.weighty = 0.1;
        cns.anchor = GridBagConstraints.CENTER;
        cns.fill = GridBagConstraints.NONE;
        n2.add(button6, cns); 

        frame.setVisible(true);
    }

    private class InputListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //JTextField t5 = (JTextField) GUIComponent.get(7);
            String userInput = t1.getText();
            playerBet = Integer.parseInt(userInput);
           // System.out.println(playerBet);
        }
    }

    private class hold0Listener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            keepPosition.add(1);
        }
    }

    private class hold1Listener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            keepPosition.add(2);
        }
    }

     private class hold2Listener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            keepPosition.add(3);
        }
    }

     private class hold3Listener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            keepPosition.add(4);
        }
    }

     private class hold4Listener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            keepPosition.add(5);
        }
    }

    private class dealListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
                updatePlayerHand();         // add a private method to update cards according to your options

                pics = "../Pics/" + Card.Suit[playerHand.get(0).getSuit() - 1] + " " + Card.Rank[playerHand.get(0).getRank() - 1] + ".jpg";
                icon = new ImageIcon(pics);
                labelA.setIcon(icon);

                pics = "../Pics/" + Card.Suit[playerHand.get(1).getSuit() - 1] + " " + Card.Rank[playerHand.get(1).getRank() - 1] + ".jpg";
                icon = new ImageIcon(pics);
                labelB.setIcon(icon);

                pics = "../Pics/" + Card.Suit[playerHand.get(2).getSuit() - 1] + " " + Card.Rank[playerHand.get(2).getRank() - 1] + ".jpg";
                icon = new ImageIcon(pics);
                labelC.setIcon(icon);

                pics = "../Pics/" + Card.Suit[playerHand.get(3).getSuit() - 1] + " " + Card.Rank[playerHand.get(3).getRank() - 1] + ".jpg";
                icon = new ImageIcon(pics);
                labelD.setIcon(icon);

                pics = "../Pics/" + Card.Suit[playerHand.get(4).getSuit() - 1] + " " + Card.Rank[playerHand.get(4).getRank() - 1] + ".jpg";
                icon = new ImageIcon(pics);
                labelE.setIcon(icon);

                if (showHandType == true)
                    checkHands();

                keepPosition.clear();
                if (!showHandType) {
                    t2.setText("Please choose which card(s) to HOLD.");
                    t0.setText(Integer.toString(playerBalance));
                    showHandType = true;
                } else if (playerBalance > 0) {
                    t0.setText(Integer.toString(playerBalance));
                    t2.setText(handType);
                    showHandType = false;
                } else {
                    t0.setText(Integer.toString(playerBalance));
                    t2.setText(handType + "\tYour balance is 0.  Bye!");
                    System.out.println("\nYour balance is 0");
                    System.out.println("Bye!");
                    System.exit(0);
                }
                
        }
    } 

      private class exitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }

   
    public static void main(String args[]) 
    {
        PokerGameGUI pokergameGUI;
        if (args.length > 0)
            pokergameGUI = new PokerGameGUI(Integer.parseInt(args[0]));
        else
            pokergameGUI = new PokerGameGUI();
        pokergameGUI.play();
    }
}
