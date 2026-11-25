package com.example.shaderfolders.mixin;

import com.example.shaderfolders.FolderView;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShaderPackSelectionList.ShaderPackEntry.class, remap = false)
public abstract class ShaderPackEntryMixin {

    @Shadow
    @Final
    private ShaderPackSelectionList list;

    @Shadow
    @Final
    private String packName;

    /** Клик/Enter по папке открывает её вместо выбора шейдера. */
    @Inject(method = "doThing", at = @At("HEAD"), cancellable = true, remap = false)
    private void shaderfolders$openFolder(CallbackInfoReturnable<Boolean> cir) {
        String target = FolderView.targetOf(this.packName);
        if (target != null) {
            FolderView.current = target;
            this.list.refresh();
            cir.setReturnValue(true);
        }
    }

    /** При отрисовке показываем только имя пака без пути папки. */
    @Redirect(
            method = "method_25343",
            at = @At(value = "FIELD",
                    target = "Lnet/irisshaders/iris/gui/element/ShaderPackSelectionList$ShaderPackEntry;packName:Ljava/lang/String;",
                    opcode = 180, // GETFIELD
                    remap = false),
            remap = false)
    private String shaderfolders$shortName(ShaderPackSelectionList.ShaderPackEntry self) {
        return FolderView.displayName(self.getPackName());
    }
}
