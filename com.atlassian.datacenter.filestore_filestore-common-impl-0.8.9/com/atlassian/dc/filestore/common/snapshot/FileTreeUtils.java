/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dc.filestore.common.snapshot;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

final class FileTreeUtils {
    FileTreeUtils() {
    }

    static void copyFileTree(final Path sourceRoot, final Path destinationRoot) throws IOException {
        Files.walkFileTree(sourceRoot, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path sourceDir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(this.resolveRelative(sourceDir, sourceRoot, destinationRoot), new FileAttribute[0]);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs) throws IOException {
                Files.copy(sourceFile, this.resolveRelative(sourceFile, sourceRoot, destinationRoot), new CopyOption[0]);
                return FileVisitResult.CONTINUE;
            }

            private Path resolveRelative(Path source, Path sourceRoot2, Path destinationRoot2) {
                return destinationRoot2.resolve(sourceRoot2.relativize(source));
            }
        });
    }

    static void deleteFileTree(Path rootDir) throws IOException {
        Files.walkFileTree(rootDir, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

