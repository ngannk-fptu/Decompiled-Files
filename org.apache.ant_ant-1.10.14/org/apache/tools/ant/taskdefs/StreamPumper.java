/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.util.FileUtils;

public class StreamPumper
implements Runnable {
    private static final int SMALL_BUFFER_SIZE = 128;
    private final InputStream is;
    private final OutputStream os;
    private volatile boolean askedToStop;
    private volatile boolean finished;
    private final boolean closeWhenExhausted;
    private boolean autoflush = false;
    private Exception exception = null;
    private int bufferSize = 128;
    private boolean started = false;
    private final boolean useAvailable;
    private PostStopHandle postStopHandle;
    private static final long POLL_INTERVAL = 100L;

    public StreamPumper(InputStream is, OutputStream os, boolean closeWhenExhausted) {
        this(is, os, closeWhenExhausted, false);
    }

    public StreamPumper(InputStream is, OutputStream os, boolean closeWhenExhausted, boolean useAvailable) {
        this.is = is;
        this.os = os;
        this.closeWhenExhausted = closeWhenExhausted;
        this.useAvailable = useAvailable;
    }

    public StreamPumper(InputStream is, OutputStream os) {
        this(is, os, false);
    }

    void setAutoflush(boolean autoflush) {
        this.autoflush = autoflush;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        StreamPumper streamPumper = this;
        synchronized (streamPumper) {
            this.started = true;
        }
        this.finished = false;
        byte[] buf = new byte[this.bufferSize];
        try {
            while (!this.askedToStop && !Thread.interrupted()) {
                int length2;
                this.waitForInput(this.is);
                if (this.askedToStop || Thread.interrupted() || (length2 = this.is.read(buf)) < 0) break;
                if (length2 <= 0) continue;
                this.os.write(buf, 0, length2);
                if (!this.autoflush) continue;
                this.os.flush();
            }
            this.doPostStop();
        }
        catch (InterruptedException length2) {
            if (this.closeWhenExhausted) {
                FileUtils.close(this.os);
            }
            this.finished = true;
            this.askedToStop = false;
            StreamPumper length2 = this;
            synchronized (length2) {
                this.notifyAll();
            }
        }
        catch (Exception e) {
            StreamPumper streamPumper2 = this;
            synchronized (streamPumper2) {
                this.exception = e;
            }
        }
        finally {
            if (this.closeWhenExhausted) {
                FileUtils.close(this.os);
            }
            this.finished = true;
            this.askedToStop = false;
            StreamPumper streamPumper3 = this;
            synchronized (streamPumper3) {
                this.notifyAll();
            }
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public synchronized void waitFor() throws InterruptedException {
        while (!this.isFinished()) {
            this.wait();
        }
    }

    public synchronized void setBufferSize(int bufferSize) {
        if (this.started) {
            throw new IllegalStateException("Cannot set buffer size on a running StreamPumper");
        }
        this.bufferSize = bufferSize;
    }

    public synchronized int getBufferSize() {
        return this.bufferSize;
    }

    public synchronized Exception getException() {
        return this.exception;
    }

    synchronized PostStopHandle stop() {
        this.askedToStop = true;
        this.postStopHandle = new PostStopHandle();
        this.notifyAll();
        return this.postStopHandle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitForInput(InputStream is) throws IOException, InterruptedException {
        if (this.useAvailable) {
            while (!this.askedToStop && is.available() == 0) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                StreamPumper streamPumper = this;
                synchronized (streamPumper) {
                    this.wait(100L);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doPostStop() throws IOException {
        try {
            byte[] buf = new byte[this.bufferSize];
            if (this.askedToStop) {
                int length;
                int bytesReadableWithoutBlocking;
                while ((bytesReadableWithoutBlocking = this.is.available()) > 0 && (length = this.is.read(buf, 0, Math.min(bytesReadableWithoutBlocking, buf.length))) > 0) {
                    this.os.write(buf, 0, length);
                }
            }
            this.os.flush();
        }
        finally {
            if (this.postStopHandle != null) {
                this.postStopHandle.latch.countDown();
                this.postStopHandle.inPostStopTasks = false;
            }
        }
    }

    final class PostStopHandle {
        private boolean inPostStopTasks = true;
        private final CountDownLatch latch = new CountDownLatch(1);

        PostStopHandle() {
        }

        boolean isInPostStopTasks() {
            return this.inPostStopTasks;
        }

        boolean awaitPostStopCompletion(long timeout, TimeUnit timeUnit) throws InterruptedException {
            return this.latch.await(timeout, timeUnit);
        }
    }
}

