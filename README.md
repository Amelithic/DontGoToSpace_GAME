# (Don't) Go To Space
Text-based sci-fi game with GUI using JavaFX.

**Aim:**
Collect all the necessary rocket parts to repair the spacecraft and leave the strange empty planet, whilst facing obstacles such as alien attackers and strange death circumstances!

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
- Saving and loading game states using JSON files!

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
