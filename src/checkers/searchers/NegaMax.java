package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class NegaMax extends CheckersSearcher {

    private int numNodesExpanded = 0;

    public NegaMax(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodesExpanded;
    }

    public Duple<Integer, Move> NegaMaxF(Checkerboard board, int depthlim) {
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

            Duple<Integer,Move> news = NegaMaxF(nextmove, depthlim -1);

            if (currentp != nextmove.getCurrentPlayer()) {
                news = new Duple<>(news.getFirst() * -1, news.getSecond());
            }
            if (bestscore <= news.getFirst()) {
                bestscore = news.getFirst();
                best = nextmove.getLastMove();
            }
        }
        return new Duple<Integer, Move>(bestscore, best);
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return Optional.of(NegaMaxF(board, getDepthLimit()));
    }
}


// depth reached in 10 sec ranged from about 10 (first/second move) to about 7 (further into game)