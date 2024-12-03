/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.eclipse.jetty.client.AsyncContentProvider;
import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;

@Deprecated
public class OutputStreamContentProvider
implements AsyncContentProvider,
Callback,
Closeable {
    private final DeferredContentProvider deferred = new DeferredContentProvider(new ByteBuffer[0]);
    private final OutputStream output = new DeferredOutputStream();

    public Invocable.InvocationType getInvocationType() {
        return this.deferred.getInvocationType();
    }

    @Override
    public long getLength() {
        return this.deferred.getLength();
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.deferred.iterator();
    }

    @Override
    public void setListener(AsyncContentProvider.Listener listener) {
        this.deferred.setListener(listener);
    }

    public OutputStream getOutputStream() {
        return this.output;
    }

    protected void write(ByteBuffer buffer) {
        this.deferred.offer(buffer);
    }

    @Override
    public void close() {
        this.deferred.close();
    }

    public void succeeded() {
        this.deferred.succeeded();
    }

    public void failed(Throwable failure) {
        this.deferred.failed(failure);
    }

    private class DeferredOutputStream
    extends OutputStream {
        private DeferredOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
            this.write(new byte[]{(byte)b}, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            OutputStreamContentProvider.this.write(ByteBuffer.wrap(b, off, len));
            this.flush();
        }

        @Override
        public void flush() throws IOException {
            OutputStreamContentProvider.this.deferred.flush();
        }

        @Override
        public void close() throws IOException {
            OutputStreamContentProvider.this.close();
        }
    }
}

