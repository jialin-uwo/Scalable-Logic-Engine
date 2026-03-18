# Scalable Logic Engine & Interaction Framework

Role: Lead Architect & Team Lead | Final Grade: 100%

A decoupled, data-driven interaction engine built with Java Swing, featuring a stateless command pattern and attribute-based causal chaining.

## Highlights

- Stateless command architecture for `Go`, `Pick`, `Drop`, `Examine`, `Use`, `Talk`, and `Give`.
- Data-driven world hydration from JSON into runtime entities and rules.
- Attribute-based rule matching (`UseRule`, `GiveRule`) for puzzle-style state transitions.
- Polymorphic entity model centered on `Entity` with concrete `Character`, `GameObject`, and `Connection`.
- GUI gameplay loop with turn counting, save/load callbacks, and autosave support.

## System Architecture & Design

The framework loads static game configuration from `resources/data/DataFile.json` and hydrates runtime models through `DataLoader`.

- Command layer: concrete command classes (`GoCommand`, `PickCommand`, `DropCommand`, `ExamineCommand`, `UseCommand`, `TalkCommand`, `GiveCommand`) encapsulate validation and execution logic.
- Engine layer: `GameEngine` orchestrates command dispatch, game-state transitions, turn logic, and UI callbacks.
- Data layer: `GameData`, `Location`, `Inventory`, `GameObjectCollection`, and rule objects model the interaction universe.
- UI layer: `GameUI` renders locations, entities, inventory, and command feedback via Swing.
- Utilities: `ImageLoader`, `DataLoader`, and `DataSaver` isolate I/O concerns.

## Repository Layout (Current)

```text
Scalable-Logic-Engine/
├── README.md
├── docs/
│   ├── Design Documentation.md
│   ├── Requirement Documentation.md
│   ├── Testing Documentation.md
│   ├── images/
│   └── archive/                  # Original PDF versions
├── libs/
│   ├── gson-2.13.2.jar
│   └── junit-platform-console-standalone-1.11.3.jar
├── resources/
│   ├── data/
│   │   ├── DataFile.json
│   │   ├── Story.txt
│   │   └── gameflow.txt
│   ├── icons/
│   └── images/
├── saves/
├── src/
│   └── *.java
└── test/
    └── *.java
```

## Engineering Lifecycle & Documentation

The project follows a full SDLC and keeps key artifacts in `docs/`:

- Requirements Specification: `docs/Requirement Documentation.md`
- Architecture & Design: `docs/Design Documentation.md`
- Testing Strategy: `docs/Testing Documentation.md`
- Archived source PDFs: `docs/archive/`

## Quality Assurance

The automated test suite is organized across unit, integration, validation, and end-to-end scenarios.

- Test files: 23 Java test classes in `test/`.
- Test methods: 210 methods annotated with `@Test`.
- Coverage focus includes data loading, command execution, game-state transitions, UI behavior, and end-to-end flow.

Representative test classes include:

- `test/DataLoaderTest.java`
- `test/CommandGameDataIntegrationTest.java`
- `test/EndToEndGameScenarioTest.java`
- `test/TurnCountingValidationTest.java`

## Requirements

- JDK 8+
- Gson 2.13.2 (already in `libs/`)
- JUnit Platform Console 1.11.3 (already in `libs/`, for tests)

## Build & Run

Run commands from the repository root.

### macOS / Linux

```bash
# Compile source and tests to project root
javac -cp ".:libs/*" -d . src/*.java "test "/*.java

# Launch game (recommended: explicitly pass current data path)
java -cp ".:libs/*" AdventureGameMain resources/data/DataFile.json

# Run all tests
java -jar libs/junit-platform-console-standalone-1.11.3.jar --class-path "." --scan-class-path
```

### Windows (PowerShell)

```powershell
# Compile source and tests to project root
javac -cp ".;libs/*" -d . src/*.java "test "/*.java

# Launch game (recommended: explicitly pass current data path)
java -cp ".;libs/*" AdventureGameMain resources/data/DataFile.json

# Run all tests
java -jar libs/junit-platform-console-standalone-1.11.3.jar --class-path "." --scan-class-path
```

## Gameplay Summary

The engine provides a point-and-click adventure loop with:

- Location exploration via connections.
- Item collection and inventory management.
- Character interactions and object usage.
- Rule-driven puzzle resolution.
- Turn-based progress with optional ending constraints.

For guided validation and demo flow, see `resources/data/gameflow.txt`.

## Professional Acknowledgement

Developed as a capstone project for CS 2212: Software Engineering at Western University.
This repository reflects Group 02's architectural and engineering work under a lead-architect workflow.
