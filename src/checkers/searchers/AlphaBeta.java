package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class AlphaBeta extends CheckersSearcher {

    private int numNodesExpanded = 0;

    public AlphaBeta (ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodesExpanded;
    }

    public Duple<Integer, Move> NegaMaxF(Checkerboard board, int depthlim, int alpha, int beta) {
        PlayerColor currentp = board.getCurrentPlayer();
        if (board.gameOver()) {
            if (board.playerWins(currentp)) {
                return new Duple<>(Integer.MAX_VALUE, board.getLastMove());
            }
            else if (board.playerWins(currentp.opponent())) {
                return new Duple<>(Integer.MAX_VALUE * -1, board.getLastMove());
            }
            else {
                return new Duple<>(0, board.getLastMove());
            }
        } else if (depthlim <= 0){
            return new Duple<>(getEvaluator().applyAsInt(board), board.getLastMove());
        }

        Move best = null;
        int bestscore = Integer.MAX_VALUE * -1;

        for (Move move : board.getCurrentPlayerMoves()) {
            Checkerboard nextmove = board.duplicate();
            numNodesExpanded++;
            nextmove.move(move);

            Duple<Integer,Move> news;
            if (currentp != nextmove.getCurrentPlayer()) {
                news = NegaMaxF(nextmove, depthlim -1, alpha, beta);
                news = new Duple<>(news.getFirst() * -1, news.getSecond());
            }
            else {
                news = NegaMaxF(nextmove, depthlim -1, alpha, beta);
            }
            if (bestscore <= news.getFirst()) {
                bestscore = news.getFirst();
                best = nextmove.getLastMove();
                if (bestscore >= alpha) {
                    alpha = bestscore;
                }
                if (alpha > beta) {
                    break;
                }
            }
        }
        return new Duple<>(bestscore, best);
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return Optional.of(NegaMaxF(board, getDepthLimit(), Integer.MAX_VALUE * -1, Integer.MAX_VALUE));
    }
}