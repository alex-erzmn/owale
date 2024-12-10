package fr.ai.game.programming;

import fr.ai.game.programming.game.Game;
import fr.ai.game.programming.game.GameFactory;
import fr.ai.game.programming.game.GameMode;
import java.util.Scanner;

/**
 * Main class of the Awalé game.
 */
public class AwaleApplication {

    public void start() {
        System.out.println("Welcome to the Awalé Application.");
        System.out.println("Please choose the Game Mode:");
        System.out.println("1 - Player vs AI");
        System.out.println("2 - AI vs Player");
        System.out.println("3 - AI vs AI");

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice < 1 || choice > 3) {
            System.out.print("Enter your choice (1, 2, or 3): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
            }
        }

        GameMode selectedMode = switch (choice) {
            case 1 -> GameMode.PLAYER_VS_AI_LOCAL;
            case 2 -> GameMode.AI_VS_PLAYER_LOCAL;
            case 3 -> GameMode.AI_VS_AI_LOCAL;
            default -> null;
        };

        Game game = GameFactory.createAwaleGame(selectedMode);
        game.start();

        scanner.close();
    }
}
