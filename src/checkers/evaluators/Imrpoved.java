package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class Imrpoved implements ToIntFunction<Checkerboard>  {
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
        int totalOppKings= value.numKingsOf(opp);
        System.out.println(totalOppKings);
        int totalcurrPKings = value.numKingsOf(currentP);
        System.out.println(totalcurrPKings);

        return value.numPiecesOf(currentP)+(2*totalcurrPKings) - value.numPiecesOf(opp)-totalcurrPKings;
    }

    // ideas:
    // kings count for +2 pieces
    // # of squares controlled in the middle of the board

    // pawn in opponents half is high value
    // pawn in our half is lower value
    // kings value is super high?
}
