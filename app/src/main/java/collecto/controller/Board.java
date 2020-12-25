package collecto.controller;
import java.util.*;

public class Board {
    private Ball[][] fields; // organised as [row][col]
    private final int BOARDSIZE = 7;

	public Board() {
		this.fields = new Ball[BOARDSIZE][BOARDSIZE];
        generateStart(fields);
	}

    public Board(Ball[][] fields) {
        this.fields = new Ball[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i < BOARDSIZE-1; i++) {
            for (int j = 0; j < BOARDSIZE-1; j++) {
                this.fields[i][j] = fields[i][j];
            }
        }
    }

    private void generateStart(Ball[][] fields) {
        // Create list with starting balls
        ArrayList<Ball> collection = new ArrayList<Ball>();
        // Create and add balls to list
        for (int i = 0; i < 8; i++) {
            collection.add(Ball.BLUE);
            collection.add(Ball.YELLOW);
            collection.add(Ball.RED);
            collection.add(Ball.ORANGE);
            collection.add(Ball.PURPLE);
            collection.add(Ball.GREEN);
        }
        // Randomise list to randomise starting ball layout
        Collections.shuffle(collection);

        // Create queue out of list
        ArrayDeque<Ball> queue = new ArrayDeque<Ball>(collection);

        // Assign balls to fields
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                if (i == 3 && j == 3) {
                    fields[i][j] = Ball.NONE;
                    continue;
                }
                Ball b = queue.pop();
                fields[i][j] = b;
            }
        }

        ArrayDeque<Integer[]> swaps = findSwaps();
        while (swaps.size() > 0) {
            for (Integer[] swap : swaps) {
                swapWithRandom(swap[0], swap[1]);
            }
            swaps = findSwaps();
        }
    }

    public ArrayDeque<Integer[]> findSwaps() {
         // Create queue containing locations that need to be swapped
        ArrayDeque<Integer[]> swaps = new ArrayDeque<Integer[]>();

        // Find and add swap locations
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                ArrayList<Integer[]> adjacent = getAdjacent(i, j);
                for (Integer[] coords : adjacent) {
                    System.out.println(String.format("%d, %d"));
                    if (fields[coords[0]][coords[1]] == fields[i][j] && fields[i][j] != Ball.NONE) {
                        boolean toAdd = true;
                        for (Integer[] swap : swaps) {
                            if (swap[0] == i && swap[1] == j) {
                                toAdd = false;
                            }
                        }
                        if (toAdd)
                            swaps.add(new Integer[]{coords[0], coords[1], i, j});
                    }
                }
            }
        }
        return swaps;
    }

    private void swapWithRandom(int x, int y) {
        Random r = new Random();
        int x2 = r.nextInt(BOARDSIZE);
        int y2 = r.nextInt(BOARDSIZE);
        if (x2 == 3 && y2 == 3)
            return;
        Ball t = fields[x][y];
        fields[x][y] = fields[x2][y2];
        fields[x2][y2] = t;
    }

    public String toString() {
        String result = new String(new char[BOARDSIZE*7+1]).replace('\0', '-') + "\n";
        for (int i = 0; i < BOARDSIZE; i++) {
            String line = "|";
            for (int j = 0; j < BOARDSIZE; j++) {
                line += String.format("%6s|", fields[i][j]);
            }
            result += line + "\n";
        }
        result += new String(new char[BOARDSIZE*7+1]).replace('\0', '-');
        return result;
    }

    private ArrayList<Integer[]> getAdjacent(int i, int j) {
        ArrayList<Integer[]> result = new ArrayList<Integer[]>();
        if (i > 0)
            result.add(new Integer[]{i-1, j});
        if (j > 0)
            result.add(new Integer[]{i, j-1});
        if (i < BOARDSIZE - 2)
            result.add(new Integer[]{i+1, j});
        if (j < BOARDSIZE - 2)
            result.add(new Integer[]{i, j+1});
        return result;
    }

    public void pushUp(int j) {
        for (int i = 1; i < BOARDSIZE; i++) {
            int x = i;
            while (x > 0 && fields[x-1][j] == Ball.NONE) {
                fields[x-1][j] = fields[x][j];
                fields[x][j] = Ball.NONE;
                x--;
            }
        }
    }

    public void pushDown(int j) {
        for (int i = BOARDSIZE - 2; i >= 0; i--) {
            int x = i;
            while (x < BOARDSIZE - 1 && fields[x+1][j] == Ball.NONE) {
                fields[x+1][j] = fields[x][j];
                fields[x][j] = Ball.NONE;
                x++;
            }
        }
    }

    public void pushLeft(int i) {
        for (int j = 1; j < BOARDSIZE; j++) {
            int x = j;
            while (x > 0 && fields[i][j-1] == Ball.NONE) {
                fields[i][x-1] = fields[i][x];
                fields[i][x] = Ball.NONE;
                x--;
            }
        }
    }

    public void pushRight(int i) {
        for (int j = BOARDSIZE - 1; j >=0; j--) {
            int x = j;
            while (x < BOARDSIZE - 2 && fields[i][x+1] == Ball.NONE) {
                fields[i][x+1] = fields[i][x];
                fields[i][x] = Ball.NONE;
                x++;
            }
        }
    }

    public ArrayList<Ball> retrieveAdjacentBalls() {
        ArrayList<Ball> result = new ArrayList<Ball>();
        ArrayDeque<Integer[]> swaps = findSwaps();

        for (Integer[] swap : swaps) {
            if (fields[swap[0]][swap[1]] != Ball.NONE) {
                result.add(fields[swap[0]][swap[1]]);
                fields[swap[0]][swap[1]] = Ball.NONE;
            }
            if (fields[swap[2]][swap[3]] != Ball.NONE) {
                result.add(fields[swap[2]][swap[3]]);
                fields[swap[2]][swap[3]] = Ball.NONE;
            }
        }
        printSwaps(swaps);
        return result;
    }

    public void testBoard() {
        System.out.println(this);
        System.out.println(findPossibleSingleMoves());
    }


    // TODO: remove
    private void printSwaps(ArrayDeque<Integer[]> swaps) {
        for (Integer[] swap : swaps) {
            System.out.println(String.format("%d, %d - %d, %d", swap[0], swap[1], swap[2], swap[3]));
        }
    }

    public String ballListTostring(ArrayList<Ball> ballList) {
        String res = "";
        for (Ball ball : ballList) {
            res += ball + ", ";
        }
        return res;
    }

    public String findPossibleSingleMoves() {
        Board copy;
        String res = "";
        ArrayList<Ball> ballList;
        for (int i = 0; i < BOARDSIZE-1; i++) {
            boolean valid = false;
            for (int j = 0; j < BOARDSIZE-1; j++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy = deepcopy();
            copy.pushLeft(i);
            ballList = copy.retrieveAdjacentBalls();
            if  (ballList.size() > 0) {
                System.out.println(ballList.size());
                res += "L " + i + " " + ballListTostring(ballList) + "\n";
            }
            copy = deepcopy();
            copy.pushRight(i);
            ballList = copy.retrieveAdjacentBalls();
            if (ballList.size() > 0)
                res += "R " + i + " " + ballListTostring(ballList) + "\n";
        }
        for (int j = 0; j < BOARDSIZE-1; j++) {
            boolean valid = false;
            for (int i = 0; i < BOARDSIZE-1; i++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy = deepcopy();
            copy.pushUp(j);
            ballList = copy.retrieveAdjacentBalls();
            if  (ballList.size() > 0)
                res += "U " + j + " " + ballListTostring(ballList) + "\n";
            copy = deepcopy();
            copy.pushDown(j);
            ballList = copy.retrieveAdjacentBalls();
            if (ballList.size() > 0)
                res += "D " + j + " " + ballListTostring(ballList) + "\n";
        }
        return res;
    }

    public Board deepcopy() {
        return new Board(fields);
    }
}
