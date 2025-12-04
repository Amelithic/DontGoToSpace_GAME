# (Don't) Go To Space
Text-based sci-fi game with GUI using JavaFX.

**Aim:**
Collect all the necessary rocket parts to repair the spacecraft and leave the strange empty planet, whilst facing obstacles such as alien attackers and strange death circumstances!

**Index**:
-> Features
-> Documentation
    -> Installing and compiling the game
-> Next Steps

---

## Features
- Multi-page JavaFX GUI interface (using FXML and controllers).
    - Room backgrounds and movement buttons for easy and recognisable navigation.
    - Multi-threading used to display live player stats, such as health, oxygen level, and progress.
    - Animated card with key mission storybeats.
    - Buttons with popups to view the map, commands available, inventory, goals.
    - Live goal tracking.
    - Autocomplete input field using custom Trie class.
- Dynamic map loading using a custom map.json format.
- Large range of items and 4 item types.
- Over 10 rooms of different types, from outdoor to indoor.
    - Room class uses generics, allowing any datatype to serve as room ID (currently text).
    - Entering rooms may cause events to occur, such as completed missions, alien attacks, or oxygen levels to deplete.
- 17 different commands, with accompanying synonyms and descriptions.
    - Custom Annotations with metadata (for file readability).
    - REGEX to handle multi-word inputs
- Easy-to-manage game config and version information using native Java Properties library.
- Win conditions, but also death conditions, for the player's interest.
- Project structure through Maven.
- Partial CLI support.
- Saving and loading game states using JSON files using Jackson.

---

## Documentation

### Installing and compiling the game
1. Download and extract the ZIP file. If you have Java 17 or above, and Maven already installed, simply run `mvn clean install` to run the build command and install dependencies from the root project folder. If not, using an IDE such as VS Code or IntelliJ will recognise the project as a Maven project, and either use the IDE's respective build and run buttons, or else run `mvn clean install` in their integrated terminal.
2. Once the project dependencies are installed, either run using the IDE's run button, or run `mvn clean javafx:run` in the integrated terminal.
3. *Running from the CLI* -> If you would like to test limited CLI version:
    - Compile to a JAR file using `mvn clean package`, which will create the file in the 'target' folder. This can be run using `java -jar {filename.jar}` in the terminal if you have Java installed on your system.
    - Change the property 'ui.ui_mode' in the file 'config/config.properties' to 'cli' to run CLI mode.
4. Enjoy playing my game! Feel free to suggest new features or suggest bug fixes!

--- 

## Next Steps
*(for future reference)*

**Things I would like to add in future to build on game:**
- Full CLI support with multithreading, as in GUI.
- More items to discover, and large map sizes.
- Dynamic minimap/map generation in GUI - allowing the player to see where they are as they move through rooms.
- In-depth informational item explorer (e.g. full GUI for reading book or terminal items).
- Cross-platform executable version, for distribution
- Multiplayer version, with player 1 trying to get win condition while player 2 tries to prevent it.

---

#### Brief Note regarding AI Use
I dislike the use of AI for large portions of the code, so the code in this project has been strictly my own creation or rewritten to suit my code style and code organisation.
However, I did use Microsoft Copilot to help identify causes of bugs and understand exception stack traces, before then writing fixes myself, or to help suggest design choices to help structure my code (such as providing a range of methods to solve my problem, that I could help direct my solution towards).
