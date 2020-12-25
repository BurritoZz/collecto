package collecto.controller;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Board {
    private final Ball[][] fields; // organised as [row][col]
    private final int BOARDSIZE = 7;

	public Board() {
		this.fields = new Ball[BOARDSIZE][BOARDSIZE];
        generateStart(fields);
	}

    public Board(final Ball[][] fields) {
        this.fields = new Ball[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                this.fields[i][j] = fields[i][j];
            }
        }
    }

    private void generateStart(final Ball[][] fields) {
        // Create list with starting balls
        final ArrayList<Ball> collection = new ArrayList<Ball>();
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
        final ArrayDeque<Ball> queue = new ArrayDeque<Ball>(collection);

        // Assign balls to fields
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                if (i == 3 && j == 3) {
                    fields[i][j] = Ball.NONE;
                    continue;
                }
                final Ball b = queue.pop();
                fields[i][j] = b;
            }
        }

        ArrayDeque<Integer[]> swaps = findSwaps();
        while (swaps.size() > 0) {
            for (final Integer[] swap : swaps) {
                swapWithRandom(swap[0], swap[1]);
            }
            swaps = findSwaps();
        }
    }

    public ArrayDeque<Integer[]> findSwaps() {
         // Create queue containing locations that need to be swapped
        final ArrayDeque<Integer[]> swaps = new ArrayDeque<Integer[]>();

        // Find and add swap locations
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                final ArrayList<Integer[]> adjacent = getAdjacent(i, j);
                for (final Integer[] coords : adjacent) {
                    if (fields[coords[0]][coords[1]] == fields[i][j] && fields[i][j] != Ball.NONE) {
                        boolean toAdd = true;
                        for (final Integer[] swap : swaps) {
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

    private void swapWithRandom(final int x, final int y) {
        final Random r = new Random();
        final int x2 = r.nextInt(BOARDSIZE);
        final int y2 = r.nextInt(BOARDSIZE);
        if (x2 == 3 && y2 == 3)
            return;
        final Ball t = fields[x][y];
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

    private ArrayList<Integer[]> getAdjacent(final int i, final int j) {
        final ArrayList<Integer[]> result = new ArrayList<Integer[]>();
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

    public void pushUp(final int j) {
        for (int i = 1; i < BOARDSIZE; i++) {
            int x = i;
            while (x > 0 && fields[x-1][j] == Ball.NONE) {
                fields[x-1][j] = fields[x][j];
                fields[x][j] = Ball.NONE;
                x--;
            }
        }
    }

    public void pushDown(final int j) {
        for (int i = BOARDSIZE - 2; i >= 0; i--) {
            int x = i;
            while (x < BOARDSIZE - 1 && fields[x+1][j] == Ball.NONE) {
                fields[x+1][j] = fields[x][j];
                fields[x][j] = Ball.NONE;
                x++;
            }
        }
    }

    public void pushLeft(final int i) {
        for (int j = 1; j < BOARDSIZE; j++) {
            int x = j;
            while (x > 0 && fields[i][j-1] == Ball.NONE) {
                fields[i][x-1] = fields[i][x];
                fields[i][x] = Ball.NONE;
                x--;
            }
        }
    }

    public void pushRight(final int i) {
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
        final ArrayList<Ball> result = new ArrayList<Ball>();
        final ArrayDeque<Integer[]> swaps = findSwaps();

        for (final Integer[] swap : swaps) {
            if (fields[swap[0]][swap[1]] != Ball.NONE) {
                result.add(fields[swap[0]][swap[1]]);
                fields[swap[0]][swap[1]] = Ball.NONE;
            }
            if (fields[swap[2]][swap[3]] != Ball.NONE) {
                result.add(fields[swap[2]][swap[3]]);
                fields[swap[2]][swap[3]] = Ball.NONE;
            }
        }
        return result;
    }

    // TODO:Remove
    public void testBoard() {
        System.out.println(this);
        System.out.println(findPossibleDoubleMoves());
    }


    public String ballListTostring(final ArrayList<Ball> ballList) {
        String res = "";
        for (final Ball ball : ballList) {
            res += ball + ", ";
        }
        return res.substring(0, res.length() - 2);
    }

    public String findPossibleSingleMoves() {
        Board copy;
        String res = "";
        ArrayList<Ball> ballList;
        for (int i = 0; i < BOARDSIZE; i++) {
            boolean valid = false;
            for (int j = 0; j < BOARDSIZE; j++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy = clone();
            copy.pushLeft(i);
            ballList = copy.retrieveAdjacentBalls();
            if  (ballList.size() > 0) {
                res += "L " + i + " " + ballListTostring(ballList) + "\n";
            }
            copy = clone();
            copy.pushRight(i);
            ballList = copy.retrieveAdjacentBalls();
            if (ballList.size() > 0)
                res += "R " + i + " " + ballListTostring(ballList) + "\n";
        }
        for (int j = 0; j < BOARDSIZE; j++) {
            boolean valid = false;
            for (int i = 0; i < BOARDSIZE; i++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy = clone();
            copy.pushUp(j);
            ballList = copy.retrieveAdjacentBalls();
            if  (ballList.size() > 0)
                res += "U " + j + " " + ballListTostring(ballList) + "\n";
            copy = clone();
            copy.pushDown(j);
            ballList = copy.retrieveAdjacentBalls();
            if (ballList.size() > 0)
                res += "D " + j + " " + ballListTostring(ballList) + "\n";
        }
        return res;
    }

    public String findPossibleDoubleMoves() {
        Board copy1;
        Board copy2;
        String res = "";
        ArrayList<Ball> ballList;
        for (int i = 0; i < BOARDSIZE; i++) {
            boolean valid = false;
            for (int j = 0; j < BOARDSIZE; j++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy1 = clone();
            copy1.pushLeft(i);
            for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                boolean valid2 = false;
                for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushLeft(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("L%d L%d %s\n", i, i2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushRight(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("L%d R%d %s\n", i, i2, ballListTostring(ballList));
            }
            for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                boolean valid2 = false;
                for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushUp(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("L%d U%d %s\n", i, j2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushDown(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("L%d D%d %s\n", i, j2, ballListTostring(ballList));
            }
            copy1 = clone();
            copy1.pushRight(i);
            for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                boolean valid2 = false;
                for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushLeft(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("R%d L%d %s\n", i, i2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushRight(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("R%d R%d %s\n", i, i2, ballListTostring(ballList));
            }
            for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                boolean valid2 = false;
                for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushUp(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("R%d U%d %s\n", i, j2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushDown(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("R%d D%d %s\n", i, j2, ballListTostring(ballList));
            }
        }
        for (int j = 0; j < BOARDSIZE; j++) {
            boolean valid = false;
            for (int i = 0; i < BOARDSIZE; i++) {
                if (fields[i][j] == Ball.NONE)
                    valid = true;
            }
            if (!valid)
                continue;
            copy1 = clone();
            copy1.pushUp(j);
            for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                boolean valid2 = false;
                for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushLeft(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("U%d L%d %s\n", j, i2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushRight(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("U%d R%d %s\n", j, i2, ballListTostring(ballList));
            }
            for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                boolean valid2 = false;
                for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushUp(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("U%d U%d %s\n", j, j2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushDown(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("U%d D%d %s\n", j, j2, ballListTostring(ballList));
            }
            copy1 = clone();
            copy1.pushDown(j);
            for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                boolean valid2 = false;
                for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushLeft(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("D%d L%d %s\n", j, i2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushRight(i2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("D%d R%d %s\n", j, i2, ballListTostring(ballList));
            }
            for (int j2 = 0; j2 < BOARDSIZE; j2++) {
                boolean valid2 = false;
                for (int i2 = 0; i2 < BOARDSIZE; i2++) {
                    if (copy1.fields[i2][j2] == Ball.NONE)
                        valid2 = true;
                }
                if (!valid2)
                    continue;
                copy2 = copy1.clone();
                copy2.pushUp(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("D%d U%d %s\n", j, j2, ballListTostring(ballList));
                copy2 = copy1.clone();
                copy2.pushDown(j2);
                ballList = copy2.retrieveAdjacentBalls();
                if (ballList.size() > 0)
                    res += String.format("D%d D%d %s\n", j, j2, ballListTostring(ballList));
            }
        }
        return res;
    }

    public Board clone() {
        return new Board(fields);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(fields);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Board other = (Board) obj;
		if (!Arrays.deepEquals(fields, other.fields))
			return false;
		return true;
	}

}
