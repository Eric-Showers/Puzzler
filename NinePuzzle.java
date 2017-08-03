/* NinePuzzle.java
   CSC 225 - Spring 2017
   Assignment 4 - Template for the 9-puzzle

   This template includes some testing code to help verify the implementation.
   Input boards can be provided with standard input or read from a file.

   To provide test inputs with standard input, run the program with
	java NinePuzzle
   To terminate the input, use Ctrl-D (which signals EOF).

   To read test inputs from a file (e.g. boards.txt), run the program with
    java NinePuzzle boards.txt

   The input format for both input methods is the same. Input consists
   of a series of 9-puzzle boards, with the '0' character representing the
   empty square. For example, a sample board with the middle square empty is

    1 2 3
    4 0 5
    6 7 8

   And a solved board is

    1 2 3
    4 5 6
    7 8 0

   An input file can contain an unlimited number of boards; each will be
   processed separately.

   B. Bird    - 07/11/2014
   M. Simpson - 11/07/2015
*/

import java.util.*;
import java.io.File;


public class NinePuzzle{

	//The total number of possible boards is 9! = 1*2*3*4*5*6*7*8*9 = 362880
	public static final int NUM_BOARDS = 362880;

	/*  SolveNinePuzzle(B)
		Given a valid 9-puzzle board (with the empty space represented by the
		value 0),return true if the board is solvable and false otherwise.
		If the board is solvable, a sequence of moves which solves the board
		will be printed, using the printBoard function below.
	*/
	public static boolean SolveNinePuzzle(int[][] B){

		/* Boards may only be adjacent to AT MOST 4 other boards. The indices
		in adjList correspond to:

		[Board i][ZDOWN, ZUP, ZRIGHT, ZLEFT, Visited, Parent]
		[Board i+1]
		[Board i+2]
		.
		.
		.      */


		int[][] adjList = new int[NUM_BOARDS][6];


	/**************************************************************************************
		Create the graph containing all possible game states (size is NUM_BOARDS)
	**************************************************************************************/

		/*Iterates once for every possible board. Finds the possible moves from
			that board, then stores those board id's in the appropriate adjList
			tag indice. If a move is not possible, that indice in adjList is set
			to -1.*/
		for (int i=0; i<NUM_BOARDS; i++){
			int[][] curBoard = getBoardFromIndex(i);

			int[] zeroLoc = findZero(curBoard);
			int zeroY = zeroLoc[0];
			int zeroX = zeroLoc[1];

			if (zeroY > 0){
				//Connect gameGraph[i] to board created by moving 0 down one row
				int[][] zeroDown = dupBoard(curBoard);
				zeroDown[zeroY][zeroX] = curBoard[zeroY-1][zeroX];
				zeroDown[zeroY-1][zeroX] = 0;

				int downIndex = getIndexFromBoard(zeroDown);
				adjList[i][0] = downIndex;
			} else {
				adjList[i][0] = -1;
			}
			if (zeroY < 2){
				//Connect gameGraph[i] to board created by moving 0 up one row
				int[][] zeroUp = dupBoard(curBoard);
				zeroUp[zeroY][zeroX] = curBoard[zeroY+1][zeroX];
				zeroUp[zeroY+1][zeroX] = 0;

				int upIndex = getIndexFromBoard(zeroUp);
				adjList[i][1] = upIndex;
			} else {
				adjList[i][1] = -1;
			}
			if (zeroX < 2){
				//Connect gameGraph[i] to board created by moving 0 right one column
				int[][] zeroRight = dupBoard(curBoard);
				zeroRight[zeroY][zeroX] = curBoard[zeroY][zeroX+1];
				zeroRight[zeroY][zeroX+1] = 0;

				int rightIndex = getIndexFromBoard(zeroRight);
				adjList[i][2] = rightIndex;
			} else {
				adjList[i][2] = -1;
			}
			if (zeroX > 0){
				//Connect gameGraph[i] to board created by moving 0 left one column
				int[][] zeroLeft = dupBoard(curBoard);
				zeroLeft[zeroY][zeroX] = curBoard[zeroY][zeroX-1];
				zeroLeft[zeroY][zeroX-1] = 0;

				int leftIndex = getIndexFromBoard(zeroLeft);
				adjList[i][3] = leftIndex;
			} else {
				adjList[i][3] = -1;
			}
		}
		/***********************************************************
							End graph construction
		************************************************************/

		//Get the start and target indices
		int startIndex = getIndexFromBoard(B);
		int[][] endBoard = new int[][] {
			{1,2,3},
			{4,5,6},
			{7,8,0}
		};
		int endIndex = getIndexFromBoard(endBoard);
		boolean solvable;

		//Call BFS method
		solvable = BFSpath(adjList, endIndex, startIndex);
		return solvable;
	}
	/*Does a BFS from start, tracking each nodes parent in the traversal. Once
	 	target is found it iterates through the parents of every node from
		target-start, printing at each node. */
	public static boolean BFSpath(int[][] adjList, int start, int target){
		LinkedList<Integer> q = new LinkedList<Integer>();
		int w = start;
		//Visit first node
		adjList[w][4] = 1;
		q.add(w);

		while (q.size() !=0){
			//If target node found print node, then parent, until node=start
			if (w == target){
				while (w != start){
					printBoard(getBoardFromIndex(w));
					w = adjList[w][5];
				}
				printBoard(getBoardFromIndex(start));
				return true;
			}
			//Visit each node adjacent to w
			for (int i=0; i<4; i++) {

				int v = adjList[w][i];
				if (v != -1 && adjList[v][4] != 1){
					//Set the visit and parent tags for v, then queue
					adjList[v][4] = 1;
					adjList[v][5] = w;
					q.add(v);
				}
			}
			//Get next node in BFS
			w = q.poll();
		}
		return false;
	}
	//When given a board, returns the coordinates of the zero
	public static int[] findZero(int[][] B){
		int[] zeroIndex = new int[2];
		for (int i=0; i<3; i++){
			for (int j=0; j<3; j++){
				if (B[i][j] == 0){
					zeroIndex[0] = i;
					zeroIndex[1] = j;
					return zeroIndex;
				}
			}
		}
		return null;
	}
	//Duplicates a board by iterating through each vertice
	public static int[][] dupBoard(int[][] B){
		int [][] newBoard = new int[3][3];
		for (int i=0; i<3; i++){
			for (int j=0; j<3; j++){
				newBoard[i][j] = B[i][j];
			}
		}
		return newBoard;
	}

	/*  printBoard(B)
		Print the given 9-puzzle board. The SolveNinePuzzle method above should
		use this method when printing the sequence of moves which solves the input
		board. If any other method is used (e.g. printing the board manually), the
		submission may lose marks.
	*/
	public static void printBoard(int[][] B){
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++)
				System.out.printf("%d ",B[i][j]);
			System.out.println();
		}
		System.out.println();
	}


	/* Board/Index conversion functions
	   These should be treated as black boxes (i.e. don't modify them, don't worry about
	   understanding them). The conversion scheme used here is adapted from
		 W. Myrvold and F. Ruskey, Ranking and Unranking Permutations in Linear Time,
		 Information Processing Letters, 79 (2001) 281-284.
	*/
	public static int getIndexFromBoard(int[][] B){
		int i,j,tmp,s,n;
		int[] P = new int[9];
		int[] PI = new int[9];
		for (i = 0; i < 9; i++){
			P[i] = B[i/3][i%3];
			PI[P[i]] = i;
		}
		int id = 0;
		int multiplier = 1;
		for(n = 9; n > 1; n--){
			s = P[n-1];
			P[n-1] = P[PI[n-1]];
			P[PI[n-1]] = s;

			tmp = PI[s];
			PI[s] = PI[n-1];
			PI[n-1] = tmp;
			id += multiplier*s;
			multiplier *= n;
		}
		return id;
	}

	public static int[][] getBoardFromIndex(int id){
		int[] P = new int[9];
		int i,n,tmp;
		for (i = 0; i < 9; i++)
			P[i] = i;
		for (n = 9; n > 0; n--){
			tmp = P[n-1];
			P[n-1] = P[id%n];
			P[id%n] = tmp;
			id /= n;
		}
		int[][] B = new int[3][3];
		for(i = 0; i < 9; i++)
			B[i/3][i%3] = P[i];
		return B;
	}


	public static void main(String[] args){
		/* Code to test your implementation */
		/* You may modify this, but nothing in this function will be marked */


		Scanner s;

		if (args.length > 0){
			//If a file argument was provided on the command line, read from the file
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			//Otherwise, read from standard input
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}

		int graphNum = 0;
		double totalTimeSeconds = 0;

		//Read boards until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading board %d\n",graphNum);
			int[][] B = new int[3][3];
			int valuesRead = 0;
			for (int i = 0; i < 3 && s.hasNextInt(); i++){
				for (int j = 0; j < 3 && s.hasNextInt(); j++){
					B[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < 9){
				System.out.printf("Board %d contains too few values.\n",graphNum);
				break;
			}
			System.out.printf("Attempting to solve board %d...\n",graphNum);
			long startTime = System.currentTimeMillis();
			boolean isSolvable = SolveNinePuzzle(B);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;

			if (isSolvable)
				System.out.printf("Board %d: Solvable.\n",graphNum);
			else
				System.out.printf("Board %d: Not solvable.\n",graphNum);
		}
		graphNum--;
		System.out.printf("Processed %d board%s.\n Average Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>1)?totalTimeSeconds/graphNum:0);

	}

}
