/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedList;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporaryResources
implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(TemporaryResources.class);
    private final LinkedList<Closeable> resources = new LinkedList();
    private Path tempFileDir = null;

    public void setTemporaryFileDirectory(Path tempFileDir) {
        this.tempFileDir = tempFileDir;
    }

    public void setTemporaryFileDirectory(File tempFileDir) {
        this.tempFileDir = tempFileDir == null ? null : tempFileDir.toPath();
    }

    public Path createTempFile(String suffix) throws IOException {
        String actualSuffix = StringUtils.isBlank(suffix) ? ".tmp" : suffix;
        Path path = this.tempFileDir == null ? Files.createTempFile("apache-tika-", actualSuffix, new FileAttribute[0]) : Files.createTempFile(this.tempFileDir, "apache-tika-", actualSuffix, new FileAttribute[0]);
        this.addResource(() -> {
            try {
                Files.delete(path);
            }
            catch (IOException e) {
                LOG.warn("delete tmp file fail, will delete it on exit");
                path.toFile().deleteOnExit();
            }
        });
        return path;
    }

    public Path createTempFile() throws IOException {
        return this.createTempFile("");
    }

    public Path createTempFile(Metadata metadata) throws IOException {
        String resourceName = metadata.get("resourceName");
        if (StringUtils.isBlank(resourceName)) {
            return this.createTempFile("");
        }
        return this.createTempFile(FilenameUtils.getSuffixFromPath(resourceName));
    }

    public File createTemporaryFile() throws IOException {
        return this.createTempFile("").toFile();
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

