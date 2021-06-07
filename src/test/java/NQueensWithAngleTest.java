import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NQueensWithAngleTest {

    @Test
    public void test() {
        // test takes about 8 seconds from cold on my 2019 i7 laptop
        for (int n = 10; n < 40; n++) {
            NQueensWithAngle test = new NQueensWithAngle(n);
            long startTs = System.currentTimeMillis();
            boolean ret = test.solve();
            System.out.println("n=" + n + " " + (System.currentTimeMillis() - startTs) + "ms");
            assertTrue(ret);
            System.out.println(test.drawBoard()); // print for manual inspection
            check(test);
        }
    }
    
    private void check(NQueensWithAngle solution) {
        // the checker uses a slightly different formulation of the same checks as the real solver,
        // in the hope that one finds bugs in the other.
        for (int row = 0; row < solution.getN(); row++) {
            int column = solution.getQueenColumn(row);
            for (int secondRow = 0; secondRow < row; secondRow++) {
                int secondColumn = solution.getQueenColumn(secondRow);
                int xDiff = secondColumn - column;
                if (xDiff == 0) {
                    fail("bad column " + column + " on rows " + row + " and " + secondRow);
                }
                int yDiff = secondRow - row;
                if (Math.abs(xDiff) == Math.abs(yDiff)) {
                    fail("bad diag xDiff=" + xDiff + ", yDiff=" + yDiff + " on rows " + row + " and " + secondRow);
                }
                // check for a third queen on the same angle
                for (int thirdRow = 0; thirdRow < secondRow; thirdRow++) {
                    int thirdColumn = solution.getQueenColumn(thirdRow);
                    // the area of three points on the same line will be zero
                    int areaOfTriangle =
                            column * (secondRow - thirdRow) +
                            secondColumn * (thirdRow - row) +
                            thirdColumn * (row - secondRow);
                    if (areaOfTriangle == 0) {
                        fail("bad triple xDiff=" + xDiff + ", yDiff=" + yDiff + " on rows " + row + ", " + secondRow + " and " + thirdRow);
                    }
                }
            }
        }
    }
}