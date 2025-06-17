# 🟩 Haskell Wordle

A terminal-based clone of the popular game **Wordle**, written in Haskell. Includes multiple difficulty levels and a helper mode that can act as a Wordle-solving assistant.

## 🔧 Features

- **Game Mode**
  - 🔹 Easy Mode: Standard guessing with validation.
  - 🔹 Standard Mode: Classic Wordle gameplay.
  - 🔹 Expert Mode: Includes a twist—one lie in the feedback is possible.

- **Helper Mode**
  - 🧠 Standard Helper: Enter feedback manually; the game suggests optimal guesses.
  - 🧠 Expert Helper: Assists with gameplay involving misleading feedback.

## ▶️ Usage

### Run the game

```bash
cabal run
```

Then follow the prompts in the terminal to choose between `game` and `helper` modes.

## 🧩 Game Rules

- Guess a **5-letter word** within **6 turns**.
- Feedback:
  - `G`: Correct letter, correct position.
  - `Y`: Correct letter, wrong position.
  - `X`: Letter not in the word.

- In Expert Mode, the game can return **one misleading response** (a "lie") during the game.

## 📁 Project Structure

- `Main.hs`: Main game logic.
- `Words.hs`: Contains the word list (not shown here).
- `Wordle.cabal`: Cabal configuration file.

## 📦 Requirements

- [GHC (Glasgow Haskell Compiler)](https://www.haskell.org/ghc/)
- [Cabal](https://www.haskell.org/cabal/)

## 📌 Todo

- Improve Expert Helper logic to account for lies.
- Add tests and word list enhancements.

