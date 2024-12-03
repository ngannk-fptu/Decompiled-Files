/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.Closeable;

public abstract class InfoStream
implements Closeable,
Cloneable {
    public static final InfoStream NO_OUTPUT;
    private static InfoStream defaultInfoStream;

    public abstract void message(String var1, String var2);

    public abstract boolean isEnabled(String var1);

    public static synchronized InfoStream getDefault() {
        return defaultInfoStream;
    }

    public static synchronized void setDefault(InfoStream infoStream) {
        if (infoStream == null) {
            throw new IllegalArgumentException("Cannot set InfoStream default implementation to null. To disable logging use InfoStream.NO_OUTPUT");
        }
        defaultInfoStream = infoStream;
    }

    public InfoStream clone() {
        try {
            return (InfoStream)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    static {
        defaultInfoStream = NO_OUTPUT = new NoOutput();
    }

    private static final class NoOutput
    extends InfoStream {
        private NoOutput() {
        }

        @Override
        public void message(String component, String message) {
            assert (false) : "message() should not be called when isEnabled returns false";
        }

        @Override
        public boolean isEnabled(String component) {
            return false;
        }

        @Override
        public void close() {
        }
    }
}

