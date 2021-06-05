/*
A solver to place N queens on an NxN chess board so that none of them attack each other.
Additionally, no three queens are in a straight line at ANY angle
e.g. queens on A1, C2 and E3, despite not attacking each other, form a straight line at some angle.
 */
public class NQueensWithAngle {

    private final int n;
    private final int[] queenRows;
    private final boolean[] queenColumnsOccupied;

    /**
     * @param n size of board
     */
    public NQueensWithAngle(int n) {
        this.n = n;
        this.queenRows = new int[n];
        this.queenColumnsOccupied = new boolean[n];
    }

    /**
     * @return true if there was a solution
     */
    public boolean solve() {
        return solve(0);
    }

    private boolean solve(int row) {
next:   for (int column = 0; column < n; column++) {
            // a second queen on same column?
            if (queenColumnsOccupied[column]) {
                continue;
            }
            // a second queen on diagonal?
            for (int secondRow = 0; secondRow < row; secondRow++) {
                int secondColumn = queenRows[secondRow];
                int xDiff = column - secondColumn;
                int yDiff = row - secondRow;
                if (xDiff == yDiff || xDiff == -yDiff) {
                    continue next;
                }
            }
            // a third queen at same angle?
            for (int secondRow = 0; secondRow < row; secondRow++) {
                int secondColumn = queenRows[secondRow];
                int xDiff1 = column - secondColumn;
                int yDiff1 = row - secondRow;
                for (int thirdRow = 0; thirdRow < secondRow; thirdRow++) {
                    int thirdColumn = queenRows[thirdRow];
                    int xDiff2 = secondColumn - thirdColumn;
                    int yDiff2 = secondRow - thirdRow;
                    // for three points to be collinear, the slope between both pairs of points must be the same
                    // e.g. (y3 - y2) / (x3 - x2) = (y2 - y1) / (x2 - x1)
                    // which can be rearranged to
                    // (y3 - y2) * (x2 - x1) = (y2 - y1) * (x3 - x2)
                    if (yDiff2 * xDiff1  == xDiff2 * yDiff1) {
                        continue next;
                    }
                }
            }
            // can place queen here
            queenRows[row] = column;
            // no more queens to place?
            if (row + 1 == n) {
                return true;
            }
            // recursion to place the next queen
            queenColumnsOccupied[column] = true;
            if (solve(row + 1)) {
                return true;  // found a solution
            }
            // no legal complete solution yet, so backtrack and prepare for next column
            queenColumnsOccupied[column] = false;
        }
        return false;
    }
    
    public int getN() {
        return n;
    }
    
    public int getQueenColumn(int row) {
        return queenRows[row];
    }

    public String drawBoard() {
        StringBuilder ret = new StringBuilder();
        for (int queenColumn : queenRows) {
            for (int column = 0; column < n; column++) {
                ret.append(queenColumn == column ? 'Q' : '+');
            }
            ret.append('\n');
        }
        return ret.toString();
    }
}
