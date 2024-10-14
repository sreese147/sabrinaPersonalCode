/*Sabrina Reese
* Connect Four
* Finished 10/14/24
*/

import java.util.Arrays;
import java.util.Scanner;


public class ConnectFour {

    /************
    * VARIABLES *
    ************/
    //gameboard. this will be printed
    private static String[][] gameBoard = new String[6][7];
    //how many x and os are in a row
    private static int xScore, oScore;
    //used to determine if anyone wins, and if its player 1
    private static boolean win = false;
    private static boolean play1Win = false;
    //used for undo.
    private static int[] lastMove = {-1, -1};
    //a list of correct values the user can enter on move
    private static final String[] VALUES = {"1", "2", "3", "4", "5", "6", "7"};

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        //Get the player's usernames
        System.out.print("Enter player 1's username: ");
        String play1 = scan.nextLine();
        System.out.print("Enter player 2's username: ");
        String play2 = scan.nextLine();

        //Set up inital screen
        board();
        System.out.println("Welcome to Connect four");
        System.out.println("Type rules (or r) on your move to reread how to play");

        //Each player has a move. Run through the move, and check if the player wins.
        do {
            if (move(play1, play2, 0, scan)) {
                break;
            }
            if (move(play2, play1, 1, scan)) {
                break;
            }
        } while (!win);

        //Print the winning player
        System.out.println(play1Win ? "Congrats! " + play1 + ", you win!" : "Congrats! " + play2 + ", you win!");
        scan.close();
    }


    //Print the board
    public static void board() {

        //Clear the board + print the horizontal line #s
        System.out.print("\033[H\033[2J");
        System.out.println("   1    2    3    4    5    6    7");

        //Print each game position
        for (int x = 0; x < 6; x++) {
            System.out.print((x + 1));
            for (int y = 0; y < 7; y++) {
                System.out.print(" " + gameBoard[x][y]);
            }
            System.out.println();
        }
    }

    //Just a bunch of print statements about the rules
    public static void rules() {
        System.out.println("Be the first player to get four circles in a row.");
        System.out.println("You can do this horizontally, vertically, or diagonally.");
        System.out.println("Players enter their desired columns, and discs drop down to the lowest open row.");
        System.out.println("If you mistype your column, type undo after your turn to choose again.");
    }

    //Move.
    public static boolean move(String mainPlayer, String otherPlayer, int player, Scanner scan) {
        System.out.print(mainPlayer + ": Your move: ");
        String moveInputString = scan.nextLine().toLowerCase();

        //Until user enters a correct row number, keep running through code
        while (!Arrays.stream(VALUES).anyMatch(moveInputString::equals)) {

            //Because I have made far too many mistakes in connect four that my mom has graciously let me redo
            if (moveInputString.contentEquals("undo") || moveInputString.contentEquals("u")) {
                //If it is the first move, there is nothing to undo
                if (lastMove[0] == -1) {
                    System.out.println("No pieces are placed! You can't undo");
                    System.out.print(mainPlayer + ": Your move: ");
                } else {
                    //call the move again, except it is the other player's turn.
                    //Additonally, the previous move is now deleted
                    System.out.println(otherPlayer + ": So sad you have chosen to undo your move. Enter your new row");
                    gameBoard[lastMove[0]][lastMove[1]] = null;
                    move(otherPlayer, mainPlayer, (player + 1) % 2, scan);
                    board();

                    //check to see if the undo is a win
                    scoreDirection();
                    if (win) {
                        break;
                    }
                    //Reprompts the original user for input
                    System.out.print(mainPlayer + ": Your move: ");
                }
            } else if (moveInputString.contentEquals("rules") || moveInputString.contentEquals("r")) {
                //Print the rules and reprompt the user
                rules();
                System.out.print(mainPlayer + ": Your move: ");
            } else {
                System.out.print("You entered something wrong. Try again: ");
            }
            moveInputString = scan.nextLine();
        }
        int moveInput = Integer.parseInt(moveInputString);

        //run through each column spot. as in connect four, the piece drops to the bottom
        int rowPlayer = 6;
        do {
            rowPlayer--;
            //if the row is full, it reprompts the users and resets the row.
            if (rowPlayer == -1) {
                System.out.print("This row is full. Try again.");
                moveInputString = scan.nextLine().toLowerCase();

                while (!Arrays.stream(VALUES).anyMatch(moveInputString::equals)) {
                    //Because I have made far too many mistakes in connect four that my mom has graciously let me redo
                    if (moveInputString.contentEquals("undo") || moveInputString.contentEquals("u")) {
                        System.out.println(otherPlayer
                            + ": So sad you have chosen to undo your move. Enter your new row");
                        gameBoard[lastMove[0]][lastMove[1]] = null;
                        move(otherPlayer, mainPlayer, (player + 1) % 2, scan);
                        board();
                        scoreDirection();
                        if (win) {
                            break;
                        }
                        //Reprompts the original user for input
                        System.out.print(mainPlayer + ": Your move: ");

                    } else if (moveInputString.contentEquals("rules") || moveInputString.contentEquals("r")) {
                        rules();
                        System.out.print(mainPlayer + ": Your move: ");
                    } else {
                        System.out.print("You entered something wrong. Try again: ");
                    }
                    moveInputString = scan.nextLine();
                }
                moveInput = Integer.parseInt(moveInputString);
                rowPlayer = 5;
            }
        } while (gameBoard[rowPlayer][moveInput - 1] != null);

        //set the latest move in the gameboard and last move
        gameBoard[rowPlayer][moveInput - 1] = player == 0 ? "\u001B[31m O  \u001B[0m" : "\u001B[33m X  \u001B[0m";
        lastMove[0] = rowPlayer;
        lastMove[1] = moveInput - 1;
        board();

        //check if anyone wins
        scoreDirection();
        if (win) {
            if (player == 0) {
                play1Win = true;
            }
            return true;
        }
        return false;
    }

    //Run through each possible winning direction - 34 lines of code(I think.)
    public static void scoreDirection() {
        for (String[] row : gameBoard) {
            //Check for horizontal wins by scanning both arrays in the 2d array then each spot in the array.
            for (String spot : row) {
                scoreCounter(spot);
            }
            xScore = 0;
            oScore = 0;
        } for (int col = 0; col < 7; col++) {
            //Check for vertical wins.
            for (String[] row : gameBoard) {
                scoreCounter(row[col]);
            }
            xScore = 0;
            oScore = 0;
        } for (int index = 0; index < 6; index++) {
            //Check for diagonal wins start from left top and going right.
            for (int index1 = 0; index1 < 7; index1++) {
                try {
                    scoreCounter(gameBoard[index1][index1 + index]);
                } catch (Exception e) { }
            }
            xScore = 0;
            oScore = 0;
        } for (int index = 0; index < 7; index++) {
            //Check for the rest of diagonal wins start from left top and going left.
            for (int index1 = 0; index1 < 7; index1++) {
                try {
                    scoreCounter(gameBoard[index1][index1 - index]);
                } catch (Exception e) { }
            }
            xScore = 0;
            oScore = 0;
        } for (int index = 7; index > -1; index--) {
            //Check for diagonal wins start from left bottom and going up.
            for (int index1 = 0; index1 < 6; index1++) {
                try {
                    scoreCounter(gameBoard[index - index1][index1]);
                } catch (Exception e) { }
            }
            xScore = 0;
            oScore = 0;
        } for (int index = 0; index < 2; index++) {
            //Check for diagonal wins start from left bottom and going right.
            for (int index1 = 0; index1 < 5; index1++) {
                try {
                    scoreCounter(gameBoard[index - index1][index1]);
                } catch (Exception e) { }
            }
            xScore = 0;
            oScore = 0;
        }
    }

    //Check the number of Xs or Os in one line- 15 lines of code
    public static void scoreCounter(String spot) {

        if (spot != null && spot.contains("\u001B[33m")) {
            //if there is a X at the spot, then the xScore is increased and the oScore is reset to 0
            xScore++;
            oScore = 0;
        } else if (spot != null && spot.contains("\u001B[31m")) {
            //if there is an O at the spot, then the oScore is increased and the XScore is reset to 0
            oScore++;
            xScore = 0;
        } else {
            //if the spot is null, both scores are reset to 0.
            xScore = 0;
            oScore = 0;
        }
        if (xScore == 4 || oScore == 4) {
            //if someone got a four in a row, the win is set to true.
            win = true;
        }
    }

}
