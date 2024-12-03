/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import io.atlassian.util.concurrent.LazyReference;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value="imageCache")
public class ImageCache {
    private static final Logger log = LoggerFactory.getLogger(ImageCache.class);
    private final HierarchicalContentFileSystemHelper fileSystemHelper = new HierarchicalContentFileSystemHelper();
    private final Supplier<File> cacheDirRef;

    @Inject
    ImageCache(final @ComponentImport ApplicationProperties applicationProperties) {
        this.cacheDirRef = new LazyReference<File>(){

            protected File create() throws Exception {
                return ImageCache.initCacheDir(applicationProperties);
            }
        };
    }

    private static File initCacheDir(ApplicationProperties applicationProperties) throws IOException {
        File cacheDir = new File(applicationProperties.getHomeDirectory(), "imgEffects");
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new IOException("The specified path: " + cacheDir + " doesn't exist and we are unable to create it.");
        }
        if (!cacheDir.canRead() || !cacheDir.canWrite()) {
            throw new IOException("Confluence doesn't have read/write access to the specified cache directory:" + cacheDir + ".");
        }
        Object[] existingPreviews = cacheDir.listFiles((dir, name) -> name.startsWith("preview"));
        if (existingPreviews != null) {
            log.debug("Deleting existing preview files {}", (Object)Arrays.toString(existingPreviews));
            for (Object existingPreview : existingPreviews) {
                ((File)existingPreview).delete();
            }
        }
        return cacheDir;
    }

    public InputStream get(String name, long modified) {
        return this.get(Optional.empty(), name, modified);
    }

    public InputStream get(Optional<Long> attachmentId, String name, long modified) {
        File imageFolder;
        File file = imageFolder = attachmentId.isPresent() ? this.fileSystemHelper.getDirectory(this.cacheDir(), attachmentId.get().longValue()) : this.cacheDir();
        if (!imageFolder.exists()) {
            return null;
        }
        File child = new File(imageFolder, name + ".png").getAbsoluteFile();
        FileInputStream in = null;
        if (child.exists() && child.canRead() && child.lastModified() >= modified) {
            try {
                in = new FileInputStream(child);
            }
            catch (Exception e) {
                try {
                    if (in != null) {
                        ((InputStream)in).close();
                    }
                }
                catch (IOException ioe) {
                    log.warn("could not close input file", (Throwable)ioe);
                }
            }
        }
        return in;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(Optional<Long> attachmentId, String name, byte[] val) {
        File imageFolder = this.getImageFolder(attachmentId);
        File file = new File(imageFolder, name + ".png").getAbsoluteFile();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            ((OutputStream)out).write(val, 0, val.length);
        }
        catch (Exception ioe) {
        }
        finally {
            try {
                if (out != null) {
                    ((OutputStream)out).close();
                }
            }
            catch (IOException ioe) {
                log.warn("could not close output file", (Throwable)ioe);
            }
        }
    }

    private File getImageFolder(Optional<Long> attachmentId) {
        File imageFolder = this.cacheDir();
        if (attachmentId.isPresent() && !(imageFolder = this.fileSystemHelper.getDirectory(this.cacheDir(), attachmentId.get().longValue())).exists()) {
            imageFolder = this.fileSystemHelper.createDirectoryHierarchy(this.cacheDir(), attachmentId.get().longValue());
        }
        return imageFolder;
    }

    File cacheDir() {
        return this.cacheDirRef.get();
    }
}

