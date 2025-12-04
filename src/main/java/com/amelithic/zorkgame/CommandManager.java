package com.amelithic.zorkgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amelithic.zorkgame.characters.Player;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        //Register all command instances here

        //interactions
        commands.add(new TakeCommand());
        commands.add(new DropCommand());
        commands.add(new DescribeCommand());
        commands.add(new ShowCommand()); //previously LookCommand + ShowCommand (old)
        commands.add(new GoalsCommand());
        commands.add(new UseCommand());
        commands.add(new FixCommand());
        commands.add(new AttackCommand());
        commands.add(new EatCommand());
        commands.add(new GoCommand());

        //game state
        commands.add(new SaveCommand());
        commands.add(new LoadCommand());
        commands.add(new QuitCommand());
        commands.add(new HelpCommand());

        //admin-only
        commands.add(new GiveCommand());
        commands.add(new WinCommand());

        //future use
        commands.add(new SayCommand());
    }

    //TODO: clear previous values from command before execute
    //-> overlap of values eg. 'describe table', then 'describe nonsense' returns table description

    public Optional<Command> parse(Main game, Player player, String input) {
        for (Command cmd : commands) {
            Optional<Command> result = cmd.parse(game, player, input);
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