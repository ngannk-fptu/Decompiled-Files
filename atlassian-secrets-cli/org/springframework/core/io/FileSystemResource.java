/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class FileSystemResource
extends AbstractResource
implements WritableResource {
    private final File file;
    private final String path;

    public FileSystemResource(File file) {
        Assert.notNull((Object)file, "File must not be null");
        this.file = file;
        this.path = StringUtils.cleanPath(file.getPath());
    }

    public FileSystemResource(String path) {
        Assert.notNull((Object)path, "Path must not be null");
        this.file = new File(path);
        this.path = StringUtils.cleanPath(path);
    }

    public final String getPath() {
        return this.path;
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public boolean isReadable() {
        return this.file.canRead() && !this.file.isDirectory();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return Files.newInputStream(this.file.toPath(), new OpenOption[0]);
        }
        catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public boolean isWritable() {
        return this.file.canWrite() && !this.file.isDirectory();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.file.toPath(), new OpenOption[0]);
    }

    @Override
    public URL getURL() throws IOException {
        return this.file.toURI().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return this.file.toURI();
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return FileChannel.open(this.file.toPath(), StandardOpenOption.READ);
        }
        catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public WritableByteChannel writableChannel() throws IOException {
        return FileChannel.open(this.file.toPath(), StandardOpenOption.WRITE);
    }

    @Override
    public long contentLength() throws IOException {
        return this.file.length();
    }

    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new FileSystemResource(pathToUse);
    }

    @Override
    public String getFilename() {
        return this.file.getName();
    }

    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof FileSystemResource && this.path.equals(((FileSystemResource)other).path);
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}

