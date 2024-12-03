/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.util.FileUtils;

public class PumpStreamHandler
implements ExecuteStreamHandler {
    private Thread outputThread;
    private Thread errorThread;
    private Thread inputThread;
    private OutputStream out;
    private OutputStream err;
    private InputStream input;
    private final boolean nonBlockingRead;
    private static final long JOIN_TIMEOUT = 200L;

    public PumpStreamHandler(OutputStream out, OutputStream err, InputStream input, boolean nonBlockingRead) {
        if (out == null) {
            throw new NullPointerException("out must not be null");
        }
        if (err == null) {
            throw new NullPointerException("err must not be null");
        }
        this.out = out;
        this.err = err;
        this.input = input;
        this.nonBlockingRead = nonBlockingRead;
    }

    public PumpStreamHandler(OutputStream out, OutputStream err, InputStream input) {
        this(out, err, input, false);
    }

    public PumpStreamHandler(OutputStream out, OutputStream err) {
        this(out, err, null);
    }

    public PumpStreamHandler(OutputStream outAndErr) {
        this(outAndErr, outAndErr);
    }

    public PumpStreamHandler() {
        this(System.out, System.err);
    }

    @Override
    public void setProcessOutputStream(InputStream is) {
        this.createProcessOutputPump(is, this.out);
    }

    @Override
    public void setProcessErrorStream(InputStream is) {
        this.createProcessErrorPump(is, this.err);
    }

    @Override
    public void setProcessInputStream(OutputStream os) {
        if (this.input != null) {
            this.inputThread = this.createPump(this.input, os, true, this.nonBlockingRead);
        } else {
            FileUtils.close(os);
        }
    }

    @Override
    public void start() {
        this.start(this.outputThread);
        this.start(this.errorThread);
        this.start(this.inputThread);
    }

    @Override
    public void stop() {
        this.finish(this.inputThread);
        try {
            this.err.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            this.out.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.finish(this.outputThread);
        this.finish(this.errorThread);
    }

    private void start(Thread t) {
        if (t != null) {
            t.start();
        }
    }

    protected final void finish(Thread t) {
        if (t == null) {
            return;
        }
        try {
            StreamPumper s = null;
            if (t instanceof ThreadWithPumper) {
                s = ((ThreadWithPumper)t).getPumper();
            }
            if (s != null && s.isFinished()) {
                return;
            }
            if (!t.isAlive()) {
                return;
            }
            StreamPumper.PostStopHandle postStopHandle = null;
            if (s != null && !s.isFinished()) {
                postStopHandle = s.stop();
            }
            if (postStopHandle != null && postStopHandle.isInPostStopTasks()) {
                postStopHandle.awaitPostStopCompletion(2L, TimeUnit.SECONDS);
            }
            while ((s == null || !s.isFinished()) && t.isAlive()) {
                t.interrupt();
                t.join(200L);
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    protected OutputStream getErr() {
        return this.err;
    }

    protected OutputStream getOut() {
        return this.out;
    }

    protected void createProcessOutputPump(InputStream is, OutputStream os) {
        this.outputThread = this.createPump(is, os);
    }

    protected void createProcessErrorPump(InputStream is, OutputStream os) {
        this.errorThread = this.createPump(is, os);
    }

    protected Thread createPump(InputStream is, OutputStream os) {
        return this.createPump(is, os, false);
    }

    protected Thread createPump(InputStream is, OutputStream os, boolean closeWhenExhausted) {
        return this.createPump(is, os, closeWhenExhausted, true);
    }

    protected Thread createPump(InputStream is, OutputStream os, boolean closeWhenExhausted, boolean nonBlockingIO) {
        StreamPumper pumper = new StreamPumper(is, os, closeWhenExhausted, nonBlockingIO);
        pumper.setAutoflush(true);
        ThreadWithPumper result = new ThreadWithPumper(pumper);
        result.setDaemon(true);
        return result;
    }

    protected static class ThreadWithPumper
    extends Thread {
        private final StreamPumper pumper;

        public ThreadWithPumper(StreamPumper p) {
            super(p);
            this.pumper = p;
        }

        protected StreamPumper getPumper() {
            return this.pumper;
        }
    }
}

