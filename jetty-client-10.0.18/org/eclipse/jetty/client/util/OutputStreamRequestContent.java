/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.FutureCallback
 */
package org.eclipse.jetty.client.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import org.eclipse.jetty.client.util.AsyncRequestContent;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;

public class OutputStreamRequestContent
extends AsyncRequestContent {
    private final AsyncOutputStream output = new AsyncOutputStream();

    public OutputStreamRequestContent() {
        this("application/octet-stream");
    }

    public OutputStreamRequestContent(String contentType) {
        super(contentType, new ByteBuffer[0]);
    }

    public OutputStream getOutputStream() {
        return this.output;
    }

    private class AsyncOutputStream
    extends OutputStream {
        private AsyncOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
            this.write(new byte[]{(byte)b}, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            try {
                FutureCallback callback = new FutureCallback();
                OutputStreamRequestContent.this.offer(ByteBuffer.wrap(b, off, len), (Callback)callback);
                callback.get();
            }
            catch (InterruptedException x) {
                throw new InterruptedIOException();
            }
            catch (ExecutionException x) {
                throw new IOException(x.getCause());
            }
        }

        @Override
        public void flush() throws IOException {
            OutputStreamRequestContent.this.flush();
        }

        @Override
        public void close() {
            OutputStreamRequestContent.this.close();
        }
    }
}

