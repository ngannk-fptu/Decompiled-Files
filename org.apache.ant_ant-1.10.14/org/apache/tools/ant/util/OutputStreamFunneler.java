/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamFunneler {
    public static final long DEFAULT_TIMEOUT_MILLIS = 1000L;
    private OutputStream out;
    private int count = 0;
    private boolean closed;
    private long timeoutMillis;

    public OutputStreamFunneler(OutputStream out) {
        this(out, 1000L);
    }

    public OutputStreamFunneler(OutputStream out, long timeoutMillis) {
        if (out == null) {
            throw new IllegalArgumentException("OutputStreamFunneler.<init>:  out == null");
        }
        this.out = out;
        this.closed = false;
        this.setTimeout(timeoutMillis);
    }

    public synchronized void setTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public synchronized OutputStream getFunnelInstance() throws IOException {
        this.dieIfClosed();
        try {
            Funnel funnel = new Funnel();
            return funnel;
        }
        finally {
            this.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void release(Funnel funnel) throws IOException {
        if (!funnel.closed) {
            try {
                if (this.timeoutMillis > 0L) {
                    long start = System.currentTimeMillis();
                    long end = start + this.timeoutMillis;
                    long now = System.currentTimeMillis();
                    try {
                        while (now < end) {
                            this.wait(end - now);
                            now = System.currentTimeMillis();
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
                if (--this.count == 0) {
                    this.close();
                }
            }
            finally {
                funnel.closed = true;
            }
        }
    }

    private synchronized void close() throws IOException {
        try {
            this.dieIfClosed();
            this.out.close();
        }
        finally {
            this.closed = true;
        }
    }

    private synchronized void dieIfClosed() throws IOException {
        if (this.closed) {
            throw new IOException("The funneled OutputStream has been closed.");
        }
    }

    private final class Funnel
    extends OutputStream {
        private boolean closed = false;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Funnel() {
            OutputStreamFunneler outputStreamFunneler2 = OutputStreamFunneler.this;
            synchronized (outputStreamFunneler2) {
                ++OutputStreamFunneler.this.count;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void flush() throws IOException {
            OutputStreamFunneler outputStreamFunneler = OutputStreamFunneler.this;
            synchronized (outputStreamFunneler) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.flush();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void write(int b) throws IOException {
            OutputStreamFunneler outputStreamFunneler = OutputStreamFunneler.this;
            synchronized (outputStreamFunneler) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void write(byte[] b) throws IOException {
            OutputStreamFunneler outputStreamFunneler = OutputStreamFunneler.this;
            synchronized (outputStreamFunneler) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            OutputStreamFunneler outputStreamFunneler = OutputStreamFunneler.this;
            synchronized (outputStreamFunneler) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b, off, len);
            }
        }

        @Override
        public void close() throws IOException {
            OutputStreamFunneler.this.release(this);
        }
    }
}

