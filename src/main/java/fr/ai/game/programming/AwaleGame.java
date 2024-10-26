package fr.ai.game.programming;

import fr.ai.game.programming.common.AwaleBoard;
import fr.ai.game.programming.common.Seed;
import lombok.Getter;

/**
 * Represents an Awale game.
 * TODO: Implement this class. Extract game logic from GamePresenter.
 */
public class AwaleGame {
    @Getter
    private AwaleBoard board;

    public AwaleGame() {
        this.board = new AwaleBoard();
    }

    public void startGame() {
        board = new AwaleBoard();
    }

    public void makeMove(int hole) {
        board.sowSeeds(hole + 1, Seed.Color.BLUE);
    }
}
