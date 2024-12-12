package fr.ai.game.programming.game.player;

import fr.ai.game.programming.game.elements.SeedColor;

public record Move(int hole, SeedColor color) {
    @Override
    public String toString() {
        // Assuming SeedColor is an enum with values like RED, BLUE, etc.
        char colorChar = color == SeedColor.RED ? 'R' : 'B'; // Adjust based on the color enum values
        return hole + "" + colorChar;
    }
}