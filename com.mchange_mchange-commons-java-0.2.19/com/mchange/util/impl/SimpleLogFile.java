/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.MessageLogger;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

public class SimpleLogFile
implements MessageLogger {
    PrintWriter logWriter;
    DateFormat df = DateFormat.getDateTimeInstance(3, 3);

    public SimpleLogFile(File file, String string) throws UnsupportedEncodingException, IOException {
        this.logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file.getAbsolutePath(), true), string)), true);
    }

    public SimpleLogFile(File file) throws IOException {
        this.logWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath(), true)), true);
    }

    @Override
    public synchronized void log(String string) throws IOException {
        this.logMessage(string);
        this.flush();
    }

    @Override
    public synchronized void log(Throwable throwable, String string) throws IOException {
        this.logMessage(string);
        throwable.printStackTrace(this.logWriter);
        this.flush();
    }

    private void logMessage(String string) {
        this.logWriter.println(this.df.format(new Date()) + " -- " + string);
    }

    private void flush() {
        this.logWriter.flush();
    }

    public synchronized void close() {
        this.logWriter.close();
    }

    public void finalize() {
        this.close();
    }
}

