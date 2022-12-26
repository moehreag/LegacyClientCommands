package io.github.moehreag.clientcommands.mixin;

import io.github.moehreag.clientcommands.ClientCommandRegistry;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void clientcommands$onChatMessage(String string, CallbackInfo ci){
        if(ClientCommandRegistry.getInstance().execute((ClientPlayerEntity)(Object)this, string) == 1){
            ci.cancel();
        }
    }
}
