/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Writable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class WritablePath
implements Path,
Writable {
    private final String encoding;
    private final Path delegate;

    public WritablePath(Path delegate) {
        this(delegate, null);
    }

    public WritablePath(Path delegate, String encoding) {
        this.encoding = encoding;
        this.delegate = delegate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Writer writeTo(Writer out) throws IOException {
        try (InputStreamReader reader = this.encoding == null ? new InputStreamReader(Files.newInputStream(this, new OpenOption[0])) : new InputStreamReader(Files.newInputStream(this, new OpenOption[0]), Charset.forName(this.encoding));){
            int c = ((Reader)reader).read();
            while (c != -1) {
                out.write(c);
                c = ((Reader)reader).read();
            }
        }
        return out;
    }

    @Override
    public FileSystem getFileSystem() {
        return this.delegate.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return this.delegate.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return this.delegate.getRoot();
    }

    @Override
    public Path getFileName() {
        return this.delegate.getFileName();
    }

    @Override
    public Path getParent() {
        return this.delegate.getParent();
    }

    @Override
    public int getNameCount() {
        return this.delegate.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return this.delegate.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return this.delegate.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return this.delegate.startsWith(other);
    }

    @Override
    public boolean startsWith(String other) {
        return this.delegate.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return this.delegate.endsWith(other);
    }

    @Override
    public boolean endsWith(String other) {
        return this.delegate.endsWith(other);
    }

    @Override
    public Path normalize() {
        return this.delegate.normalize();
    }

    @Override
    public Path resolve(Path other) {
        return this.delegate.resolve(other);
    }

    @Override
    public Path resolve(String other) {
        return this.delegate.resolve(other);
    }

    @Override
    public Path resolveSibling(Path other) {
        return this.delegate.resolveSibling(other);
    }

    @Override
    public Path resolveSibling(String other) {
        return this.delegate.resolveSibling(other);
    }

    @Override
    public Path relativize(Path other) {
        return this.delegate.relativize(other);
    }

    @Override
    public URI toUri() {
        return this.delegate.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return this.delegate.toAbsolutePath();
    }

    @Override
    public Path toRealPath(LinkOption ... options) throws IOException {
        return this.delegate.toRealPath(options);
    }

    @Override
    public File toFile() {
        return this.delegate.toFile();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier ... modifiers) throws IOException {
        return this.delegate.register(watcher, events, modifiers);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?> ... events) throws IOException {
        return this.delegate.register(watcher, events);
    }

    @Override
    public Iterator<Path> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public int compareTo(Path other) {
        return this.delegate.compareTo(other);
    }

    @Override
    public boolean equals(Object other) {
        return this.delegate.equals(other);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}

