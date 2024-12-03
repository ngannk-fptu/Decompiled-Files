/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.internal.compiler.util.CtSym;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.JrtFileSystem;

class JrtFileSystemWithOlderRelease
extends JrtFileSystem {
    final String release;
    private List<Path> releaseRoots = Collections.emptyList();
    protected Path modulePath;
    private CtSym ctSym;

    JrtFileSystemWithOlderRelease(File jrt, String release) throws IOException {
        super(jrt);
        this.release = release;
        this.initialize(jrt, release);
    }

    @Override
    void initialize(File jdk) throws IOException {
    }

    private void initialize(File jdk, String rel) throws IOException {
        super.initialize(jdk);
        this.fs = null;
        String releaseCode = CtSym.getReleaseCode(this.release);
        this.ctSym = JRTUtil.getCtSym(Paths.get(this.jdkHome, new String[0]));
        this.fs = this.ctSym.getFs();
        if (!Files.exists(this.fs.getPath(releaseCode, new String[0]), new LinkOption[0]) || Files.exists(this.fs.getPath(releaseCode, "system-modules"), new LinkOption[0])) {
            this.fs = null;
        }
        this.releaseRoots = this.ctSym.releaseRoots(releaseCode);
    }

    @Override
    void walkModuleImage(final JRTUtil.JrtFileVisitor<Path> visitor, final int notify) throws IOException {
        for (Path p : this.releaseRoots) {
            Files.walkFileTree(p, (FileVisitor<? super Path>)new JRTUtil.AbstractFileVisitor<Path>(){

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    int count = dir.getNameCount();
                    if (count == 1) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (count == 2) {
                        Path mod = dir.getName(1);
                        if (JRTUtil.MODULE_TO_LOAD != null && JRTUtil.MODULE_TO_LOAD.length() > 0 && JRTUtil.MODULE_TO_LOAD.indexOf(mod.toString()) == -1) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return (notify & 4) == 0 ? FileVisitResult.CONTINUE : visitor.visitModule(dir, JRTUtil.sanitizedFileName(mod));
                    }
                    if ((notify & 2) == 0) {
                        return FileVisitResult.CONTINUE;
                    }
                    return visitor.visitPackage(dir.subpath(2, count), dir.getName(1), attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if ((notify & 1) == 0) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (file.getNameCount() == 3) {
                        JrtFileSystemWithOlderRelease.this.cachePackage("", file.getName(1).toString());
                    }
                    return visitor.visitFile(file.subpath(2, file.getNameCount()), file.getName(1), attrs);
                }
            });
        }
    }
}

