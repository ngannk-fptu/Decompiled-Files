/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.filefilter;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class PathEqualsFileFilter
extends AbstractFileFilter {
    private final Path path;

    public PathEqualsFileFilter(Path file) {
        this.path = file;
    }

    @Override
    public boolean accept(File file) {
        return Objects.equals(this.path, file.toPath());
    }

    @Override
    public FileVisitResult accept(Path path, BasicFileAttributes attributes) {
        return this.toFileVisitResult(Objects.equals(this.path, path));
    }
}

