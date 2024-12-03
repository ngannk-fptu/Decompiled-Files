/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.file.PathFilter;
import org.apache.commons.io.file.SimplePathVisitor;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SymbolicLinkFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.function.IOBiFunction;

public class CountingPathVisitor
extends SimplePathVisitor {
    static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final Counters.PathCounters pathCounters;
    private final PathFilter fileFilter;
    private final PathFilter dirFilter;

    static IOFileFilter defaultDirFilter() {
        return TrueFileFilter.INSTANCE;
    }

    static IOFileFilter defaultFileFilter() {
        return new SymbolicLinkFileFilter(FileVisitResult.TERMINATE, FileVisitResult.CONTINUE);
    }

    public static CountingPathVisitor withBigIntegerCounters() {
        return new CountingPathVisitor(Counters.bigIntegerPathCounters());
    }

    public static CountingPathVisitor withLongCounters() {
        return new CountingPathVisitor(Counters.longPathCounters());
    }

    public CountingPathVisitor(Counters.PathCounters pathCounter) {
        this(pathCounter, CountingPathVisitor.defaultFileFilter(), CountingPathVisitor.defaultDirFilter());
    }

    public CountingPathVisitor(Counters.PathCounters pathCounter, PathFilter fileFilter, PathFilter dirFilter) {
        this.pathCounters = Objects.requireNonNull(pathCounter, "pathCounter");
        this.fileFilter = Objects.requireNonNull(fileFilter, "fileFilter");
        this.dirFilter = Objects.requireNonNull(dirFilter, "dirFilter");
    }

    public CountingPathVisitor(Counters.PathCounters pathCounter, PathFilter fileFilter, PathFilter dirFilter, IOBiFunction<Path, IOException, FileVisitResult> visitFileFailed) {
        super(visitFileFailed);
        this.pathCounters = Objects.requireNonNull(pathCounter, "pathCounter");
        this.fileFilter = Objects.requireNonNull(fileFilter, "fileFilter");
        this.dirFilter = Objects.requireNonNull(dirFilter, "dirFilter");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CountingPathVisitor)) {
            return false;
        }
        CountingPathVisitor other = (CountingPathVisitor)obj;
        return Objects.equals(this.pathCounters, other.pathCounters);
    }

    public Counters.PathCounters getPathCounters() {
        return this.pathCounters;
    }

    public int hashCode() {
        return Objects.hash(this.pathCounters);
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        this.updateDirCounter(dir, exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
        FileVisitResult accept = this.dirFilter.accept(dir, attributes);
        return accept != FileVisitResult.CONTINUE ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
    }

    public String toString() {
        return this.pathCounters.toString();
    }

    protected void updateDirCounter(Path dir, IOException exc) {
        this.pathCounters.getDirectoryCounter().increment();
    }

    protected void updateFileCounters(Path file, BasicFileAttributes attributes) {
        this.pathCounters.getFileCounter().increment();
        this.pathCounters.getByteCounter().add(attributes.size());
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
        if (Files.exists(file, new LinkOption[0]) && this.fileFilter.accept(file, attributes) == FileVisitResult.CONTINUE) {
            this.updateFileCounters(file, attributes);
        }
        return FileVisitResult.CONTINUE;
    }
}

