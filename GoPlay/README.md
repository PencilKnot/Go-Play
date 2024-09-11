## How to Open

- Unzip game folder
- Open `runMe.bat` file

## How to Play

This program allows two users to play Go with a customizable setup. Here are more details:

<u>Setup</u>
- Use drop-down tables to choose board size and board theme
- Previous gameplay records can be viewed at <b>Records</b> button on start screen

<u>In-Game</u>
- Click on any intersection to place black or white Go pieces, depending on the current player
- Computer will automatically remove any captured stone on the board (edge cases like *ko* are not considered)
- Timer found at top left, current player found at top right

<u>Ending the Game</u>
- Resign results in instant win for other player
- Two consecutive passes from a player ends game and opens scoring screen
- Click on a dead stone on the board, the click <b>Remove Dead Stones</b> button of endgame screen to remove it
- <b>Determine Winner</b> button calculates final winner of game and stores it

Note that `game_records.txt` stores all previous gameplay winners and times. DO NOT DELETE THE TEXT FILE.

## Rules of Go and Scoring
Scoring followed the rules found [here](https://senseis.xmp.net/?RulesOfGoIntroductory). The sections on *No repetition* and *Ending the game* were modified for the program.

The scoring methodology for this program follows this [analysis](https://jpolitz.github.io/notes/2012/12/25/go-termination.html). It assumes that players are playing a standard game of Go and keeping each other accountable to the rules.

> <b>TLDR</b>: The count of the number of stones of each colour on the board is an appropriate approximation of the winner. The player with more stones wins.
