package com.example.shaderfolders.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Совместимость с IRLights (IRL Redactor): пропатченный пак всегда кладётся
 * в основную папку shaderpacks, а не в подпапку исходного пака.
 * Без IRLights этот миксин просто не применяется (@Pseudo).
 */
@Pseudo
@Mixin(targets = "org.qualet.irl.patcher.Shaderpacks", remap = false)
public abstract class IrlShaderpacksMixin {

    @ModifyVariable(method = "outputName", at = @At("HEAD"), argsOnly = true, remap = false)
    private static String shaderfolders$rootOutput(String packName) {
        if (packName == null) return null;
        String n = packName.replace('\\', '/');
        int i = n.lastIndexOf('/');
        return i < 0 ? packName : n.substring(i + 1);
    }
}
