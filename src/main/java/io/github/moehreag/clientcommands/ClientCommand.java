package io.github.moehreag.clientcommands;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;

public abstract class ClientCommand extends AbstractCommand {

    @Override
    public int getPermissionLevel() {
        return 1;
    }

    @Override
    public boolean isAccessible(CommandSource source) {
        return true;
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "";
    }

    public String getPrefix(){
        return "/";
    }
}
