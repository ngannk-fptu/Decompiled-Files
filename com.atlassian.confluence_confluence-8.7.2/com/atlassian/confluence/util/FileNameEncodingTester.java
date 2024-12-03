/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNameEncodingTester {
    private static final Logger log = LoggerFactory.getLogger(FileNameEncodingTester.class);
    public static final String TEST_STRING = "I\u00f1t\u00ebrn\u00e2ti\u00f4n\u00e0liz\u00e6ti\u00f8n";
    public static final String TEST_STRING_CHANGED = "I\u00f1t\u00ebrn\u00e2ti\u00f4n\u00e0liz\u00e6ti\u00f9n";
    private boolean fileNameMangled = false;
    private Throwable fileCreationException = null;
    private boolean fileCreationSuccessful = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FileNameEncodingTester(File testDir) {
        File encodingTestFile = null;
        if (!testDir.exists()) {
            throw new IllegalArgumentException(testDir + "does not exist");
        }
        if (!testDir.isDirectory()) {
            throw new IllegalArgumentException(testDir + "is not a directory");
        }
        try {
            File alternateFile = new File(testDir, TEST_STRING_CHANGED);
            this.debugLogging("alternateFile", alternateFile);
            encodingTestFile = new File(testDir, TEST_STRING);
            this.debugLogging("encodingTestFile", encodingTestFile);
            encodingTestFile.createNewFile();
            this.debugLogging("after createNewFile: encodingTestFile", encodingTestFile);
            this.fileNameMangled = alternateFile.exists();
            log.debug("fileNameMangled: {}", (Object)this.fileNameMangled);
            log.debug("(encodingTestFile != null: {}", (Object)(encodingTestFile != null ? 1 : 0));
        }
        catch (IOException e) {
            try {
                log.debug("IOException caught", (Throwable)e);
                this.fileCreationException = e;
                log.debug("(encodingTestFile != null: {}", (Object)(encodingTestFile != null ? 1 : 0));
            }
            catch (Throwable throwable) {
                log.debug("(encodingTestFile != null: {}", (Object)(encodingTestFile != null ? 1 : 0));
                log.debug("(encodingTestFile.exists()) {}", (Object)(encodingTestFile != null && encodingTestFile.exists() ? 1 : 0));
                if (encodingTestFile != null && encodingTestFile.exists()) {
                    this.fileCreationSuccessful = true;
                    encodingTestFile.delete();
                }
                log.debug("fileCreationSuccessful: {}", (Object)this.fileCreationSuccessful);
                throw throwable;
            }
            log.debug("(encodingTestFile.exists()) {}", (Object)(encodingTestFile != null && encodingTestFile.exists() ? 1 : 0));
            if (encodingTestFile != null && encodingTestFile.exists()) {
                this.fileCreationSuccessful = true;
                encodingTestFile.delete();
            }
            log.debug("fileCreationSuccessful: {}", (Object)this.fileCreationSuccessful);
        }
        log.debug("(encodingTestFile.exists()) {}", (Object)(encodingTestFile != null && encodingTestFile.exists() ? 1 : 0));
        if (encodingTestFile != null && encodingTestFile.exists()) {
            this.fileCreationSuccessful = true;
            encodingTestFile.delete();
        }
        log.debug("fileCreationSuccessful: {}", (Object)this.fileCreationSuccessful);
    }

    private void debugLogging(String message, File file) {
        log.debug(message + ": {}", (Object)file);
        if (file != null) {
            log.debug("getName(): {}", (Object)file.getName());
            log.debug("getAbsolutePath(): {}", (Object)file.getAbsolutePath());
        } else {
            log.debug("File is null");
        }
    }

    public boolean isFileNameMangled() {
        return this.fileNameMangled;
    }

    public Throwable getFileCreationException() {
        return this.fileCreationException;
    }

    public boolean isFileCreationSuccessful() {
        return this.fileCreationSuccessful;
    }
}

