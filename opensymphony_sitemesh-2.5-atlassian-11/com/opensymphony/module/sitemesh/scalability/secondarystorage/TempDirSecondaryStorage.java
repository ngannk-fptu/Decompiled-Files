/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempDirSecondaryStorage
implements SecondaryStorage {
    protected static Logger logger = Logger.getLogger(TempDirSecondaryStorage.class.getName());
    private static final String UTF_8 = "UTF-8";
    private final long memoryLimitBeforeUse;
    private Writer captureWriter;
    private File tempFile;
    private File tempDirectory;

    public TempDirSecondaryStorage(long memoryLimitBeforeUse) {
        this(memoryLimitBeforeUse, null);
    }

    public TempDirSecondaryStorage(long memoryLimitBeforeUse, File tempDirectory) {
        this.memoryLimitBeforeUse = memoryLimitBeforeUse;
        this.tempDirectory = tempDirectory;
    }

    public long getMemoryLimitBeforeUse() {
        return this.memoryLimitBeforeUse;
    }

    public File getTempDirectory() {
        return this.tempDirectory;
    }

    protected void ensureIsOpen() {
        if (this.captureWriter == null) {
            try {
                this.tempFile = this.getTempFile();
                this.captureWriter = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.tempFile), UTF_8));
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to create SiteMesh secondary storage in temp directory");
            }
        }
    }

    protected File getTempFile() throws IOException {
        return File.createTempFile("sitemesh-spillover-" + this.memoryLimitBeforeUse + "-", ".txt", this.tempDirectory);
    }

    public void write(int c) throws IOException {
        this.ensureIsOpen();
        this.captureWriter.write(c);
    }

    public void write(char[] chars, int off, int len) throws IOException {
        this.ensureIsOpen();
        this.captureWriter.write(chars, off, len);
    }

    public void write(String str, int off, int len) throws IOException {
        this.ensureIsOpen();
        this.captureWriter.write(str, off, len);
    }

    public void write(String str) throws IOException {
        this.ensureIsOpen();
        this.captureWriter.write(str);
    }

    public void writeTo(Writer out) throws IOException {
        if (this.captureWriter != null) {
            this.captureWriter.close();
            this.captureWriter = null;
            Reader reader = null;
            try {
                int read;
                reader = new InputStreamReader((InputStream)new FileInputStream(this.tempFile), UTF_8);
                char[] temp = new char[8192];
                while ((read = reader.read(temp)) != -1) {
                    out.write(temp, 0, read);
                }
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException("Unable to open temporary SiteMesh storage file " + this.tempFile, e);
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    public void cleanUp() {
        try {
            this.cleanupImplementation();
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to clean up SiteMesh secondary storage", e);
        }
        this.captureWriter = null;
    }

    protected void cleanupImplementation() throws IOException {
        if (this.captureWriter != null) {
            this.captureWriter.close();
        }
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
    }
}

