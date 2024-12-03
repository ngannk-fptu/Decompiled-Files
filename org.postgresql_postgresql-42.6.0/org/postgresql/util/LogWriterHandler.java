/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.io.Writer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.jdbc.ResourceLock;

public class LogWriterHandler
extends Handler {
    private @Nullable Writer writer;
    private final ResourceLock lock = new ResourceLock();

    public LogWriterHandler(Writer inWriter) {
        this.setLevel(Level.INFO);
        this.setFilter(null);
        this.setFormatter(new SimpleFormatter());
        this.setWriter(inWriter);
    }

    @Override
    public void publish(LogRecord record) {
        String formatted;
        Formatter formatter = this.getFormatter();
        try {
            formatted = formatter.format(record);
        }
        catch (Exception ex) {
            this.reportError("Error Formatting record", ex, 5);
            return;
        }
        if (formatted.length() == 0) {
            return;
        }
        try (ResourceLock ignore = this.lock.obtain();){
            Writer writer = this.writer;
            if (writer != null) {
                writer.write(formatted);
            }
        }
        catch (Exception ex) {
            this.reportError("Error writing message", ex, 1);
        }
    }

    @Override
    public void flush() {
        try (ResourceLock ignore = this.lock.obtain();){
            Writer writer = this.writer;
            if (writer != null) {
                writer.flush();
            }
        }
        catch (Exception ex) {
            this.reportError("Error on flush", ex, 1);
        }
    }

    @Override
    public void close() throws SecurityException {
        try (ResourceLock ignore = this.lock.obtain();){
            Writer writer = this.writer;
            if (writer != null) {
                writer.close();
            }
        }
        catch (Exception ex) {
            this.reportError("Error closing writer", ex, 1);
        }
    }

    private void setWriter(Writer writer) throws IllegalArgumentException {
        try (ResourceLock ignore = this.lock.obtain();){
            if (writer == null) {
                throw new IllegalArgumentException("Writer cannot be null");
            }
            this.writer = writer;
            try {
                writer.write(this.getFormatter().getHead(this));
            }
            catch (Exception ex) {
                this.reportError("Error writing head section", ex, 1);
            }
        }
    }
}

