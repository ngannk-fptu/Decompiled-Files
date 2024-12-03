/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes.fetcher.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.pipes.fetcher.AbstractFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFetcher
extends AbstractFetcher
implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemFetcher.class);
    private Path basePath = null;

    static boolean isDescendant(Path root, Path descendant) {
        return descendant.toAbsolutePath().normalize().startsWith(root.toAbsolutePath().normalize());
    }

    @Override
    public InputStream fetch(String fetchKey, Metadata metadata) throws IOException, TikaException {
        if (fetchKey.contains("\u0000")) {
            throw new IllegalArgumentException("Path must not contain \u0000. Please review the life decisions that led you to requesting a file name with this character in it.");
        }
        Path p = null;
        if (this.basePath != null) {
            p = this.basePath.resolve(fetchKey);
            if (!p.toRealPath(new LinkOption[0]).startsWith(this.basePath.toRealPath(new LinkOption[0]))) {
                throw new IllegalArgumentException("fetchKey must resolve to be a descendant of the 'basePath'");
            }
        } else {
            p = Paths.get(fetchKey, new String[0]);
        }
        metadata.set(TikaCoreProperties.SOURCE_PATH, fetchKey);
        if (!Files.isRegularFile(p, new LinkOption[0])) {
            if (this.basePath != null && !Files.isDirectory(this.basePath, new LinkOption[0])) {
                throw new IOException("BasePath is not a directory: " + this.basePath);
            }
            throw new FileNotFoundException(p.toAbsolutePath().toString());
        }
        return TikaInputStream.get(p, metadata);
    }

    public Path getBasePath() {
        return this.basePath;
    }

    @Field
    public void setBasePath(String basePath) {
        this.basePath = Paths.get(basePath, new String[0]);
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
        if (this.basePath == null || this.basePath.toString().trim().length() == 0) {
            LOG.warn("'basePath' has not been set. This means that client code or clients can read from any file that this process has permissions to read. If you are running tika-server, make absolutely certain that you've locked down access to tika-server and file-permissions for the tika-server process.");
            return;
        }
        if (this.basePath.toString().startsWith("http://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please use the tika-fetcher-http module for http calls");
        }
        if (this.basePath.toString().startsWith("ftp://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please consider contributing an ftp fetcher module");
        }
        if (this.basePath.toString().startsWith("s3://")) {
            throw new TikaConfigException("FileSystemFetcher only works with local file systems.  Please use the tika-fetcher-s3 module");
        }
        if (this.basePath.toAbsolutePath().toString().contains("\u0000")) {
            throw new TikaConfigException("base path must not contain \u0000. Seriously, what were you thinking?");
        }
        LOG.info("A FileSystemFetcher ({}) has been initialized. Clients will be able to read all files under '{}' if this process has permission to read them.", (Object)this.getName(), (Object)this.basePath.toAbsolutePath());
    }
}

