package com.amelithic.zorkgame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.amelithic.zorkgame.characters.Character;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.locations.Room;

public class Main {
    private Parser parser;
    private Character player;

    public Main() {
        createRooms();
        parser = new Parser();
    }

    private void createRooms() {
        Room outside, theatre, pub, lab, office, cafe;

        // create rooms
        /*outside = new OutdoorArea("outside the main entrance of the university");
        theatre = new IndoorArea("in a lecture theatre");
        pub = new IndoorArea("in the campus pub");
        lab = new IndoorArea("in a computing lab");
        office = new IndoorArea("in the computing admin office");
        cafe = new IndoorArea("in the Cube Cafe");*/

        outside = new Room("outside the main entrance of the university");
        theatre = new Room("in a lecture theatre");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        cafe = new Room("in the Cube Cafe");


        // initialise room exits
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("southwest", cafe);
        outside.setExit("west", pub);

        theatre.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        cafe.setExit("northeast", lab);


        //Adding items to rooms
        Item apple = new Item("apple", "consumable", true);
        Item treeBark = new Item("tree bark", "consumable but not very preferable", true);
        Item plasticBag = new Item("plastic bag", "destroyed piece of (inedible) rubbish", false);
        Item computerChips = new Item("computer chips", "a pile of computer chips", false);
        Item hotChocolate = new Item("hot chocolate", "consumable and very tasty", true);
        Item cake = new Item("cake", "very tasty", true);
        Item alcohol = new Item("alcohol", "ew alchol", true);


        outside.setRoomItems(apple, treeBark, plasticBag);
        lab.setRoomItems(computerChips, computerChips, computerChips);
        cafe.setRoomItems(hotChocolate, cake);
        pub.setRoomItems(plasticBag,alcohol);

        // create the player character and start outside
        player = new Character("player", outside);

        // Deserialize the object from the file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("player.ser"))) {
            Character deserializedPerson = (Character) in.readObject();
            System.out.println("Object has been deserialized!");
            deserializedPerson.printChar();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the University adventure!");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
        System.out.println(player.getCurrentRoom().printRoomItems());
    }

    private boolean processCommand(Command command) {
        String commandWord = command.getCommandWord();

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        switch (commandWord) {
            case "help":
                printHelp();
                break;
            case "go":
                goRoom(command);
                break;
            case "inventory":
                System.out.println(player.printInventory());
            case "show":
                if (command.hasSecondWord()) {
                    switch (command.getSecondWord().toLowerCase()) {
                        case "inventory":
                            System.out.println(player.printInventory());
                            break;
                        case "items":
                            System.out.println(player.getCurrentRoom().printRoomItems());
                            break;
                        case "location":
                            System.out.println(player.getCurrentRoom().getLongDescription());
                            System.out.println(player.getCurrentRoom().printRoomItems());
                            break;
                        default:
                            System.out.println("Unknown command...\nAvailable options: inventory, items");
                    }
                } else {
                    System.out.println("Show what?");
                }
                break;
            case "eat":
                if (command.hasSecondWord()) {
                    Item edibleItem = null;

                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(command.getSecondWord())) {
                            edibleItem = item;
                            break;
                        } else {
                            continue;
                        }
                    }
                    if (edibleItem != null) {
                        if (edibleItem.isEdible() == true) {
                            if (edibleItem.getName().toLowerCase() == "cake") {
                                System.out.println("You consumed " + edibleItem.getName());
                                player.removeFromInventory(edibleItem);

                                Item crumbs = new Item("crumbs", "leftover crumbs from cake", true);
                                player.setInventory(crumbs);
                                System.out.printf("Added %s to inventory!\n", crumbs.getName());
                            } else if (edibleItem.getName().toLowerCase() == "alcohol") {
                                System.out.println("You consumed " + edibleItem.getName());
                                player.removeFromInventory(edibleItem);

                                Item bottle = new Item("bottle", "empty alcohol bottle", false);
                                player.setInventory(bottle);
                                System.out.printf("Added %s to inventory!\n", bottle.getName());
                            } else {
                                System.out.println("You consumed " + edibleItem.getName());
                                player.removeFromInventory(edibleItem);
                            }
                        } else {
                            System.out.printf("You cannot consume %s!\n", edibleItem.getName());
                        }
                    } else {
                        System.out.println("There is no item of that type to eat in your inventory.");
                    }
                } else {
                    System.out.println("Eat what?");
                }
                break;
            case "take":
                if (command.hasSecondWord()) {
                    Item takeItem = null;

                    //find item in room
                    for (Item item : player.getCurrentRoom().getRoomItems()) {
                        if (item.getName().equalsIgnoreCase(command.getSecondWord())) {
                            takeItem = item;
                            break;
                        } else {
                            continue;
                        }
                    }

                    //verify it exists
                    if (takeItem != null) {
                        //check room contains object + add to inventory + remove from room
                        if (player.getCurrentRoom().getRoomItems().contains(takeItem)) {
                            player.setInventory(takeItem);
                            if (player.getCurrentRoom().removeRoomItem(takeItem)) {
                                System.out.printf("Added %s to inventory!\n", takeItem.getName());
                            } else {
                                System.out.println("Cannot add item");
                            }
                        }
                    } else {
                        System.out.println("There is no item of that type in this room.");
                    }

                } else {
                    System.out.println("Take what?");
                }
                break;
            case "save":
                // Deserialize the object from the file
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("player.ser"))) {
                    Character deserializedPerson = (Character) in.readObject();
                    System.out.println("Object has been deserialized!");
                    deserializedPerson.printChar();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // Serialize the object to a fileCharacter
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("player.ser"))) {
                    out.writeObject(player);
                    System.out.println("Object has been serialized to player.ser");
                    player.printChar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "drop":
                if (command.hasSecondWord()) {
                    Item removeItem = null;

                    //find item in inventory
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(command.getSecondWord())) {
                            removeItem = item;
                            break;
                        } else {
                            continue;
                        }
                    }

                    //verify it exists
                    if (removeItem != null) {
                        //check its in inventory + remove from inventory + add to room
                        if (player.getInventory().contains(removeItem)) {
                            player.getCurrentRoom().setRoomItems(removeItem);
                            if (player.removeFromInventory(removeItem)) {
                                System.out.printf("Dropped %s!\n", removeItem.getName());
                            } else {
                                System.out.println("Cannot drop item");
                            }
                        }
                    } else {
                        System.out.println("There is no item of that type in this room.");
                    }

                } else {
                    System.out.println("Drop what?");
                }
                break;
            case "describe":
                if (command.hasSecondWord()) {
                    Item describeItem = null;

                    //find item in inventory
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(command.getSecondWord())) {
                            describeItem = item;
                            break;
                        } else {
                            continue;
                        }
                    }
                    for (Item item : player.getCurrentRoom().getRoomItems()) {
                        if (item.getName().equalsIgnoreCase(command.getSecondWord())) {
                            describeItem = item;
                            break;
                        } else {
                            continue;
                        }
                    }

                    //verify it exists
                    if (describeItem != null) {
                        System.out.println(describeItem.getName()+": "+describeItem.getDescription());
                    } else {
                        System.out.println("There is no item of that type in this room.");
                    }

                } else {
                    System.out.println("Describe what?");
                }
                break;
            case "say":
                if (command.hasSecondWord()) {
                    System.out.println(player.getName()+" says: "+command.getSecondWord());
                } else {
                    System.out.println("Say what?");
                }
            case "quit":
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true; // signal to quit
                }
            default:
                System.out.println("I don't know what you mean...");
                break;
        }
        return false;
    }

    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
            System.out.println(player.getCurrentRoom().printRoomItems());
        }
    }

    public static void main(String[] args) {
        Main game = new Main();
        game.play();
    }
}


class ZorkUL {
    public static final String WELCOME_MESSAGE = "Welcome to the ZorkUL Game!";
    public static final String VERSION = "1.0";
}