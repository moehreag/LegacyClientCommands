package io.github.moehreag.clientcommands.mixin;

import com.google.common.collect.ObjectArrays;
import io.github.moehreag.clientcommands.ClientCommandRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @ModifyArg(method = "onCommandSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;setSuggestions([Ljava/lang/String;)V"))
    private String[] clientcommands$setSuggestions(String[] suggestions){
        String[] completions = ClientCommandRegistry.getInstance().latestAutoComplete;
        if(completions != null){
            return ObjectArrays.concat(completions, suggestions, String.class);
        }
        return suggestions;
    }
}
