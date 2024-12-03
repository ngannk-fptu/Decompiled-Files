/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.poi.util.RandomSingleton;
import org.apache.poi.util.TempFileCreationStrategy;

public class DefaultTempFileCreationStrategy
implements TempFileCreationStrategy {
    public static final String POIFILES = "poifiles";
    public static final String DELETE_FILES_ON_EXIT = "poi.delete.tmp.files.on.exit";
    private File dir;

    public DefaultTempFileCreationStrategy() {
        this(null);
    }

    public DefaultTempFileCreationStrategy(File dir) {
        this.dir = dir;
    }

    private void createPOIFilesDirectory() throws IOException {
        if (this.dir == null) {
            String tmpDir = System.getProperty("java.io.tmpdir");
            if (tmpDir == null) {
                throw new IOException("Systems temporary directory not defined - set the -Djava.io.tmpdir jvm property!");
            }
            this.dir = new File(tmpDir, POIFILES);
        }
        this.createTempDirectory(this.dir);
    }

    private synchronized void createTempDirectory(File directory) throws IOException {
        boolean dirExists;
        boolean bl = dirExists = directory.exists() || directory.mkdirs();
        if (!dirExists) {
            throw new IOException("Could not create temporary directory '" + directory + "'");
        }
        if (!directory.isDirectory()) {
            throw new IOException("Could not create temporary directory. '" + directory + "' exists but is not a directory.");
        }
    }

    @Override
    public File createTempFile(String prefix, String suffix) throws IOException {
        this.createPOIFilesDirectory();
        File newFile = Files.createTempFile(this.dir.toPath(), prefix, suffix, new FileAttribute[0]).toFile();
        if (System.getProperty(DELETE_FILES_ON_EXIT) != null) {
            newFile.deleteOnExit();
        }
        return newFile;
    }

    @Override
    public File createTempDirectory(String prefix) throws IOException {
        this.createPOIFilesDirectory();
        long n = RandomSingleton.getInstance().nextLong();
        File newDirectory = new File(this.dir, prefix + Long.toString(n));
        this.createTempDirectory(newDirectory);
        newDirectory.deleteOnExit();
        return newDirectory;
    }
}

