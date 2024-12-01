package fr.ai.game.programming.game.elements;

import lombok.Getter;

public class Seed {

    public enum Color {
        RED, BLUE
    }

    @Getter
    private final Color color;

    public Seed(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color == Color.BLUE ? "Blue" : "Red";
    }
}
