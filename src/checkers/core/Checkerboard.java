package checkers.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class Checkerboard {

    private final boolean debug = false;

    private final int sideSquares = 8;

    private final int numSquares = (sideSquares * sideSquares) / 2;
    private final int numStartRows = (sideSquares - 2) / 2;
    private final int numStartingPieces = numStartRows * sideSquares / 2;

    private PlayerColor currentPlayer;
    private boolean turnRepeating;
    private int repeatingRow;
    private int repeatingCol;
    private Optional<Piece> board[] = (Optional<Piece>[]) new Optional<?>[numSquares];
    private ArrayList<Move> moveSequence;

    // Invariants:
    //   minRow() < maxRow(); minCol() < maxCol()
    //   For all row, col s.t. minRow() <= row <= maxRow(); 
    //     minCol() <= col <= maxCol(), exactly one of the "pieceAt" 
    //     methods returns true.
    //   if (row + col) % 2 == 0, then noPieceAt (row, col) == true
    //   (Such a square will also be colored black when drawn)
    //   !gameOver() if and only if getNextBoards().size() > 0
    //   getNextBoards().size() == getCurrentPlayerMoves().size()

    // Pre: None
    // Post: Red pieces in top three rows; black pieces in bottom three rows;
    //       no pieces in middle two rows
    public Checkerboard() {
        newGame();
    }

    public Optional<Piece> pieceAt(int row, int col) {
        if (blackSquareAt(row, col)) {
            return Optional.empty();
        } else {
            return board[getIndex(row, col)];
        }
    }

    public Checkerboard(String src) {
        String[] rows = src.split("\\n");
        if (rows.length != sideSquares) {
            throw new IllegalArgumentException("Wrong number of rows");
        }
        for (int row = 0; row < rows.length; row++) {
            if (rows[row].length() != sideSquares) {
                throw new IllegalArgumentException("Wrong number of columns");
            }
            for (int col = 0; col < rows[row].length(); col++) {
                if (redSquareAt(row, col)) {
                    char c = rows[row].charAt(col);
                    int i = getIndex(row, col);
                    if (c == '.') {
                        board[i] = Optional.empty();
                    } else if (c == 'r') {
                        board[i] = Optional.of(new Piece(PlayerColor.RED));
                    } else if (c == 'b') {
                        board[i] = Optional.of(new Piece(PlayerColor.BLACK));
                    } else {
                        throw new IllegalArgumentException(c + " is not a defined input");
                    }
                }
            }
        }
        currentPlayer = PlayerColor.BLACK;
        turnRepeating = false;
        moveSequence = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                s.append(pieceAt(row, col).map(Piece::toString).orElse("."));
            }
            s.append('\n');
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Checkerboard board) {
            for (int row = 0; row < numRows(); row++) {
                for (int col = 0; col < numCols(); col++) {
                    if (!this.pieceAt(row, col).equals(board.pieceAt(row, col))) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Checkerboard> getNextBoards() {
        ArrayList<Checkerboard> futures = new ArrayList<>();
        Set<Move> moves = getCurrentPlayerMoves();
        for (Move m : moves) {
            Checkerboard c = duplicate();
            c.move(m);
            futures.add(c);
        }
        return futures;
    }

    // Pre: None
    // Post: Returns a duplicate of this Checkerboard
    public Checkerboard duplicate() {
        Checkerboard dup = new Checkerboard();
        System.arraycopy(board, 0, dup.board, 0, numSquares);
        dup.currentPlayer = currentPlayer;
        dup.repeatingRow = repeatingRow;
        dup.repeatingCol = repeatingCol;
        dup.turnRepeating = turnRepeating;
        dup.moveSequence = new ArrayList<>(moveSequence);
        return dup;
    }

    // Pre: None
    // Post: Red pieces in top three rows; black pieces in bottom three rows;
    //       no pieces in middle two rows
    public void newGame() {
        for (int i = 0; i < numStartingPieces; ++i) {
            board[i] = Optional.of(new Piece(PlayerColor.RED));
        }
        int blackStart = numSquares - numStartingPieces;
        for (int i = numStartingPieces; i < blackStart; ++i) {
            board[i] = Optional.empty();
        }
        for (int i = blackStart; i < numSquares; ++i) {
            board[i] = Optional.of(new Piece(PlayerColor.BLACK));
        }
        currentPlayer = PlayerColor.BLACK;
        turnRepeating = false;
        moveSequence = new ArrayList<>();
    }

    public int minRow() {
        return 0;
    }

    public int maxRow() {
        return 7;
    }

    public int minCol() {
        return 0;
    }

    public int maxCol() {
        return 7;
    }

    public int numRows() {
        return maxRow() - minRow() + 1;
    }

    public int numCols() {
        return maxCol() - minCol() + 1;
    }

    public int getNumMovesMade() {
        return moveSequence.size();
    }

    // Pre: 0 <= n < getNumMovesMade()
    // Post: Returns nth move
    public Move getNthMove(int n) {
        return moveSequence.get(n);
    }

    // Pre: getNumMovesMade() >= 1
    // Post: Returns most recent move
    public Move getLastMove() {
        return getNthMove(getNumMovesMade() - 1);
    }

    // Pre: getRedMoves().contains(move) || getBlackMoves.contains(move)
    // Post: Piece is removed from move's start and appears at move's end
    //       If move hops over an opposing piece, the opposing piece is 
    //       removed.  If the move results in another legal capture, the
    //       current player gets another turn.  Otherwise, the turn switches
    //       to the other player.
    public void move(Move move) {
        int start = getIndex(move.getStartRow(), move.getStartCol());
        int end = getIndex(move.getEndRow(), move.getEndCol());
        board[end] = board[start];
        board[start] = Optional.empty();
        if (!kingAt(move.getEndRow(), move.getEndCol()) &&
                canKing(move.getEndRow(), move.getEndCol())) {
            makeKing(move.getEndRow(), move.getEndCol());
        }
        boolean changeTurn = !move.isCapture();

        if (!changeTurn) {
            int captureRow = getCaptureRow(move);
            int captureCol = getCaptureCol(move);
            board[getIndex(captureRow, captureCol)] = Optional.empty();

            if (pieceCanStillCapture(move.getEndRow(), move.getEndCol())) {
                repeatingRow = move.getEndRow();
                repeatingCol = move.getEndCol();
                turnRepeating = true;
            } else {
                changeTurn = true;
            }
        }

        moveSequence.add(move);

        if (changeTurn) {
            currentPlayer = currentPlayer.opponent();
            turnRepeating = false;
        }
    }

    // Pre: None
    // Post: Returns true if 
    //       minRow() <= row <= maxRow(); minCol() <= col <= maxCol()
    public boolean legal(int row, int col) {
        return (row >= minRow() && row <= maxRow() && col >= minCol() && col <= maxCol());
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isTurnFor(PlayerColor player) {
        return currentPlayer == player;
    }

    public boolean turnIsRepeating() {
        return turnRepeating;
    }

    public Set<Move> getCurrentPlayerMoves() {
        return getLegalMoves(getCurrentPlayer());
    }

    public Set<Move> getLegalMoves(PlayerColor player) {
        Set<Move> captureMoves = allCaptureMoves(player);
        if (captureMoves.size() > 0) {
            return captureMoves;
        }
        return allRegularMoves(player);
    }

    public Set<Move> allCaptureMoves(PlayerColor player) {
        Set<Move> captureMoves = new LinkedHashSet<>();
        if (turnRepeating) {
            addCaptureMoves(player, captureMoves, repeatingRow, repeatingCol);

        } else {
            for (int i = 0; i < numSquares; ++i) {
                int row = getRow(i);
                int col = getCol(i);
                if (colorAt(row, col, player)) {
                    addCaptureMoves(player, captureMoves, row, col);
                }
            }
        }

        return captureMoves;
    }

    public Set<Move> allRegularMoves(PlayerColor player) {
        Set<Move> regularMoves = new LinkedHashSet<>();
        if (!turnRepeating) {
            for (int i = 0; i < numSquares; ++i) {
                int row = getRow(i);
                int col = getCol(i);
                if (debug) {
                    System.out.println("row: " + row + " col: " + col);
                }
                if (colorAt(row, col, player)) {
                    if (debug) {
                        System.out.println("adding regular...");
                    }
                    addRegularMoves(regularMoves, row, col);
                } else if (debug) {
                    System.out.println("Not considering");
                }
            }
        }
        return regularMoves;
    }

    public boolean playerWins(PlayerColor player) {
        PlayerColor otherPlayer = player.opponent();
        return isTurnFor(otherPlayer) && (getLegalMoves(otherPlayer).size() == 0);
    }

    public boolean gameOver() {
        return playerWins(PlayerColor.RED) || playerWins(PlayerColor.BLACK);
    }

    // Pre: legal (row, col)
    // Post: Returns true if the current player can still capture
    //       from the position that terminated the previous move
    public boolean pieceCanStillCapture(int row, int col) {
        if (!colorAt(row, col, getCurrentPlayer())) {
            return false;
        }
        Set<Move> captureMoves = new LinkedHashSet<>();
        addCaptureMoves(getCurrentPlayer(), captureMoves, row, col);
        return (captureMoves.size() > 0);
    }

    // Pre: legal (row, col)
    // Post: Returns true if square is black
    public boolean blackSquareAt(int row, int col) {
        return (row + col) % 2 == 0;
    }

    // Pre: legal (row, col)
    // Post: Returns true if square is red
    public boolean redSquareAt(int row, int col) {
        return !blackSquareAt(row, col);
    }

    public boolean colorAt(int row, int col, PlayerColor color) {
        return getColorAt(row, col).filter(c -> c.equals(color)).isPresent();
    }

    public Optional<PlayerColor> getColorAt(int row, int col) {
        return pieceAt(row, col).map(Piece::getColor);
    }

    // Pre: legal (row, col)
    // Post: Returns true if there is a piece and it is a king
    public boolean kingAt(int row, int col) {
        return pieceAt(row, col).filter(Piece::isKing).isPresent();
    }

    // Pre: color == RED or BLACK
    // Post: Returns total number of pieces of the specified color
    public int numPiecesOf(PlayerColor color) {
        int count = 0;
        for (int row = minRow(); row <= maxRow(); ++row) {
            for (int col = minCol(); col <= maxCol(); ++col) {
                if (colorAt(row, col, color)) {
                    ++count;
                }
            }
        }
        return count;
    }

    // Pre: color == RED or BLACK
    // Post: Returns total number of kings of the specified color
    public int numKingsOf(PlayerColor color) {
        int count = 0;
        for (int row = minRow(); row <= maxRow(); ++row) {
            for (int col = minCol(); col <= maxCol(); ++col) {
                if (colorAt(row, col, color) && kingAt(row, col)) {
                    ++count;
                }
            }
        }
        return count;
    }

    // Private methods

    // Pre: move != null
    // Post: Returns average of start and end row
    private int getCaptureRow(Move move) {
        return (move.getStartRow() + move.getEndRow()) / 2;
    }

    // Pre: move != null
    // Post: Returns average of start and end column
    private int getCaptureCol(Move move) {
        return (move.getStartCol() + move.getEndCol()) / 2;
    }

    // Pre: captureMoves != null; legal (row, col); !noPieceAt (row, col)
    // Post: All legal capture moves for this player at (row, col) are 
    //       added to captureMoves
    private void addCaptureMoves(PlayerColor player, Set<Move> captureMoves, int row, int col) {
        Set<Move> candidates = getCandidateCaptures(row, col);
        for (Move m : candidates) {
            int captureRow = getCaptureRow(m);
            int captureCol = getCaptureCol(m);
            if (legal(m.getEndRow(), m.getEndCol()) && pieceAt(m.getEndRow(), m.getEndCol()).isEmpty() &&
                    pieceAt(captureRow, captureCol).filter(p -> p.getColor() != player).isPresent()) {
                captureMoves.add(m);
            }
        }
    }

    // Pre: regularMoves != null; legal (row, col); !noPieceAt (row, col)
    // Post: All legal non-capturing moves for this player at (row, col) are 
    //       added to regularMoves
    private void addRegularMoves(Set<Move> regularMoves, int row, int col) {
        Set<Move> candidates = getCandidateRegularMoves(row, col);
        for (Move m : candidates) {
            if (legal(m.getEndRow(), m.getEndCol()) && pieceAt(m.getEndRow(), m.getEndCol()).isEmpty()) {
                regularMoves.add(m);
            }
        }
    }

    // Pre: legal (row, col); piece at (row, col); 
    // Post: Returns all possible candidate moves
    //       For a non-king, the candidate moves are the two adjacent diagonals
    //       in the next row
    //       For a king, the adjacent diagonals in the preceding row are also
    //       included
    private Set<Move> getCandidateRegularMoves(int row, int col) {
        Move upLeft = new Move(this, row, col, row - 1, col - 1);
        Move upRight = new Move(this, row, col, row - 1, col + 1);
        Move downLeft = new Move(this, row, col, row + 1, col - 1);
        Move downRight = new Move(this, row, col, row + 1, col + 1);
        return getCandidateMoves(row, col, upLeft, upRight,
                downLeft, downRight);
    }

    // Pre: legal (row, col); piece at (row, col); 
    // Post: Returns all possible candidate moves
    //       For a non-king, the candidate moves are the two adjacent diagonals
    //       two rows ahead
    //       For a king, the adjacent diagonals two rows behind are also
    //       included
    private Set<Move> getCandidateCaptures(int row, int col) {
        Move upLeft = new Move(this, row, col, row - 2, col - 2);
        Move upRight = new Move(this, row, col, row - 2, col + 2);
        Move downLeft = new Move(this, row, col, row + 2, col - 2);
        Move downRight = new Move(this, row, col, row + 2, col + 2);
        return getCandidateMoves(row, col, upLeft, upRight,
                downLeft, downRight);
    }

    // Pre: legal (row, col); piece at (row, col); 
    //      upLeft, upRight, downLeft, downRight make sense semantically
    //      and start at (row, col)
    // Post: Returns all possible candidate moves
    //       For a non-king, the candidate moves are the two forward moves
    //       For a king, the backward moves are also included
    private Set<Move> getCandidateMoves(int row, int col, Move upLeft,
                                        Move upRight, Move downLeft,
                                        Move downRight) {
        Set<Move> moves = new LinkedHashSet<>();
        if (kingAt(row, col)) {
            moves.add(downRight);
            moves.add(downLeft);
            moves.add(upLeft);
            moves.add(upRight);

        } else if (colorAt(row, col, PlayerColor.RED)) {
            moves.add(downRight);
            moves.add(downLeft);

        } else if (colorAt(row, col, PlayerColor.BLACK)) {
            moves.add(upRight);
            moves.add(upLeft);

        } else {
            System.out.println("Player does not exist at row: " + row
                    + " col: " + col);
        }
        return moves;
    }

    private boolean canKing(int row, int col) {
        return (row == maxRow() && colorAt(row, col, PlayerColor.RED)) ||
                (row == minRow() && colorAt(row, col, PlayerColor.BLACK)) && legal(row, col);
    }

    private void makeKing(int row, int col) {
        int i = getIndex(row, col);
        board[i] = board[i].map(Piece::kinged);
    }

    // Pre: legal (row, col)
    // Post: Returns appropriate array index for board
    private int getIndex(int row, int col) {
        return (row * sideSquares + col) / 2;
    }

    // Pre: 0 <= index < numSquares
    // Post: Returns row corresponding to index
    private int getRow(int index) {
        return index / (sideSquares / 2);
    }

    // Pre: 0 <= index < numSquares
    // Post: Returns column corresponding to index
    private int getCol(int index) {
        return ((index % (sideSquares / 2)) * 2) + (1 - (getRow(index) % 2));
    }
}
