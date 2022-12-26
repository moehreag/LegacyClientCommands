package io.github.moehreag.clientcommands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.command.CommandRegistry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import static net.minecraft.util.Formatting.*;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClientCommandRegistry extends CommandRegistry {

    private static final ClientCommandRegistry instance = new ClientCommandRegistry();

    public String[] latestAutoComplete = null;

    private final List<CommandExecutionListener> listeners = new ArrayList<>();
    private final List<String> prefixes = new ArrayList<>();

    public static ClientCommandRegistry getInstance(){
        return instance;
    }

    public void registerCommand(String name, Function<String[], List<String>> suggestions, Consumer<String[]> onExecute){
        registerCommand(new ClientCommand() {
            @Override
            public String getCommandName() {
                return name;
            }

            @Override
            public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
                return suggestions.apply(args);
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                onExecute.accept(args);
            }
        });
    }

    public Command registerCommand(ClientCommand command){
        if(!prefixes.contains(command.getPrefix())) {
            prefixes.add(command.getPrefix());
        }
        return registerCommand((Command) command);
    }

    @Override
    public Command registerCommand(Command command) {
        if(!(command instanceof ClientCommand)){
            LogManager.getLogger().warn("Somebody tried to register a client-sided Command with a normal command.\n" +
                    "This is not good! Skipping command...");
        } else {
            return super.registerCommand(command);
        } return command;
    }

    public void registerListener(CommandExecutionListener listener){
        listeners.add(listener);
    }

    public void removeListener(CommandExecutionListener listener){
        listeners.remove(listener);
    }

    /**
     * @return 1 if successfully executed, -1 if no permission or wrong usage,
     *         0 if it doesn't exist or it was canceled (it's sent to the server)
     */
    @Override
    public int execute(CommandSource sender, String message)
    {
        message = message.trim();

        boolean strippedPrefix = false;
        for (String s: prefixes){
            if(message.startsWith(s)){
                message = message.substring(s.length());
                strippedPrefix = true;
                break;
            }
        }

        if(!strippedPrefix){
            return 0;
        }
        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        Command icommand = getCommandMap().get(commandName);

        try
        {
            if (icommand == null)
            {
                return 0;
            }

            if (icommand.isAccessible(sender))
            {
                AtomicBoolean event = new AtomicBoolean(false);
                listeners.forEach(listener -> event.set(event.get() || listener.onExecute(icommand, sender, args)));
                if(event.get()){
                    return 0;
                }

                icommand.execute(sender, args);
                return 1;
            }
            else
            {
                sender.sendMessage(format(RED, "commands.generic.permission"));
            }
        }
        catch (IncorrectUsageException wue)
        {
            sender.sendMessage(format(RED, "commands.generic.usage", format(RED, wue.getMessage(), wue.getArgs())));
        }
        catch (CommandException ce)
        {
            sender.sendMessage(format(RED, ce.getMessage(), ce.getArgs()));
        }
        catch (Throwable t)
        {
            sender.sendMessage(format(RED, "commands.generic.exception"));
            t.printStackTrace();
        }

        return -1;
    }

    private Text format(Formatting color, String str, Object... args)
    {
        Text ret = new TranslatableText(str, args);
        ret.getStyle().setFormatting(color);
        return ret;
    }

    public void autoComplete(String leftOfCursor, String full)
    {
        latestAutoComplete = null;

        if (leftOfCursor.charAt(0) == '/')
        {
            leftOfCursor = leftOfCursor.substring(1);

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen instanceof ChatScreen)
            {
                List<String> commands = getCompletions(mc.player, leftOfCursor, mc.player.pos);
                if (commands != null && !commands.isEmpty())
                {
                    if (leftOfCursor.indexOf(' ') == -1)
                    {
                        commands.replaceAll(s -> GRAY + "/" + s + RESET);
                    }
                    else
                    {
                        commands.replaceAll(s -> GRAY + s + RESET);
                    }

                    latestAutoComplete = commands.toArray(new String[0]);
                }
            }
        }
    }

    public interface CommandExecutionListener {
        boolean onExecute(Command command, CommandSource sender, String[] args);
    }
}
