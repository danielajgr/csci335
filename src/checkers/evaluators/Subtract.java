package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class Subtract implements ToIntFunction<Checkerboard>  {
    @Override
    public int applyAsInt(Checkerboard value) {
        PlayerColor currentP = value.getCurrentPlayer();
        PlayerColor opp;
        if (currentP == PlayerColor.RED) {
            opp = PlayerColor.BLACK;
        }
        else {
            opp = PlayerColor.RED;
        }
        return value.numPiecesOf(currentP) - value.numPiecesOf(opp);
    }

    // ideas:
    // kings count for +2 pieces
    // # of squares controlled in the middle of the board

    // pawn in opponents half is high value
    // pawn in our half is lower value
    // kings value is super high?
}
