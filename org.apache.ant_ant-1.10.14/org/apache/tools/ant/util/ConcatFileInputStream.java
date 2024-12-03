/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class ConcatFileInputStream
extends InputStream {
    private static final int EOF = -1;
    private int currentIndex = -1;
    private boolean eof = false;
    private File[] file;
    private InputStream currentStream;
    private ProjectComponent managingPc;

    public ConcatFileInputStream(File[] file) throws IOException {
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        this.closeCurrent();
        this.eof = true;
    }

    @Override
    public int read() throws IOException {
        int result = this.readCurrent();
        if (result == -1 && !this.eof) {
            this.openFile(++this.currentIndex);
            result = this.readCurrent();
        }
        return result;
    }

    public void setManagingTask(Task task) {
        this.setManagingComponent(task);
    }

    public void setManagingComponent(ProjectComponent pc) {
        this.managingPc = pc;
    }

    public void log(String message, int loglevel) {
        if (this.managingPc != null) {
            this.managingPc.log(message, loglevel);
        } else if (loglevel > 1) {
            System.out.println(message);
        } else {
            System.err.println(message);
        }
    }

    private int readCurrent() throws IOException {
        return this.eof || this.currentStream == null ? -1 : this.currentStream.read();
    }

    private void openFile(int index) throws IOException {
        this.closeCurrent();
        if (this.file != null && index < this.file.length) {
            this.log("Opening " + this.file[index], 3);
            try {
                this.currentStream = new BufferedInputStream(Files.newInputStream(this.file[index].toPath(), new OpenOption[0]));
            }
            catch (IOException eyeOhEx) {
                this.log("Failed to open " + this.file[index], 0);
                throw eyeOhEx;
            }
        } else {
            this.eof = true;
        }
    }

    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}

