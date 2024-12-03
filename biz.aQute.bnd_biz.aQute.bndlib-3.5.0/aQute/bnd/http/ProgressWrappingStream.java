/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.http;

import aQute.bnd.service.progress.ProgressPlugin;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressWrappingStream
extends InputStream {
    private InputStream delegate;
    private ProgressPlugin.Task task;
    private int size;
    private int reported;
    private int read;
    private long timeout;
    private long deadline;
    private final AtomicBoolean closed = new AtomicBoolean();

    public ProgressWrappingStream(InputStream delegate, String name, int size, ProgressPlugin.Task task, long timeout) {
        this.delegate = delegate;
        this.task = task;
        this.size = size;
        this.timeout = timeout == 0L ? Long.MAX_VALUE : timeout;
        this.read = 0;
        this.reported = 0;
        this.deadline = System.currentTimeMillis() + timeout;
    }

    @Override
    public int read() throws IOException {
        while (!this.isTimeout()) {
            if (this.task.isCanceled()) {
                throw new EOFException("Canceled");
            }
            try {
                int data = this.delegate.read();
                this.update(data == -1 ? -1 : 1);
                return data;
            }
            catch (SocketTimeoutException e) {
                if (!this.task.isCanceled()) continue;
                throw new EOFException("Canceled");
            }
        }
        throw new EOFException("Timeout");
    }

    private boolean isTimeout() throws IOException {
        if (this.timeout <= 0L) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.deadline < now) {
            this.close();
            return true;
        }
        return false;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        while (!this.isTimeout()) {
            if (this.task.isCanceled()) {
                throw new EOFException("Canceled");
            }
            try {
                int count = this.delegate.read(buffer);
                return this.update(count);
            }
            catch (SocketTimeoutException e) {
                if (!this.task.isCanceled()) continue;
                throw new EOFException("Canceled");
            }
        }
        throw new EOFException("Timeout");
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        while (!this.isTimeout()) {
            if (this.task.isCanceled()) {
                throw new EOFException("Canceled");
            }
            try {
                int count = this.delegate.read(buffer, offset, length);
                return this.update(count);
            }
            catch (SocketTimeoutException e) {
                if (!this.task.isCanceled()) continue;
                throw new EOFException("Canceled");
            }
        }
        throw new EOFException("Timeout");
    }

    public int update(int count) throws IOException {
        if (this.task.isCanceled()) {
            this.close();
            throw new EOFException("Canceled");
        }
        this.deadline = System.currentTimeMillis() + this.timeout;
        if (count != -1) {
            this.read += count;
            int where = (50 + this.read * 100) / this.size;
            int delta = where - this.reported;
            if (delta > 0) {
                this.task.worked(delta);
            }
            this.reported = where;
        } else {
            this.close();
        }
        return count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.closed.getAndSet(true)) {
            return;
        }
        try {
            this.task.done("Finished", null);
        }
        finally {
            this.delegate.close();
        }
    }
}

