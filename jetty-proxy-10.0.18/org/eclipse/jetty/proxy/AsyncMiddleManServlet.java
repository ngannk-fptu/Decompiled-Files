/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.client.ContentDecoder
 *  org.eclipse.jetty.client.GZIPContentDecoder
 *  org.eclipse.jetty.client.HttpClient
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Request$Content
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.client.api.Response$CompleteListener
 *  org.eclipse.jetty.client.api.Response$Listener$Adapter
 *  org.eclipse.jetty.client.api.Result
 *  org.eclipse.jetty.client.util.AsyncRequestContent
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.io.RuntimeIOException
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.CountingCallback
 *  org.eclipse.jetty.util.IteratingCallback
 *  org.eclipse.jetty.util.IteratingCallback$Action
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.ContentDecoder;
import org.eclipse.jetty.client.GZIPContentDecoder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.AsyncRequestContent;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.proxy.AbstractProxyServlet;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.CountingCallback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.component.Destroyable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMiddleManServlet
extends AbstractProxyServlet {
    private static final String PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".proxyRequestContentCommitted";
    private static final String CLIENT_TRANSFORMER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".clientTransformer";
    private static final String SERVER_TRANSFORMER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".serverTransformer";
    private static final String CONTINUE_ACTION_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".continueAction";
    private static final String WRITE_LISTENER_ATTRIBUTE = AsyncMiddleManServlet.class.getName() + ".writeListener";

    protected void service(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) throws ServletException, IOException {
        String rewrittenTarget = this.rewriteTarget(clientRequest);
        if (this._log.isDebugEnabled()) {
            StringBuffer target = clientRequest.getRequestURL();
            if (clientRequest.getQueryString() != null) {
                target.append("?").append(clientRequest.getQueryString());
            }
            this._log.debug("{} rewriting: {} -> {}", new Object[]{this.getRequestId(clientRequest), target, rewrittenTarget});
        }
        if (rewrittenTarget == null) {
            this.onProxyRewriteFailed(clientRequest, proxyResponse);
            return;
        }
        Request proxyRequest = this.newProxyRequest(clientRequest, rewrittenTarget);
        this.copyRequestHeaders(clientRequest, proxyRequest);
        this.addProxyHeaders(clientRequest, proxyRequest);
        AsyncContext asyncContext = clientRequest.startAsync();
        asyncContext.setTimeout(0L);
        proxyRequest.timeout(this.getTimeout(), TimeUnit.MILLISECONDS);
        if (this.hasContent(clientRequest)) {
            AsyncRequestContent content = this.newProxyRequestContent(clientRequest, proxyResponse, proxyRequest);
            proxyRequest.body((Request.Content)content);
            if (this.expects100Continue(clientRequest)) {
                proxyRequest.attribute(CONTINUE_ACTION_ATTRIBUTE, () -> {
                    try {
                        ServletInputStream input = clientRequest.getInputStream();
                        input.setReadListener(this.newProxyReadListener(clientRequest, proxyResponse, proxyRequest, content));
                    }
                    catch (Throwable failure) {
                        this.onClientRequestFailure(clientRequest, proxyRequest, proxyResponse, failure);
                    }
                });
                this.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            } else {
                ServletInputStream input = clientRequest.getInputStream();
                input.setReadListener(this.newProxyReadListener(clientRequest, proxyResponse, proxyRequest, content));
            }
        } else {
            this.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
        }
    }

    protected AsyncRequestContent newProxyRequestContent(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
        return new ProxyAsyncRequestContent(clientRequest);
    }

    protected ReadListener newProxyReadListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest, AsyncRequestContent content) {
        return new ProxyReader(clientRequest, proxyResponse, proxyRequest, content);
    }

    protected ProxyWriter newProxyWriteListener(HttpServletRequest clientRequest, Response proxyResponse) {
        return new ProxyWriter(clientRequest, proxyResponse);
    }

    @Override
    protected Response.CompleteListener newProxyResponseListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) {
        return new ProxyResponseListener(clientRequest, proxyResponse);
    }

    protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
        return ContentTransformer.IDENTITY;
    }

    protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
        return ContentTransformer.IDENTITY;
    }

    @Override
    protected void onContinue(HttpServletRequest clientRequest, Request proxyRequest) {
        super.onContinue(clientRequest, proxyRequest);
        Runnable action = (Runnable)proxyRequest.getAttributes().get(CONTINUE_ACTION_ATTRIBUTE);
        action.run();
    }

    private void transform(ContentTransformer transformer, ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
        try {
            transformer.transform(input, finished, output);
        }
        catch (Throwable x) {
            this._log.info("Exception while transforming {} ", (Object)transformer, (Object)x);
            throw x;
        }
    }

    int readClientRequestContent(ServletInputStream input, byte[] buffer) throws IOException {
        return input.read(buffer);
    }

    void writeProxyResponseContent(ServletOutputStream output, ByteBuffer content) throws IOException {
        AsyncMiddleManServlet.write((OutputStream)output, content);
    }

    private static void write(OutputStream output, ByteBuffer content) throws IOException {
        byte[] buffer;
        int length = content.remaining();
        int offset = 0;
        if (content.hasArray()) {
            offset = content.arrayOffset();
            buffer = content.array();
        } else {
            buffer = new byte[length];
            content.get(buffer);
        }
        output.write(buffer, offset, length);
    }

    private void cleanup(HttpServletRequest clientRequest) {
        ContentTransformer serverTransformer;
        ContentTransformer clientTransformer = (ContentTransformer)clientRequest.getAttribute(CLIENT_TRANSFORMER_ATTRIBUTE);
        if (clientTransformer instanceof Destroyable) {
            ((Destroyable)clientTransformer).destroy();
        }
        if ((serverTransformer = (ContentTransformer)clientRequest.getAttribute(SERVER_TRANSFORMER_ATTRIBUTE)) instanceof Destroyable) {
            ((Destroyable)serverTransformer).destroy();
        }
    }

    private class ProxyAsyncRequestContent
    extends AsyncRequestContent {
        private final HttpServletRequest clientRequest;

        private ProxyAsyncRequestContent(HttpServletRequest clientRequest) {
            super(new ByteBuffer[0]);
            this.clientRequest = clientRequest;
        }

        public boolean offer(ByteBuffer buffer, Callback callback) {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} proxying content to upstream: {} bytes", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest), (Object)buffer.remaining());
            }
            return super.offer(buffer, callback);
        }
    }

    protected class ProxyReader
    extends IteratingCallback
    implements ReadListener {
        private final byte[] buffer;
        private final List<ByteBuffer> buffers;
        private final HttpServletRequest clientRequest;
        private final HttpServletResponse proxyResponse;
        private final Request proxyRequest;
        private final AsyncRequestContent content;
        private final int contentLength;
        private final boolean expects100Continue;
        private int length;

        protected ProxyReader(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest, AsyncRequestContent content) {
            this.buffer = new byte[AsyncMiddleManServlet.this.getHttpClient().getRequestBufferSize()];
            this.buffers = new ArrayList<ByteBuffer>();
            this.clientRequest = clientRequest;
            this.proxyResponse = proxyResponse;
            this.proxyRequest = proxyRequest;
            this.content = content;
            this.contentLength = clientRequest.getContentLength();
            this.expects100Continue = AsyncMiddleManServlet.this.expects100Continue(clientRequest);
        }

        public void onDataAvailable() {
            this.iterate();
        }

        public void onAllDataRead() throws IOException {
            if (!this.content.isClosed()) {
                this.process(BufferUtil.EMPTY_BUFFER, new Callback(){

                    public void failed(Throwable x) {
                        ProxyReader.this.onError(x);
                    }
                }, true);
            }
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} proxying content to upstream completed", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest));
            }
        }

        public void onError(Throwable t) {
            AsyncMiddleManServlet.this.cleanup(this.clientRequest);
            AsyncMiddleManServlet.this.onClientRequestFailure(this.clientRequest, this.proxyRequest, this.proxyResponse, t);
        }

        protected IteratingCallback.Action process() throws Exception {
            ServletInputStream input = this.clientRequest.getInputStream();
            while (input.isReady() && !input.isFinished()) {
                int read = AsyncMiddleManServlet.this.readClientRequestContent(input, this.buffer);
                if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                    AsyncMiddleManServlet.this._log.debug("{} asynchronous read {} bytes on {}", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), read, input});
                }
                if (read < 0) {
                    return IteratingCallback.Action.SUCCEEDED;
                }
                if (this.contentLength > 0 && read > 0) {
                    this.length += read;
                }
                ByteBuffer content = read > 0 ? ByteBuffer.wrap(this.buffer, 0, read) : BufferUtil.EMPTY_BUFFER;
                boolean finished = this.length == this.contentLength;
                this.process(content, (Callback)this, finished);
                if (read <= 0) continue;
                return IteratingCallback.Action.SCHEDULED;
            }
            if (input.isFinished()) {
                if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                    AsyncMiddleManServlet.this._log.debug("{} asynchronous read complete on {}", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest), (Object)input);
                }
                return IteratingCallback.Action.SUCCEEDED;
            }
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} asynchronous read pending on {}", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest), (Object)input);
            }
            return IteratingCallback.Action.IDLE;
        }

        private void process(ByteBuffer content, Callback callback, boolean finished) throws IOException {
            boolean contentCommitted;
            int contentBytes;
            ContentTransformer transformer = (ContentTransformer)this.clientRequest.getAttribute(CLIENT_TRANSFORMER_ATTRIBUTE);
            if (transformer == null) {
                transformer = AsyncMiddleManServlet.this.newClientRequestContentTransformer(this.clientRequest, this.proxyRequest);
                this.clientRequest.setAttribute(CLIENT_TRANSFORMER_ATTRIBUTE, (Object)transformer);
            }
            if ((contentBytes = content.remaining()) == 0 && !finished) {
                callback.succeeded();
                return;
            }
            AsyncMiddleManServlet.this.transform(transformer, content, finished, this.buffers);
            int newContentBytes = 0;
            int size = this.buffers.size();
            if (size > 0) {
                CountingCallback counter = new CountingCallback(callback, size);
                for (ByteBuffer buffer : this.buffers) {
                    newContentBytes += buffer.remaining();
                    this.content.offer(buffer, (Callback)counter);
                }
                this.buffers.clear();
            }
            if (finished) {
                this.content.close();
            }
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} upstream content transformation {} -> {} bytes", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), contentBytes, newContentBytes});
            }
            boolean bl = contentCommitted = this.clientRequest.getAttribute(PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE) != null;
            if (!contentCommitted && (size > 0 || finished)) {
                this.clientRequest.setAttribute(PROXY_REQUEST_CONTENT_COMMITTED_ATTRIBUTE, (Object)true);
                if (!this.expects100Continue) {
                    this.proxyRequest.headers(headers -> headers.remove(HttpHeader.CONTENT_LENGTH));
                    AsyncMiddleManServlet.this.sendProxyRequest(this.clientRequest, this.proxyResponse, this.proxyRequest);
                }
            }
            if (size == 0) {
                callback.succeeded();
            }
        }

        protected void onCompleteFailure(Throwable x) {
            this.onError(x);
        }
    }

    protected class ProxyWriter
    implements WriteListener {
        private final Queue<Chunk> chunks = new ArrayDeque<Chunk>();
        private final HttpServletRequest clientRequest;
        private final Response serverResponse;
        private Chunk chunk;
        private boolean writePending;

        protected ProxyWriter(HttpServletRequest clientRequest, Response serverResponse) {
            this.clientRequest = clientRequest;
            this.serverResponse = serverResponse;
        }

        public boolean offer(ByteBuffer content, Callback callback) {
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} proxying content to downstream: {} bytes {}", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), content.remaining(), callback});
            }
            return this.chunks.offer(new Chunk(content, callback));
        }

        public void onWritePossible() throws IOException {
            ServletOutputStream output = this.clientRequest.getAsyncContext().getResponse().getOutputStream();
            if (this.writePending) {
                if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                    AsyncMiddleManServlet.this._log.debug("{} pending async write complete of {} on {}", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), this.chunk, output});
                }
                this.writePending = false;
                if (this.succeed(this.chunk.callback)) {
                    return;
                }
            }
            int length = 0;
            Chunk chunk = null;
            while (output.isReady()) {
                if (chunk != null) {
                    if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                        AsyncMiddleManServlet.this._log.debug("{} async write complete of {} ({} bytes) on {}", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), chunk, length, output});
                    }
                    if (this.succeed(chunk.callback)) {
                        return;
                    }
                }
                this.chunk = chunk = this.chunks.poll();
                if (chunk == null) {
                    return;
                }
                length = chunk.buffer.remaining();
                if (length <= 0) continue;
                AsyncMiddleManServlet.this.writeProxyResponseContent(output, chunk.buffer);
            }
            if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                AsyncMiddleManServlet.this._log.debug("{} async write pending of {} ({} bytes) on {}", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), chunk, length, output});
            }
            this.writePending = true;
        }

        private boolean succeed(Callback callback) {
            callback.succeeded();
            return this.writePending;
        }

        public void onError(Throwable failure) {
            Chunk chunk = this.chunk;
            if (chunk != null) {
                chunk.callback.failed(failure);
            } else {
                this.serverResponse.abort(failure);
            }
        }
    }

    protected class ProxyResponseListener
    extends Response.Listener.Adapter
    implements Callback {
        private final Callback complete = new CountingCallback((Callback)this, 2);
        private final List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
        private final HttpServletRequest clientRequest;
        private final HttpServletResponse proxyResponse;
        private boolean hasContent;
        private long contentLength;
        private long length;
        private Response response;

        protected ProxyResponseListener(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) {
            this.clientRequest = clientRequest;
            this.proxyResponse = proxyResponse;
        }

        public void onBegin(Response serverResponse) {
            this.response = serverResponse;
            this.proxyResponse.setStatus(serverResponse.getStatus());
        }

        public void onHeaders(Response serverResponse) {
            this.contentLength = serverResponse.getHeaders().getLongField(HttpHeader.CONTENT_LENGTH);
            AsyncMiddleManServlet.this.onServerResponseHeaders(this.clientRequest, this.proxyResponse, serverResponse);
        }

        public void onContent(Response serverResponse, ByteBuffer content, Callback callback) {
            try {
                ContentTransformer transformer;
                boolean committed;
                int contentBytes = content.remaining();
                if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                    AsyncMiddleManServlet.this._log.debug("{} received server content: {} bytes", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest), (Object)contentBytes);
                }
                this.hasContent = true;
                ProxyWriter proxyWriter = (ProxyWriter)this.clientRequest.getAttribute(WRITE_LISTENER_ATTRIBUTE);
                boolean bl = committed = proxyWriter != null;
                if (proxyWriter == null) {
                    proxyWriter = AsyncMiddleManServlet.this.newProxyWriteListener(this.clientRequest, serverResponse);
                    this.clientRequest.setAttribute(WRITE_LISTENER_ATTRIBUTE, (Object)proxyWriter);
                }
                if ((transformer = (ContentTransformer)this.clientRequest.getAttribute(SERVER_TRANSFORMER_ATTRIBUTE)) == null) {
                    transformer = AsyncMiddleManServlet.this.newServerResponseContentTransformer(this.clientRequest, this.proxyResponse, serverResponse);
                    this.clientRequest.setAttribute(SERVER_TRANSFORMER_ATTRIBUTE, (Object)transformer);
                }
                this.length += (long)contentBytes;
                boolean finished = this.contentLength >= 0L && this.length == this.contentLength;
                AsyncMiddleManServlet.this.transform(transformer, content, finished, this.buffers);
                int newContentBytes = 0;
                int size = this.buffers.size();
                if (size > 0) {
                    Callback counter = size == 1 ? callback : new CountingCallback(callback, size);
                    for (ByteBuffer buffer : this.buffers) {
                        newContentBytes += buffer.remaining();
                        proxyWriter.offer(buffer, counter);
                    }
                    this.buffers.clear();
                } else {
                    proxyWriter.offer(BufferUtil.EMPTY_BUFFER, callback);
                }
                if (finished) {
                    proxyWriter.offer(BufferUtil.EMPTY_BUFFER, this.complete);
                }
                if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                    AsyncMiddleManServlet.this._log.debug("{} downstream content transformation {} -> {} bytes", new Object[]{AsyncMiddleManServlet.this.getRequestId(this.clientRequest), contentBytes, newContentBytes});
                }
                if (committed) {
                    proxyWriter.onWritePossible();
                } else {
                    if (this.contentLength >= 0L) {
                        this.proxyResponse.setContentLength(-1);
                    }
                    this.proxyResponse.getOutputStream().setWriteListener((WriteListener)proxyWriter);
                }
            }
            catch (Throwable x) {
                callback.failed(x);
            }
        }

        public void onSuccess(Response serverResponse) {
            try {
                if (this.hasContent) {
                    if (this.contentLength < 0L) {
                        ProxyWriter proxyWriter = (ProxyWriter)this.clientRequest.getAttribute(WRITE_LISTENER_ATTRIBUTE);
                        ContentTransformer transformer = (ContentTransformer)this.clientRequest.getAttribute(SERVER_TRANSFORMER_ATTRIBUTE);
                        AsyncMiddleManServlet.this.transform(transformer, BufferUtil.EMPTY_BUFFER, true, this.buffers);
                        long newContentBytes = 0L;
                        int size = this.buffers.size();
                        if (size > 0) {
                            Callback callback = size == 1 ? this.complete : new CountingCallback(this.complete, size);
                            for (ByteBuffer buffer : this.buffers) {
                                newContentBytes += (long)buffer.remaining();
                                proxyWriter.offer(buffer, callback);
                            }
                            this.buffers.clear();
                        } else {
                            proxyWriter.offer(BufferUtil.EMPTY_BUFFER, this.complete);
                        }
                        if (AsyncMiddleManServlet.this._log.isDebugEnabled()) {
                            AsyncMiddleManServlet.this._log.debug("{} downstream content transformation to {} bytes", (Object)AsyncMiddleManServlet.this.getRequestId(this.clientRequest), (Object)newContentBytes);
                        }
                        proxyWriter.onWritePossible();
                    }
                } else {
                    this.complete.succeeded();
                }
            }
            catch (Throwable x) {
                this.complete.failed(x);
            }
        }

        public void onComplete(Result result) {
            if (result.isSucceeded()) {
                this.complete.succeeded();
            } else {
                this.complete.failed(result.getFailure());
            }
        }

        public void succeeded() {
            AsyncMiddleManServlet.this.cleanup(this.clientRequest);
            AsyncMiddleManServlet.this.onProxyResponseSuccess(this.clientRequest, this.proxyResponse, this.response);
        }

        public void failed(Throwable failure) {
            AsyncMiddleManServlet.this.cleanup(this.clientRequest);
            AsyncMiddleManServlet.this.onProxyResponseFailure(this.clientRequest, this.proxyResponse, this.response, failure);
        }
    }

    public static interface ContentTransformer {
        public static final ContentTransformer IDENTITY = new IdentityContentTransformer();

        public void transform(ByteBuffer var1, boolean var2, List<ByteBuffer> var3) throws IOException;
    }

    private static class Chunk {
        private final ByteBuffer buffer;
        private final Callback callback;

        private Chunk(ByteBuffer buffer, Callback callback) {
            this.buffer = Objects.requireNonNull(buffer);
            this.callback = Objects.requireNonNull(callback);
        }
    }

    public static class GZIPContentTransformer
    implements ContentTransformer {
        private static final Logger logger = LoggerFactory.getLogger(GZIPContentTransformer.class);
        private final List<ByteBuffer> buffers = new ArrayList<ByteBuffer>(2);
        private final ContentTransformer transformer;
        private final ContentDecoder decoder;
        private final ByteArrayOutputStream out;
        private final GZIPOutputStream gzipOut;

        public GZIPContentTransformer(ContentTransformer transformer) {
            this(null, transformer);
        }

        public GZIPContentTransformer(HttpClient httpClient, ContentTransformer transformer) {
            try {
                this.transformer = transformer;
                ByteBufferPool byteBufferPool = httpClient == null ? null : httpClient.getByteBufferPool();
                this.decoder = new GZIPContentDecoder(byteBufferPool, 8192);
                this.out = new ByteArrayOutputStream();
                this.gzipOut = new GZIPOutputStream(this.out);
            }
            catch (IOException x) {
                throw new RuntimeIOException((Throwable)x);
            }
        }

        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
            if (logger.isDebugEnabled()) {
                logger.debug("Ungzipping {} bytes, finished={}", (Object)input.remaining(), (Object)finished);
            }
            List<ByteBuffer> decodeds = Collections.emptyList();
            if (!input.hasRemaining()) {
                if (finished) {
                    this.transformer.transform(input, true, this.buffers);
                }
            } else {
                boolean decodeComplete;
                decodeds = new ArrayList();
                do {
                    boolean complete;
                    ByteBuffer decoded = this.decoder.decode(input);
                    decodeds.add(decoded);
                    decodeComplete = !input.hasRemaining() && !decoded.hasRemaining();
                    boolean bl = complete = finished && decodeComplete;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ungzipped {} bytes, complete={}", (Object)decoded.remaining(), (Object)complete);
                    }
                    if (!decoded.hasRemaining() && !complete) continue;
                    this.transformer.transform(decoded, complete, this.buffers);
                } while (!decodeComplete);
            }
            if (!this.buffers.isEmpty() || finished) {
                ByteBuffer result = this.gzip(this.buffers, finished);
                this.buffers.clear();
                output.add(result);
            }
            decodeds.forEach(arg_0 -> ((ContentDecoder)this.decoder).release(arg_0));
        }

        private ByteBuffer gzip(List<ByteBuffer> buffers, boolean finished) throws IOException {
            for (ByteBuffer buffer : buffers) {
                AsyncMiddleManServlet.write(this.gzipOut, buffer);
            }
            if (finished) {
                this.gzipOut.close();
            }
            byte[] gzipBytes = this.out.toByteArray();
            this.out.reset();
            return ByteBuffer.wrap(gzipBytes);
        }
    }

    private static class IdentityContentTransformer
    implements ContentTransformer {
        private IdentityContentTransformer() {
        }

        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
            output.add(input);
        }
    }

    public static class Transparent
    extends AsyncMiddleManServlet {
        private final AbstractProxyServlet.TransparentDelegate delegate = new AbstractProxyServlet.TransparentDelegate(this);

        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            this.delegate.init(config);
        }

        @Override
        protected String rewriteTarget(HttpServletRequest request) {
            return this.delegate.rewriteTarget(request);
        }
    }
}

