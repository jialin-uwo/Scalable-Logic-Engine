# CS2212 Group Project

# Testing Documentation   

<table><tr><td>Version</td><td>Date</td><td>Author(s)</td><td>Summary of Changes</td></tr><tr><td>1.0</td><td>2025.12.01</td><td>Peiyong Wang, Zhixian Wang, Xinyan Cai, Junqi Zheng, Jialin Li</td><td>Create introduction, test plan including unit, integration, validation and system test, and summary sections.</td></tr><tr><td></td><td></td><td></td><td></td></tr></table>

<table><tr><td colspan="2">Table of Content</td></tr><tr><td>Section</td><td>Discription</td></tr><tr><td>1. Introduction</td><td>Provides an overview of the Adventure Game Engine, including the testing objectives, scope, and references.</td></tr><tr><td>2. Test Plan</td><td>Details the comprehensive testing strategy covering Unit, Integration, Validation, and System testing levels with specific test cases.</td></tr><tr><td>3. Summary</td><td>Concludes the document by outlining the testing methodology, tools used, and key project deliverables.</td></tr></table>

# Comments

# 1. Introduction

# 1.1 Overview

The software system developed by Group02 is a Java-based Adventure Game Engine designed to play point-and-click style adventure games. The application loads game data (locations, objects, characters, and rules) from an external JSON/XML file, allowing users to explore a virtual world through a graphical user interface.

Key features of the system include:

Command Processing: Parsing and executing user actions such as Go, Pick Up, Use, and Talk.   
Game State Management: Tracking user inventory, current location, and world state changes based on rule interactions.   
- Interactive UI: Displaying location images, descriptions, and providing visual feedback for user commands.   
- Data-Driven Design: Decoupling the game engine from the story content to allow support for multiple game scenarios.

# 1.2 Objectives

The primary objective of this project is to apply software engineering principles to design, implement, and verify a robust adventure game engine.

Specific objectives for this Testing Documentation include:

- To ensure the implementation meets all functional requirements defined in the Project Specification, including navigation, inventory management, and complex rule matching (Give/Use).   
- To expose and mitigate defects in logic, parsing, state transitions, and boundary conditions.   
- To validate non-functional requirements such as system stability and data persistence (Save/Load functionality).

# 1.3 References

1. CS2212A Group Project Specification - https://westernu.brightspace.com/content/enforced/127086-UGRD_1259_3953/CS2212A%20Project%20Specification.html   
2. CS2212 Group Project Requirements Documentation (Group 2) -Gitlab, Requirements Documentation   
3. CS2212 Group Project Design Documentation (Group 2) -Gitlab, Design Documentation

# Comments

# 2. Test Plan

# - Purpose

o Provide a detailed plan for verifying and validating the implemented Adventure Game.   
The plan explicitly covers Unit, Integration, Validation and System testing as required by the assignment.

# - Scope

The plan addresses the game's core modules: command parsing and execution, game state model, rules engine (GiveRule/UseRule), inventory, locations and characters, data loader/saver, and UI hooks (testable via non-graphical substitutes).   
Non-functional tests (basic performance and stability) are included in System testing.

# - Objectives

o Ensure the implementation meets the functional requirements defined in the project specification.   
o Expose and mitigate defects in logic, parsing, state transitions, persistence and boundary conditions.   
o Provide traceability from requirements to test cases and acceptance criteria.

# Comments

# 2.1 Unit Testing

Goal: Verify the correctness of individual classes and methods in isolation.

Method: Write JUnit 5 tests for public methods and for internal behaviors where necessary (for critical logic). Use test doubles/mocks for external dependencies.

Entry Criteria: Source code compiled, classes instantiable; developers have added unit tests to the test/ directory.

Exit Criteria: All defined unit-level tests executed locally; targeted unit coverage metric achieved for critical classes (e.g., 80% lines for Rules/Commands/GameEngine) OR all stated unit test cases implemented.

# Target Modules and Representative Test Cases (with rationale)

A. Data Model and Collections   

<table><tr><td>Module</td><td>Test Cases</td><td>Rationale</td></tr><tr><td>GameObjectCollection</td><td>add, remove, getByName, case-insensitive name lookup, iteration.</td><td>Covers boundary conditions for object stores and core name matching.</td></tr><tr><td>Inventory</td><td>addObject success and failure when capacity reached; removeObject; containsObject; getAllObjects size changes.</td><td>Covers capacity logic and invariants.</td></tr><tr><td>Location</td><td>connections list integrity, add/remove objects/characters, connection lookups by name, unreachable/isolated location edge cases. Dynamic addConnection/removeConnection state validation.</td><td>Covers spatial model, path invariants, and support for dynamic connection creation.</td></tr><tr><td>GameEngine/GameData</td><td>Test turnCount increment logic and verification of turnLimit being reached leading to an EndGame state.</td><td>Turn Counting is a mandatory functional requirement.</td></tr></table>

B. Rules and Matching (Refined for GiveRule Restriction)   

<table><tr><td>Module</td><td>Test Cases</td><td>Rationale</td></tr><tr><td>GiveRule.applicable(...)</td><td>Test only with exact name matches; case variations, leading/trailing whitespace. Critical Test: Verify that attempting to match by attribute always fails.</td><td>GiveRule is strictly limited to name matching. This ensures logic adheres to the specific implementation constraint.</td></tr><tr><td>UseRule.applicable(...)</td><td>Test with Attribute matches (&quot;Hard&quot; attribute + &quot;Window&quot;); Exact name matches; Mixed matches (Name + Attribute); case variations.</td><td>Ensures the implemented Attribute-based matching works correctly, as this complexity is confined to UseRule.</td></tr><tr><td>UseRule</td><td>Test Rule execution when it carries the requestEnd flag, verifying it signals the GameEngine to perform end-game handling.</td><td>Validates the mechanism for ending the game via a specific Use command.</td></tr></table>

C. Individual Command Classes (Focus on Feature Variations)   

<table><tr><td>Command</td><td>Test Cases</td><td>Rationale</td></tr><tr><td>ExamineCommand</td><td>Unit: Returns description; handles unknown names; when an object contains items (e.g., Mysterious Box), verifies the contained objects are correctly added to the Location's object list.</td><td>Validates the logic for discovering contents of container objects.</td></tr><tr><td>UseCommand</td><td>Unit: validate correctly matches UseRule based on name and attributes; execute verifies used objects are removed, resulting Objects are added, and dynamic Connections are created/updated.</td><td>Ensures correctness of Attribute-based rule application and dynamic state transitions (e.g., new connections).</td></tr><tr><td>GoCommand</td><td>Unit: Validates connection existence (including newly created connections); verifies that moving to an EndLocation correctly triggers an EndGame state.</td><td>Covers dynamic connection usage and the logic for the EndLocation ending condition.</td></tr><tr><td>PickCommand</td><td>Unit: Validate presence and selectable flag; removing from location and adding to inventory; error when not present.Integration: pick then use/give sequence relies on picked items.</td><td>Ensures core inventory addition logic.</td></tr><tr><td>DropCommand</td><td>Unit: Validate removal from inventory and addition to location; error when item not in inventory.Integration: drop then pick back to verify persistence of state.</td><td>Ensures core inventory removal logic.</td></tr><tr><td>TalkCommand</td><td>Unit: Validate character presence; returns dialogue text and optional side-effects.</td><td>Ensures dialogue sequence logic.</td></tr><tr><td>GiveCommand</td><td>Unit: Validate character present and inventory contains object; matchedRule selection (only by name); requestEnd for end-game rules. Integration: complete give sequences that may produce resulting objects or trigger endings.</td><td>Ensures complex rule-based interaction logic.</td></tr></table>

D. Data Loader & Persistence   

<table><tr><td>Module</td><td>Test Cases</td><td>Rationale</td></tr><tr><td>DataLoader.loadGameData(file)</td><td>valid file → returns fully populated GameData;invalid/malformed file → throws documented exception;checks referential integrity post-load.</td><td>Covers robustness of parsing and prevents runtime errors.</td></tr><tr><td>Persistence (Save/Load)</td><td>Unit: Test saveState() and loadState() for GameEngine/GameData, verifying that key fields (currentLocation, turnCount, inventory contents) remain consistent across serialization/deserialization.</td><td>Validates the correctness of the Add-on feature: Persistence (Save/Load).</td></tr></table>

# Comments

# 2.2 Integration Testing

Goal: Verify correct interactions between components (commands, engine, data model, UI hooks, persistence).

Approach: Use integration JUnit tests that wire real instances together but substitute GUI components with SilentGameUI and images with TestImageLoader. Focus on sequences and dataflow rather than isolated method behavior.

Entry Criteria: Unit tests completed for related units; integration test scaffolding (SilentGameUI) available.

Exit Criteria: All integration scenarios implemented as test cases and executable in local environment.

Integration Scenarios (Detailed Test Cases)   

<table><tr><td>Case ID</td><td>Scenario Description</td><td>Steps and Verification Focus</td><td>Rationale</td></tr><tr><td>I1</td><td>Command parsing to execution (Give command path)</td><td>Invoke engine.processCommand(&quot;give O to C&quot;). Observe: Inventory item removed; resulting objects added; if rule.endGame → engine performs end-of-game handling; Turn Count incremented.</td><td>Exercises parser, command factory, command validate/execute, rules lookup, and engine post-execution logic.</td></tr><tr><td>I2</td><td>Use Command Path (Attribute-based match &amp; Dynamic Connection)</td><td>1. Use Metal Rod (Attribute: Hard) on Study Window.
2. Verification: Study Window is replaced by the WindowEscape connection; the correct message is displayed.</td><td>Validates the Rule Engine correctly handles attribute matching (via UseRule) and dynamic Connection creation.</td></tr><tr><td>I3</td><td>DataLoader → GameData consistency</td><td>Load via DataLoader, check referential integrity (Rules vs. Objects), and the presence of startLocation and EndLocation(s).</td><td>Prevents runtime NPEs and verifies loaded data structure consistency.</td></tr><tr><td>I4</td><td>Multi-command sequences (Container, Give, Use)</td><td>Execute the sequence: PICKUP Cup → USE Cup + Faucet → GIVE Cup of Water to Ghostly Boy → USE Child&#x27;s Emblem + Old Cabinet.
Verification: Assert intermediate states (Cup of Water, Child&#x27;s Emblem, Glowing Lantern) are correctly gained/removed.</td><td>Verifies the flow of container item usage, Give rule activation (by name), and multi-step puzzles.</td></tr><tr><td>I5 (New)</td><td>Persistence Cycle Test</td><td>1. Execute I4 steps up to acquiring the Child&#x27;s Emblem.
2. engine.saveGame&quot;.
3. engine.loadGame&quot;.
4. Continue executing the remaining steps of I4.
Verification: Game state (Inventory, Location, Turn Count) is restored perfectly, and subsequent commands execute without error.</td><td>Validates the robustness of the Save/Reload feature within an active gameplay session.</td></tr></table>

# Comments

# 2.3 Validation Testing

Goal: Validate that implemented features satisfy the requirements (requirement-level verification). This is black-box testing mapped to requirements.

Method: For each functional requirement, define at least one test case that demonstrates the expected behavior. Each validation test case must reference the requirement ID and provide a pass/fail oracle.

Entry Criteria: Requirements list finalized; integration tests prepared.

Exit Criteria: Requirement $\rightarrow$ test mapping exists and tests are automatable.

Representative Mapping and Test Design (Examples)   

<table><tr><td>REQ ID</td><td>Test Design</td><td>Justification/Expected Behavior (Oracle)</td></tr><tr><td>REQ R1/R3</td><td>Navigation and EndGame (Side Path): Use Attribute-based USE (Metal Rod on Study Window) to create WindowEscape. Then, use GO: WindowEscape to move to GardenExit.</td><td>Oracle: New connection is created; entering GardenExit (an EndLocation) causes the GameEngine to immediately trigger EndGame.</td></tr><tr><td>REQ R1/R3</td><td>Rule-based EndGame (Main Path): Execute the final required action: USE: Glowing Lantern in Secret Chamber.</td><td>Oracle: The Use Rule triggers the state change and the GameEngine correctly triggers EndGame based on the UseRule&#x27;s requestEnd flag.</td></tr><tr><td>REQ R2</td><td>Item Pickup and Inventory/Container</td><td>EXAMINE Mysterious Box; confirm Secret Key is added to the location list. Then PICKUP Secret Key; confirm Inventory contains it.</td></tr><tr><td>REQ R4</td><td>Persistence (Save/Load)</td><td>Save a Data instance to file after key state changes (e.g., inventory contains Glowing Lantern). Load it back and attempt to continue the game flow.</td></tr><tr><td>REQ R5 (Turn Count)</td><td>Turn Limit Ending</td><td>Set a small turnLimit; execute commands until the limit is reached.</td></tr></table>

# Comments

# 2.4 System Testing

Goal: Verify the system as a whole in an environment approximating user deployment and validate non-functional requirements (performance and stability basics).

Entry Criteria: Integration tests complete; baseline build and test harness available.

Exit Criteria: All system test scripts prepared and runnable; non-functional thresholds defined and documented.

System Test Cases (Detailed)   

<table><tr><td>Case ID</td><td>Scenario Description</td><td>Steps and Verification Focus</td></tr><tr><td>S1</td><td>End-to-End scenario execution (Main Path - Happy Path)</td><td>Steps: Load DataFile.json, simulate the defined Main Path (SecretChamber Ending) 1 by invoking the sequence of commands.Focus: end-to-end state transitions, interaction of all components, display hooks invoked, and successful EndGame trigger via UseRule.</td></tr><tr><td>S2</td><td>Alternative Endings and Branch Coverage</td><td>Steps: Execute command sequences that exercise all defined alternative endings: 1. Side Path (EndLocation) ending. 2. Turn Limit ending.Focus: Ensure all ending paths are reachable and trigger the correct end-game state-message.</td></tr><tr><td>S3</td><td>Persistence and Recovery Stability</td><td>Steps: During a complex scenario, call save, simulate an application restart, load the game, and continue the scenario to a successful conclusion.Focus: Data consistency and overall system stability following data recovery.</td></tr><tr><td>S4</td><td>Non-Functional - UI/Usability Check</td><td>Steps: Execute a standard sequence of commands and check the UI.Focus: Verify all required UI elements (Location Name/Picture, Items, Connections, Inventory, Turn Count) are always visible, correctly updated, and responsive throughout the session.</td></tr></table>

# Comments

# 3. Summary

This Test Plan outlines a comprehensive mixed-method strategy (white-box and black-box) designed to verify the Adventure Game's compliance with functional requirements and robustness against defects. The methodology is structured around four distinct levels: Unit testing to isolate logic correctness using JUnit 5, Integration testing to verify component interactions via a SilentGameUI, Validation testing to map specific checks directly to requirements (such as navigation and inventory), and System testing to validate end-to-end storylines and data persistence.

To ensure execution reliability, the team will utilize JDK 17/22 and a command-line test runner, placing the highest priority on validating command parsing and rule matching. The key deliverables include this plan, the JUnit source code, system scripts, and a final Test Report, with primary risks regarding GUI dependencies mitigated through the use of test doubles and image loader substitutes.

# Comments
