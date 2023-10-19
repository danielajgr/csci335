package checkers.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckerboardTest {
    public static Checkerboard fromMoves(String moves) {
        Checkerboard board = new Checkerboard();
        for (String move: moves.split("\n")) {
            int[] nums = move.chars().filter(c -> c >= '0' && c <= '9').map(c -> c - '0').toArray();
            Move m = new Move(board, nums[0], nums[1], nums[2], nums[3]);
            board.move(m);
        }
        return board;
    }

    @Test
    public void moveTest() {
        Checkerboard board = new Checkerboard();
        assertEquals(board.allRegularMoves(PlayerColor.RED).toString(), "[(2, 1) to (3, 2), (2, 1) to (3, 0), (2, 3) to (3, 4), (2, 3) to (3, 2), (2, 5) to (3, 6), (2, 5) to (3, 4), (2, 7) to (3, 6)]");
        assertEquals(board.allRegularMoves(PlayerColor.BLACK).toString(), "[(5, 0) to (4, 1), (5, 2) to (4, 3), (5, 2) to (4, 1), (5, 4) to (4, 5), (5, 4) to (4, 3), (5, 6) to (4, 7), (5, 6) to (4, 5)]");
        assertEquals(0, board.allCaptureMoves(PlayerColor.RED).size());
        assertEquals(0, board.allCaptureMoves(PlayerColor.BLACK).size());
    }

    @Test
    public void duplicateTest() {
        Checkerboard board = new Checkerboard();
        assertEquals(board, board.duplicate());
    }

    @Test
    public void test1() {
        Checkerboard board = fromMoves("""
(5, 2) to (4, 1)
(2, 5) to (3, 6)
(5, 6) to (4, 5)
(1, 6) to (2, 5)
(6, 3) to (5, 2)
(2, 5) to (3, 4)
(4, 1) to (3, 0)
(3, 4) to (5, 6)
(6, 7) to (4, 5)
(0, 5) to (1, 6)
(6, 5) to (5, 6)
(3, 6) to (4, 7)
(7, 4) to (6, 5)
(2, 7) to (3, 6)
(4, 5) to (2, 7)
(2, 7) to (0, 5)
(2, 3) to (3, 2)
(0, 5) to (2, 3)
(2, 3) to (4, 1)
(0, 7) to (1, 6)
(7, 2) to (6, 3)
(1, 6) to (2, 5)
(5, 6) to (4, 5)
(2, 5) to (3, 6)
(4, 5) to (2, 7)
(2, 1) to (3, 2)
(4, 1) to (2, 3)
(1, 2) to (3, 4)
(5, 2) to (4, 1)
(0, 3) to (1, 2)
(6, 1) to (5, 2)
(1, 0) to (2, 1)
(7, 6) to (6, 7)
(3, 4) to (4, 3)
(5, 4) to (3, 2)
(3, 2) to (1, 0)
(1, 2) to (2, 1)
(3, 0) to (1, 2)
(0, 1) to (2, 3)
(1, 0) to (0, 1)
(2, 3) to (3, 4)
(0, 1) to (1, 0)
(3, 4) to (4, 5)
(6, 5) to (5, 4)
(4, 5) to (5, 6)
(6, 7) to (4, 5)
(4, 7) to (5, 6)
(4, 1) to (3, 2)
(5, 6) to (6, 5)
(4, 5) to (3, 4)""");
        // AI Requested Illegal Move:  (6, 5) to (7, 4)
        System.out.println(board);
        System.out.println(board.getCurrentPlayerMoves());
    }

    @Test
    public void test2() {
        Checkerboard board = fromMoves("""
                (5, 2) to (4, 1)
                (2, 1) to (3, 0)
                (5, 4) to (4, 5)
                (3, 0) to (5, 2)
                (6, 1) to (4, 3)
                (2, 3) to (3, 4)
                (4, 5) to (2, 3)
                (1, 2) to (3, 4)
                (3, 4) to (5, 2)
                (6, 3) to (4, 1)
                (0, 3) to (1, 2)
                (5, 6) to (4, 5)
                (2, 5) to (3, 4)
                (4, 5) to (2, 3)
                (1, 4) to (3, 2)
                (4, 1) to (2, 3)
                (1, 2) to (3, 4)
                (6, 5) to (5, 4)
                (1, 0) to (2, 1)
                (7, 4) to (6, 3)
                (2, 7) to (3, 6)
                (6, 7) to (5, 6)
                (2, 1) to (3, 0)
                (7, 6) to (6, 7)
                (3, 6) to (4, 7)
                (6, 3) to (5, 2)
                (4, 7) to (6, 5)
                (5, 0) to (4, 1)
                """);
        // AI requested illegal move: (3, 4) to (4, 3)
        System.out.println(board);
        System.out.println(board.getCurrentPlayerMoves());
        System.out.println(board.getCurrentPlayerMoves().contains(new Move(board, 3, 4, 4, 3)));
    }

    @Test
    public void test3() {
        Checkerboard board = fromMoves("""
                (5, 4) to (4, 3)
                (2, 5) to (3, 4)
                (4, 3) to (2, 5)
                (1, 6) to (3, 4)
                (5, 0) to (4, 1)
                (2, 3) to (3, 2)
                (4, 1) to (2, 3)
                (1, 4) to (3, 2)
                (5, 2) to (4, 1)
                (3, 2) to (5, 0)
                (5, 6) to (4, 7)
                (0, 5) to (1, 6)
                (6, 1) to (5, 2)
                (1, 2) to (2, 3)
                (6, 5) to (5, 4)
                (2, 7) to (3, 6)
                (4, 7) to (2, 5)
                (2, 3) to (3, 2)
                (5, 4) to (4, 3)
                (3, 2) to (5, 4)
                (6, 3) to (4, 5)
                (4, 5) to (2, 3)
                (1, 6) to (3, 4)
                (5, 2) to (4, 3)
                (3, 4) to (5, 2)
                (2, 3) to (1, 2)
                (0, 1) to (2, 3)
                (7, 2) to (6, 3)
                (2, 3) to (3, 4)
                (6, 3) to (4, 1)
                (0, 3) to (1, 4)
                (6, 7) to (5, 6)
                (3, 4) to (4, 3)
                (4, 1) to (3, 0)
                (4, 3) to (5, 2)
                (3, 0) to (1, 2)
                (1, 0) to (2, 1)
                """);
        // Requested illegal move (1, 2) to (0, 1)
        // Alleged legal moves: [(1, 2) to (3, 0)]
        System.out.println(board);
        System.out.println(board.getCurrentPlayerMoves());
        System.out.println(board.getCurrentPlayerMoves().contains(new Move(board, 1, 2, 0, 1)));
    }

    @Test
    public void test4() {
        Checkerboard board = fromMoves("""
                (5, 6) to (4, 7)
                (2, 3) to (3, 4)
                (5, 2) to (4, 1)
                (1, 2) to (2, 3)
                (4, 1) to (3, 0)
                (0, 1) to (1, 2)
                (5, 4) to (4, 3)
                (3, 4) to (5, 2)
                (6, 3) to (4, 1)
                (2, 5) to (3, 4)
                (4, 7) to (3, 6)
                (2, 7) to (4, 5)
                (6, 7) to (5, 6)
                (4, 5) to (6, 7)
                (4, 1) to (3, 2)
                (2, 3) to (4, 1)
                (5, 0) to (3, 2)
                (2, 1) to (4, 3)
                (6, 5) to (5, 4)
                (4, 3) to (6, 5)
                (7, 4) to (5, 6)
                (1, 2) to (2, 1)
                (3, 0) to (1, 2)
                (0, 3) to (2, 1)
                (5, 6) to (4, 5)
                (3, 4) to (5, 6)
                (6, 1) to (5, 0)
                (1, 6) to (2, 7)
                (7, 0) to (6, 1)
                (2, 7) to (3, 6)
                (7, 6) to (6, 5)
                (5, 6) to (7, 4)
                (6, 1) to (5, 2)
                (0, 7) to (1, 6)
                (7, 2) to (6, 1)
                (3, 6) to (4, 7)
                (5, 2) to (4, 3)
                """);
        System.out.println(board);
    }

    @Test
    public void test5() {
        Checkerboard board = fromMoves("""
                (5, 4) to (4, 5)
                (2, 5) to (3, 4)
                (5, 6) to (4, 7)
                (3, 4) to (5, 6)
                (6, 7) to (4, 5)
                (1, 6) to (2, 5)
                (4, 7) to (3, 6)
                (2, 5) to (4, 7)
                (5, 0) to (4, 1)
                (2, 3) to (3, 4)
                (4, 5) to (2, 3)
                (1, 4) to (3, 2)
                (3, 2) to (5, 0)
                (6, 3) to (5, 4)
                (0, 5) to (1, 6)
                (7, 4) to (6, 3)
                (2, 1) to (3, 0)
                (7, 6) to (6, 7)
                (2, 7) to (3, 6)
                (6, 5) to (5, 6)
                (4, 7) to (6, 5)
                (5, 2) to (4, 3)
                (1, 6) to (2, 5)
                """);
        System.out.println(board);
    }

    @Test
    public void test6() {
        Checkerboard board = fromMoves("""
                (5, 6) to (4, 5)
                (2, 1) to (3, 0)
                (6, 7) to (5, 6)
                (1, 0) to (2, 1)
                (5, 4) to (4, 3)
                (0, 1) to (1, 0)
                (4, 5) to (3, 6)
                (2, 5) to (4, 7)
                (5, 0) to (4, 1)
                (2, 3) to (3, 2)
                (4, 1) to (2, 3)
                (2, 3) to (0, 1)
                (1, 4) to (2, 3)
                (5, 2) to (4, 1)
                (3, 0) to (5, 2)
                (6, 3) to (4, 1)
                (1, 6) to (2, 5)
                (4, 3) to (3, 4)
                (2, 3) to (4, 5)
                (4, 5) to (6, 7)
                (7, 2) to (6, 3)
                (0, 5) to (1, 6)
                (4, 1) to (3, 0)
                (2, 5) to (3, 4)
                (3, 0) to (1, 2)
                (0, 3) to (2, 1)
                (6, 5) to (5, 6)
                (4, 7) to (6, 5)
                (7, 4) to (5, 6)
                (3, 4) to (4, 3)
                (6, 3) to (5, 2)
                (2, 1) to (3, 2)
                (5, 2) to (3, 4)
                (1, 0) to (2, 1)
                (5, 6) to (4, 7)
                (2, 1) to (3, 0)
                (0, 1) to (1, 2)
                (3, 0) to (4, 1)
                (1, 2) to (0, 3)
                (4, 1) to (5, 0)
                (0, 3) to (1, 4)
                (5, 0) to (7, 2)
                (1, 4) to (2, 5)
                (7, 2) to (6, 3)
                (3, 4) to (2, 3)
                (1, 6) to (3, 4)
                (2, 3) to (1, 4)
                (6, 3) to (5, 4)
                (7, 0) to (6, 1)
                (3, 4) to (4, 3)
                (1, 4) to (2, 3)
                (3, 2) to (4, 1)
                (6, 1) to (5, 0)
                (5, 4) to (6, 3)
                (5, 0) to (3, 2)
                (0, 7) to (1, 6)
                (2, 3) to (3, 4)
                (6, 3) to (5, 4)
                (3, 4) to (5, 2)
                (5, 4) to (6, 5)
                (7, 6) to (5, 4)
                (1, 6) to (2, 5)
                """);
        System.out.println(board);
    }
}
