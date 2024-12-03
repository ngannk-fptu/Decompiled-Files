/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaException;

public class TemporaryResources
implements Closeable {
    private static final Logger LOG = Logger.getLogger(TemporaryResources.class.getName());
    private final LinkedList<Closeable> resources = new LinkedList();
    private Path tempFileDir = null;

    public void setTemporaryFileDirectory(Path tempFileDir) {
        this.tempFileDir = tempFileDir;
    }

    public void setTemporaryFileDirectory(File tempFileDir) {
        this.tempFileDir = tempFileDir == null ? null : tempFileDir.toPath();
    }

    public Path createTempFile() throws IOException {
        final Path path = this.tempFileDir == null ? Files.createTempFile("apache-tika-", ".tmp", new FileAttribute[0]) : Files.createTempFile(this.tempFileDir, "apache-tika-", ".tmp", new FileAttribute[0]);
        this.addResource(new Closeable(){

            @Override
            public void close() throws IOException {
                try {
                    Files.delete(path);
                }
                catch (IOException e) {
                    LOG.log(Level.WARNING, "delete tmp file failed; will delete it on exit");
                    path.toFile().deleteOnExit();
                }
            }
        });
        return path;
    }

    public File createTemporaryFile() throws IOException {
        return this.createTempFile().toFile();
    }

    public void addResource(Closeable resource) {
        this.resources.addFirst(resource);
    }

    public <T extends Closeable> T getResource(Class<T> klass) {
        for (Closeable resource : this.resources) {
            if (!klass.isAssignableFrom(resource.getClass())) continue;
            return (T)resource;
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;
        for (Closeable resource : this.resources) {
            try {
                resource.close();
            }
            catch (IOException e) {
                if (exception == null) {
                    exception = e;
                    continue;
                }
                exception.addSuppressed(e);
            }
        }
        this.resources.clear();
        if (exception != null) {
            throw exception;
        }
    }

    public void dispose() throws TikaException {
        try {
            this.close();
        }
        catch (IOException e) {
            throw new TikaException("Failed to close temporary resources", e);
        }
    }
}

