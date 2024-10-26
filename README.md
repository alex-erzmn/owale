# owale
Based on the course "AI Game Programming" by J.-C. Régin, this version of the Owale game was created to experience implementing AI for two player games using the Minimax-Algorithm.

## TODO:

### Optimizing the AI
- [ ] Optimizing Minimax-Algorithm using Alpha-Beta-Pruning
- [ ] Implementing Iterative Deepening
- [ ] Examine the possible depth within a turn time limit of 2sec
- [ ] Implement safety mechanism to stop computing after 2sec and return the best move so far
- [ ] FIX: int bestMove = -1;

### Optimizing Interface and Game
- [ ] Make sure the rules are applied correctly 
- [ ] Having flexible Interface to connect second AI or Player
- [ ] Adding display for the time spend to determine a turn
- [ ] Adding display for the depth of the Minimax-Algorithm
- [ ] Separating the two turns such that one can see who did a turn



# New Rules
There are 16 holes, 8 per player.
Holes are numbered from 1 to 16. We turn clockwise: Hole 1 follows clockwise after Hole 16.
The first player controls the odd-numbered holes, and the second player controls the even-numbered holes.
(Be careful, this is quite different from oware.)

## Colors
There are two colors: red and blue.
At the beginning, there are 2 seeds of each color in every hole.

## Object
The game starts with 2 blue and 2 red seeds in each hole. The object of the game is to capture more seeds than your opponent. Since there is an even number of seeds, it is possible for the game to end in a draw, where each player has captured 32 seeds.

## Sowing
Players take turns moving the seeds. On a turn, a player chooses one of the eight holes under their control. The player removes seeds from that hole (see below for color management) and distributes them by dropping one seed into holes clockwise (i.e., in non-decreasing order) from this hole, in a process called sowing.

Moves are made according to the color of the seeds. First, a color is designated, and all the seeds of that color are played:
If the seeds are blue, they are distributed into each hole.
If the seeds are red, they are distributed only into the opponent's holes.

Seeds are not distributed into the hole they were removed from. The starting hole is always left empty; if it contained 16 (or more) seeds, it is skipped, and the sixteenth seed is placed in the next hole.

Thus, a move is expressed by NC, where N is the number of the hole, and C is the color of the seeds being played.
For example, "3R" means that you play the red seeds from hole 3 (and only the red ones).

## Capturing
Capturing occurs only when a player brings the number of seeds in a hole to exactly two or three (of any color). This always captures the seeds in that hole, and possibly more: If the previous-to-last seed also brought a hole to two or three seeds, those seeds are captured as well, and this continues until a hole is reached that does not contain exactly two or three seeds. Captured seeds are set aside. Starving the opponent is allowed.

Note: You can take seeds from your own holes, and seeds are captured independently of their color. It is also allowed to capture all of the opponent's seeds. In case of starving, all the remaining seeds are captured by the last player.

The game ends when there are fewer than 8 seeds left on the board. In this case, the remaining seeds are not counted.

## Winning
The game is over when:
One player has captured 33 or more seeds, or
Both players have captured 32 seeds (draw), or
There are fewer than 8 seeds remaining on the board.

The winner is the player who has captured more seeds than their opponent.