package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.Seed;

public record Move(int hole, Seed.Color color) {
    @Override
    public String toString() {
        // Assuming Seed.Color is an enum with values like RED, BLUE, etc.
        char colorChar = color == Seed.Color.RED ? 'R' : 'B'; // Adjust based on the color enum values
        return hole + "" + colorChar;
    }
}