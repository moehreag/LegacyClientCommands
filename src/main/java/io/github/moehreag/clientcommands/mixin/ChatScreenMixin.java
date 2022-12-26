package io.github.moehreag.clientcommands.mixin;

import io.github.moehreag.clientcommands.ClientCommandRegistry;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Inject(method = "requestAutocomplete", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;result:Lnet/minecraft/util/hit/BlockHitResult;"))
    private void clientcommands$getCompletions(String string, String string2, CallbackInfo ci){
        ClientCommandRegistry.getInstance().autoComplete(string, string2);
    }

    @ModifyArg(method = "showSuggestion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;write(Ljava/lang/String;)V"))
    private String clientcommands$stripFormatting(String text){
        return Formatting.strip(text);
    }

    @Redirect(method = "setSuggestions", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;getCommonPrefix([Ljava/lang/String;)Ljava/lang/String;", remap = false))
    private String clientcommands$stripFormatting(String[] strs){

        return Formatting.strip(StringUtils.getCommonPrefix(strs));
    }
}
