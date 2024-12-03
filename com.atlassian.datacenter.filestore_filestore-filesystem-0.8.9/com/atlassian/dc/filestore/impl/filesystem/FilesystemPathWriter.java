/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$OutputStreamWriter
 *  com.atlassian.dc.filestore.api.FileStore$Writer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dc.filestore.impl.filesystem;

import com.atlassian.dc.filestore.api.FileStore;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FilesystemPathWriter
implements FileStore.Writer {
    private static final Logger log = LoggerFactory.getLogger(FilesystemPathWriter.class);
    private final Path targetFile;

    public FilesystemPathWriter(Path targetFile) {
        this.targetFile = Objects.requireNonNull(targetFile);
    }

    public void write(InputStream source) throws IOException {
        this.writeInternal(dest -> Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING));
    }

    public void write(FileStore.OutputStreamWriter writer) throws IOException {
        this.writeInternal(dest -> {
            try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(dest, new OpenOption[0]));){
                writer.writeTo((OutputStream)output);
            }
        });
    }

    public void write(byte[] data) throws IOException {
        this.writeInternal(dest -> Files.write(dest, data, new OpenOption[0]));
    }

    private void writeInternal(Writer writer) throws IOException {
        log.trace("Ensuring parent directory {} exists", (Object)this.parentDirectory());
        Files.createDirectories(this.parentDirectory(), new FileAttribute[0]);
        Path tempFile = Files.createTempFile(this.parentDirectory(), ".incomplete", ".tmp", new FileAttribute[0]);
        try {
            log.trace("Writing data to temp file {}", (Object)tempFile);
            writer.writeTo(tempFile);
            log.trace("Renaming temp file {} atomically to {}", (Object)tempFile, (Object)this.targetFile);
            this.moveFile(tempFile);
        }
        catch (IOException | RuntimeException ex) {
            Files.deleteIfExists(tempFile);
            throw ex;
        }
    }

    private void moveFile(Path tempFile) throws IOException {
        try {
            Files.move(tempFile, this.targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AtomicMoveNotSupportedException ex) {
            log.warn("Atomic move not supported when renaming {} to {}, falling back to copy-and-delete", (Object)tempFile, (Object)this.targetFile);
            Files.copy(tempFile, this.targetFile, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(tempFile);
        }
    }

    private Path parentDirectory() {
        Path parent = this.targetFile.getParent();
        if (parent == null) {
            throw new IllegalStateException("Targetfile does not have a parent directory: " + this.targetFile);
        }
        return parent;
    }

    @FunctionalInterface
    private static interface Writer {
        public void writeTo(Path var1) throws IOException;
    }
}

