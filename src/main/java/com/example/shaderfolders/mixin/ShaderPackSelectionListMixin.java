package com.example.shaderfolders.mixin;

import com.example.shaderfolders.FolderView;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.irisshaders.iris.shaderpack.discovery.ShaderpackDirectoryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;

/**
 * Подменяет список, который рисует меню шейдеров, на содержимое текущей папки.
 * Остальные (IRLights и т.д.) по-прежнему получают полный список из enumerate().
 */
@Mixin(value = ShaderPackSelectionList.class, remap = false)
public abstract class ShaderPackSelectionListMixin {

    @Redirect(
            method = "refresh",
            at = @At(value = "INVOKE",
                    target = "Lnet/irisshaders/iris/shaderpack/discovery/ShaderpackDirectoryManager;enumerate()Ljava/util/List;",
                    remap = false),
            remap = false)
    private List<String> shaderfolders$folderView(ShaderpackDirectoryManager manager) throws IOException {
        return FolderView.build(manager.enumerate());
    }
}
