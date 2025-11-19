package com.amelithic.zorkgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        // Register all command instances here
        commands.add(new GoCommand());
        commands.add(new QuitCommand());
        commands.add(new HelpCommand());
        commands.add(new LookCommand());
        commands.add(new EatCommand());
        commands.add(new ShowCommand());
        commands.add(new TakeItemCommand());
        commands.add(new DropCommand());
        commands.add(new SayCommand());
        commands.add(new DescribeCommand());
    }

    public Optional<Command> parse(String input) {
        for (Command cmd : commands) {
            Optional<Command> result = cmd.parse(input);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public List<Command> getAllCommands() {
        return commands;
    }
}