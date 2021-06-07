/*
Will Edwards 2021-05-05

A solver to place N queens on an NxN chess board so that none of them attack each other.
Additionally, no three queens are in a straight line at ANY angle:
e.g. queens on A1, C2 and E3, despite not attacking each other, form a straight line at some angle.

This solver implementation uses classic n-queens backtracking, using boolean masks to
propagate the occupied columns and diagonals.  Classic backtracking is a go-to algorithm for n-queens
up to about N=60 or so.

This implementation has a novel bias on the column evaluation order.  From top-left, the evaluation order
for an N=9 board is:

    123456789 first row is left-to-right
    --1234567 second row starts searching at prevColumn + 2
    6---12345 if we exhaust the left-to-right search, we continue searching right-to-left from prevColumn - 1
    654---123 there's no point ever evaluating prevColumn +- 1 as its in same column or diagonal as previous queen
    ...

The additional angle constraint is difficult to propagate efficiently.  For the small N that a backtracker can
cope with, its sufficiently efficient to simply exhaustively check the new queen position isn't collinear with
any two previously placed queens.  Simple benchmarking showed it is better to check the constraint as new queens
are considered, rather than generating a classic valid n-queens solution and then checking it also meets the angle
constraint (on the machine I used, with the cold JVM I used, etc.  Micro-optimisation theatre at its finest!)

As backtracking can cope only with small N, it is tempting to limit the max N to, say, 64.  This would allow us to
use 64-bit integers as boolean masks for the constraints etc, and these could be passed as arguments instead of by
reference, eliminating memory access and bounds checking.

interesting reading: https://sites.google.com/site/nqueensolver/home
 */
public class NQueensWithAngle {

    private final int n;
    private final int[] queenRows;
    private final boolean[] queenColumnsOccupied;
    private final boolean[] queenLeadingDiagonalOccupied;
    private final boolean[] queenCounterDiagonalOccupied;

    /**
     * @param n size of board
     */
    public NQueensWithAngle(int n) {
        this.n = n;
        this.queenRows = new int[n];
        this.queenColumnsOccupied = new boolean[n];
        this.queenLeadingDiagonalOccupied = new boolean[n * 2];
        this.queenCounterDiagonalOccupied = new boolean[n * 2];
    }

    /**
     * @return true if there was a solution
     */
    public boolean solve() {
        return solveRow(0, -2);  // start with first queen getting placed in first column
    }

    private boolean solveRow(int row, int prevColumn) {
        // biasing the check to start after prevColumn is a really strong heuristic
        for (int column = prevColumn + 2; column < n; column++) {
            if (solveColumn(row, column)) {
                return true;
            }
        }
        // and going backwards if we can't go forwards is much better than starting at 0 again
        for (int column = prevColumn - 1; column --> 0; ) {
            if (solveColumn(row, column)) {
                return true;
            }
        }
        return false;
    }

    private boolean solveColumn(int row, int column) {
        // classic n-queens constraints check
        int leadingIndex = row + column;
        int counterIndex = row - column + n;
        if (queenColumnsOccupied[column] ||
                queenLeadingDiagonalOccupied[leadingIndex] ||
                queenCounterDiagonalOccupied[counterIndex]) {
            return false;
        }
        // the angle check involves checking the new queen is not collinear with any pair of already-placed queens.
        // For three points to be collinear, the slope between both pairs of points must be the same
        // e.g. (y3 - y2) / (x3 - x2) = (y2 - y1) / (x2 - x1)
        // which can be rearranged to
        // (y3 - y2) * (x2 - x1) = (y2 - y1) * (x3 - x2)
        for (int row2 = 1; row2 < row; row2++) {
            int col2 = queenRows[row2];
            int xDiff1 = column - col2;
            int yDiff1 = row - row2;
            for (int row3 = 0; row3 < row2; row3++) {
                int col3 = queenRows[row3];
                int xDiff2 = col2 - col3;
                int yDiff2 = row2 - row3;
                if ((xDiff1 < 0) == (xDiff2 < 0) && // sloping same sign?
                        (yDiff2 * xDiff1) == (xDiff2 * yDiff1)) {  // check they are collinear
                    return false;
                }
            }
        }
        // queen can be placed, so DFS onwards
        queenRows[row] = column;
        queenColumnsOccupied[column] = true;
        queenLeadingDiagonalOccupied[leadingIndex] = true;
        queenCounterDiagonalOccupied[counterIndex] = true;
        if (row == n - 1) {
            return true;
        }
        if (solveRow(row + 1, column)) {
            return true;
        }
        queenColumnsOccupied[column] = false;
        queenLeadingDiagonalOccupied[leadingIndex] = false;
        queenCounterDiagonalOccupied[counterIndex] = false;
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
