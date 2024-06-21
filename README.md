# Flood It Game

## Overview

This repository contains an implementation of the Flood It game, a project for the CS course Fundamentals II. The game is designed with mutable world state, ArrayLists, mutable linked data structures, and loops using the Impworld library. The project is divided into two parts: creating and rendering the board, and implementing gameplay and resetting the game.

## Table of Contents

- Overview
- Installation
- Usage
- Gameplay
- Testing
- Project Structure

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/flood-it-game.git

2.	Navigate to the project directory:
   cd flood-it-game

3.	Make sure you have the necessary libraries:
	•	javalib library
	•	tester library
(both libraries found here: https://course.khoury.northeastern.edu/cs2510/Documentation.html)

## Usage
To run the game, execute the ExamplesFloodIt class in your Java environment with the necessary libraries above.
Change the first line of the last method (testBigBang(Tester t)) to change the size and number of colors.

## Gameplay
	•	Mouse Click: Change the color of the top-left cell to the color of the clicked cell.
	•	‘r’ Key: Reset the game with a new random board.

## Rules
	•	The player is given a limited number of clicks to achieve a single-colored grid.
	•	The game provides feedback on winning or losing based on the number of allowed clicks.

## Tests
The project includes thorough tests for each method, ensuring the functionality and correctness of the game mechanics. Tests are implemented using the tester library.

## Project Structure
	•	Cell.java: Represents a single square of the game area.
	•	FloodItWorld.java: Manages the game state, including the board, game mechanics, and rendering.
	•	ExamplesFloodIt.java: Contains tests for the game.
