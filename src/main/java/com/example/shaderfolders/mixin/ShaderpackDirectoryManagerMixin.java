package com.example.shaderfolders.mixin;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.discovery.ShaderpackDirectoryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Дополняет список шейдерпаков Iris паками из вложенных папок.
 * shaderpacks/Реализм/BSL/shaders/...  ->  "Реализм/BSL"
 */
@Mixin(value = ShaderpackDirectoryManager.class, remap = false)
public abstract class ShaderpackDirectoryManagerMixin {

    private static final int MAX_DEPTH = 4;

    @Inject(method = "enumerate", at = @At("RETURN"), cancellable = true)
    private void shaderfolders$scanSubfolders(CallbackInfoReturnable<List<String>> cir) {
        final Path root = Iris.getShaderpacksDirectory();
        Set<String> result = new LinkedHashSet<>(cir.getReturnValue());

        try {
            Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), MAX_DEPTH,
                    new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.equals(root)) return FileVisitResult.CONTINUE;
                    // Это сам шейдерпак — внутрь не заходим
                    if (Files.isDirectory(dir.resolve("shaders"))) {
                        add(dir);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path parent = file.getParent();
                    if (parent != null && !parent.equals(root)
                            && file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                        add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                private void add(Path p) {
                    result.add(root.relativize(p).toString().replace('\\', '/'));
                }
            });
        } catch (IOException ignored) {
            // при ошибке оставляем стандартный список
        }

        List<String> sorted = new ArrayList<>(result);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        cir.setReturnValue(sorted);
    }
}
