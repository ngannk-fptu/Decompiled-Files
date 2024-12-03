/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Request$Content
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.client.util.AsyncRequestContent
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IteratingCallback
 *  org.eclipse.jetty.util.IteratingCallback$Action
 */
package org.eclipse.jetty.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.AsyncRequestContent;
import org.eclipse.jetty.proxy.AbstractProxyServlet;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;

public class AsyncProxyServlet
extends ProxyServlet {
    private static final String WRITE_LISTENER_ATTRIBUTE = AsyncProxyServlet.class.getName() + ".writeListener";

    @Override
    protected Request.Content proxyRequestContent(HttpServletRequest request, HttpServletResponse response, Request proxyRequest) throws IOException {
        AsyncRequestContent content = new AsyncRequestContent(new ByteBuffer[0]);
        request.getInputStream().setReadListener(this.newReadListener(request, response, proxyRequest, content));
        return content;
    }

    protected ReadListener newReadListener(HttpServletRequest request, HttpServletResponse response, Request proxyRequest, AsyncRequestContent content) {
        return new StreamReader(request, response, proxyRequest, content);
    }

    @Override
    protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length, Callback callback) {
        try {
            StreamWriter writeListener;
            if (this._log.isDebugEnabled()) {
                this._log.debug("{} proxying content to downstream: {} bytes", (Object)this.getRequestId(request), (Object)length);
            }
            if ((writeListener = (StreamWriter)request.getAttribute(WRITE_LISTENER_ATTRIBUTE)) == null) {
                writeListener = this.newWriteListener(request, proxyResponse);
                request.setAttribute(WRITE_LISTENER_ATTRIBUTE, (Object)writeListener);
                writeListener.data(buffer, offset, length, callback);
                response.getOutputStream().setWriteListener((WriteListener)writeListener);
            } else {
                writeListener.data(buffer, offset, length, callback);
                writeListener.onWritePossible();
            }
        }
        catch (Throwable x) {
            callback.failed(x);
            proxyResponse.abort(x);
        }
    }

    protected StreamWriter newWriteListener(HttpServletRequest request, Response proxyResponse) {
        return new StreamWriter(request, proxyResponse);
    }

    protected class StreamReader
    extends IteratingCallback
    implements ReadListener {
        private final byte[] buffer;
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final Request proxyRequest;
        private final AsyncRequestContent content;

        protected StreamReader(HttpServletRequest request, HttpServletResponse response, Request proxyRequest, AsyncRequestContent content) {
            this.buffer = new byte[AsyncProxyServlet.this.getHttpClient().getRequestBufferSize()];
            this.request = request;
            this.response = response;
            this.proxyRequest = proxyRequest;
            this.content = content;
        }

        public void onDataAvailable() {
            this.iterate();
        }

        public void onAllDataRead() {
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                AsyncProxyServlet.this._log.debug("{} proxying content to upstream completed", (Object)AsyncProxyServlet.this.getRequestId(this.request));
            }
            this.content.close();
        }

        public void onError(Throwable t) {
            AsyncProxyServlet.this.onClientRequestFailure(this.request, this.proxyRequest, this.response, t);
        }

        protected IteratingCallback.Action process() throws Exception {
            int requestId = AsyncProxyServlet.this._log.isDebugEnabled() ? AsyncProxyServlet.this.getRequestId(this.request) : 0;
            ServletInputStream input = this.request.getInputStream();
            while (input.isReady()) {
                int read = input.read(this.buffer);
                if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                    AsyncProxyServlet.this._log.debug("{} asynchronous read {} bytes on {}", new Object[]{requestId, read, input});
                }
                if (read > 0) {
                    if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                        AsyncProxyServlet.this._log.debug("{} proxying content to upstream: {} bytes", (Object)requestId, (Object)read);
                    }
                    this.onRequestContent(this.request, this.proxyRequest, this.content, this.buffer, 0, read, (Callback)this);
                    return IteratingCallback.Action.SCHEDULED;
                }
                if (read >= 0) continue;
                if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                    AsyncProxyServlet.this._log.debug("{} asynchronous read complete on {}", (Object)requestId, (Object)input);
                }
                return IteratingCallback.Action.SUCCEEDED;
            }
            if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                AsyncProxyServlet.this._log.debug("{} asynchronous read pending on {}", (Object)requestId, (Object)input);
            }
            return IteratingCallback.Action.IDLE;
        }

        protected void onRequestContent(HttpServletRequest request, Request proxyRequest, AsyncRequestContent content, byte[] buffer, int offset, int length, Callback callback) {
            content.offer(ByteBuffer.wrap(buffer, offset, length), callback);
        }

        public void failed(Throwable x) {
            super.failed(x);
            this.onError(x);
        }
    }

    protected class StreamWriter
    implements WriteListener {
        private final HttpServletRequest request;
        private final Response proxyResponse;
        private WriteState state;
        private byte[] buffer;
        private int offset;
        private int length;
        private Callback callback;

        protected StreamWriter(HttpServletRequest request, Response proxyResponse) {
            this.request = request;
            this.proxyResponse = proxyResponse;
            this.state = WriteState.IDLE;
        }

        protected void data(byte[] bytes, int offset, int length, Callback callback) {
            if (this.state != WriteState.IDLE) {
                throw new WritePendingException();
            }
            this.state = WriteState.READY;
            this.buffer = bytes;
            this.offset = offset;
            this.length = length;
            this.callback = callback;
        }

        public void onWritePossible() throws IOException {
            int requestId = AsyncProxyServlet.this.getRequestId(this.request);
            ServletOutputStream output = this.request.getAsyncContext().getResponse().getOutputStream();
            if (this.state == WriteState.READY) {
                if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                    AsyncProxyServlet.this._log.debug("{} asynchronous write start of {} bytes on {}", new Object[]{requestId, this.length, output});
                }
                output.write(this.buffer, this.offset, this.length);
                this.state = WriteState.PENDING;
                if (output.isReady()) {
                    if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                        AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes completed on {}", new Object[]{requestId, this.length, output});
                    }
                    this.complete();
                } else if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                    AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes pending on {}", new Object[]{requestId, this.length, output});
                }
            } else if (this.state == WriteState.PENDING) {
                if (AsyncProxyServlet.this._log.isDebugEnabled()) {
                    AsyncProxyServlet.this._log.debug("{} asynchronous write of {} bytes completing on {}", new Object[]{requestId, this.length, output});
                }
                this.complete();
            } else {
                throw new IllegalStateException();
            }
        }

        protected void complete() {
            this.buffer = null;
            this.offset = 0;
            this.length = 0;
            Callback c = this.callback;
            this.callback = null;
            this.state = WriteState.IDLE;
            c.succeeded();
        }

        public void onError(Throwable failure) {
            this.proxyResponse.abort(failure);
        }
    }

    private static enum WriteState {
        READY,
        PENDING,
        IDLE;

    }

    public static class Transparent
    extends AsyncProxyServlet {
        private final AbstractProxyServlet.TransparentDelegate delegate = new AbstractProxyServlet.TransparentDelegate(this);

        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            this.delegate.init(config);
        }

        @Override
        protected String rewriteTarget(HttpServletRequest clientRequest) {
            return this.delegate.rewriteTarget(clientRequest);
        }
    }
}

