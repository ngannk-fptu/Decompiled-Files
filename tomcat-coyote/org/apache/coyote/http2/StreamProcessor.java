/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.Response;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.coyote.http2.SendfileData;
import org.apache.coyote.http2.Stream;
import org.apache.coyote.http2.StreamException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

class StreamProcessor
extends AbstractProcessor {
    private static final Log log = LogFactory.getLog(StreamProcessor.class);
    private static final StringManager sm = StringManager.getManager(StreamProcessor.class);
    private static final Set<String> H2_PSEUDO_HEADERS_REQUEST = new HashSet<String>();
    private final Lock processLock = new ReentrantLock();
    private final Http2UpgradeHandler handler;
    private final Stream stream;
    private SendfileData sendfileData = null;
    private SendfileState sendfileState = null;

    StreamProcessor(Http2UpgradeHandler handler, Stream stream, Adapter adapter, SocketWrapperBase<?> socketWrapper) {
        super(adapter, stream.getCoyoteRequest(), stream.getCoyoteResponse());
        this.handler = handler;
        this.stream = stream;
        this.setSocketWrapper(socketWrapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void process(SocketEvent event) {
        try {
            this.processLock.lock();
            try {
                AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
                try {
                    state = this.process(this.socketWrapper, event);
                    if (state == AbstractEndpoint.Handler.SocketState.LONG) {
                        this.handler.getProtocol().getHttp11Protocol().addWaitingProcessor(this);
                    } else if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.handler.getProtocol().getHttp11Protocol().removeWaitingProcessor(this);
                        if (!this.stream.isInputFinished() && this.getErrorState().isIoAllowed()) {
                            StreamException se = new StreamException(sm.getString("streamProcessor.cancel", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}), Http2Error.NO_ERROR, this.stream.getIdAsInt());
                            this.stream.close(se);
                        } else if (!this.getErrorState().isConnectionIoAllowed()) {
                            ConnectionException ce = new ConnectionException(sm.getString("streamProcessor.error.connection", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}), Http2Error.INTERNAL_ERROR);
                            this.stream.close(ce);
                        } else if (!this.getErrorState().isIoAllowed()) {
                            StreamException se = this.stream.getResetException();
                            if (se == null) {
                                se = new StreamException(sm.getString("streamProcessor.error.stream", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}), Http2Error.INTERNAL_ERROR, this.stream.getIdAsInt());
                            }
                            this.stream.close(se);
                        } else if (!this.stream.isActive()) {
                            this.stream.recycle();
                        }
                    }
                }
                catch (Exception e) {
                    String msg = sm.getString("streamProcessor.error.connection", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()});
                    if (log.isDebugEnabled()) {
                        log.debug((Object)msg, (Throwable)e);
                    }
                    ConnectionException ce = new ConnectionException(msg, Http2Error.INTERNAL_ERROR, e);
                    this.stream.close(ce);
                    state = AbstractEndpoint.Handler.SocketState.CLOSED;
                }
                finally {
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.recycle();
                    }
                }
            }
            finally {
                this.processLock.unlock();
            }
        }
        finally {
            this.handler.executeQueuedStream();
        }
    }

    @Override
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        if (this.handler.hasAsyncIO() && this.handler.getProtocol().getUseSendfile()) {
            this.prepareSendfile();
        }
        StreamProcessor.prepareHeaders(this.request, this.response, this.sendfileData == null, this.handler.getProtocol(), this.stream);
        this.stream.writeHeaders();
    }

    private void prepareSendfile() {
        String fileName = (String)this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName != null) {
            this.sendfileData = new SendfileData();
            this.sendfileData.path = new File(fileName).toPath();
            this.sendfileData.pos = (Long)this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.start");
            this.sendfileData.end = (Long)this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.end");
            this.sendfileData.left = this.sendfileData.end - this.sendfileData.pos;
            this.sendfileData.stream = this.stream;
        }
    }

    static void prepareHeaders(Request coyoteRequest, Response coyoteResponse, boolean noSendfile, Http2Protocol protocol, Stream stream) {
        MimeHeaders headers = coyoteResponse.getMimeHeaders();
        int statusCode = coyoteResponse.getStatus();
        headers.addValue(":status").setString(Integer.toString(statusCode));
        if (noSendfile && protocol != null && protocol.useCompression(coyoteRequest, coyoteResponse)) {
            stream.addOutputFilter(new GzipOutputFilter());
        }
        if (statusCode >= 200 && statusCode != 204 && statusCode != 205 && statusCode != 304) {
            long contentLength;
            String contentLanguage;
            String contentType = coyoteResponse.getContentType();
            if (contentType != null) {
                headers.setValue("content-type").setString(contentType);
            }
            if ((contentLanguage = coyoteResponse.getContentLanguage()) != null) {
                headers.setValue("content-language").setString(contentLanguage);
            }
            if ((contentLength = coyoteResponse.getContentLengthLong()) != -1L && headers.getValue("content-length") == null) {
                headers.addValue("content-length").setLong(contentLength);
            }
        } else {
            if (stream != null) {
                stream.configureVoidOutputFilter();
            }
            if (statusCode == 205) {
                coyoteResponse.setContentLength(0L);
            } else {
                coyoteResponse.setContentLength(-1L);
            }
        }
        if (statusCode >= 200 && headers.getValue("date") == null) {
            headers.addValue("date").setString(FastHttpDateFormat.getCurrentDate());
        }
    }

    @Override
    protected final void finishResponse() throws IOException {
        this.sendfileState = this.handler.processSendfile(this.sendfileData);
        if (this.sendfileState != SendfileState.PENDING) {
            this.stream.getOutputBuffer().end();
        }
    }

    @Override
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
        if ((continueResponseTiming == ContinueResponseTiming.ALWAYS || continueResponseTiming == this.handler.getProtocol().getContinueResponseTimingInternal()) && !this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.stream.writeAck();
            }
            catch (IOException ioe) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            }
        }
    }

    @Override
    protected final void flush() throws IOException {
        this.stream.getOutputBuffer().flush();
    }

    @Override
    protected final int available(boolean doRead) {
        return this.stream.getInputBuffer().available();
    }

    @Override
    protected final void setRequestBody(ByteChunk body) {
        this.stream.getInputBuffer().insertReplayedBody(body);
        try {
            this.stream.receivedEndOfStream();
        }
        catch (ConnectionException connectionException) {
            // empty catch block
        }
    }

    @Override
    protected final void setSwallowResponse() {
    }

    @Override
    protected final void disableSwallowRequest() {
    }

    @Override
    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        if (dispatch) {
            this.handler.processStreamOnContainerThread(this, event);
        } else {
            this.process(event);
        }
    }

    @Override
    protected final boolean isReadyForRead() {
        return this.stream.getInputBuffer().isReadyForRead();
    }

    @Override
    protected final boolean isRequestBodyFullyRead() {
        return this.stream.getInputBuffer().isRequestBodyFullyRead();
    }

    @Override
    protected final void registerReadInterest() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final boolean isReadyForWrite() {
        return this.stream.isReadyForWrite();
    }

    @Override
    protected final void executeDispatches() {
        Iterator<DispatchType> dispatches = this.getIteratorAndClearDispatches();
        while (dispatches != null && dispatches.hasNext()) {
            DispatchType dispatchType = dispatches.next();
            this.processSocketEvent(dispatchType.getSocketStatus(), true);
        }
    }

    @Override
    protected final boolean isPushSupported() {
        return this.stream.isPushSupported();
    }

    @Override
    protected final void doPush(Request pushTarget) {
        try {
            this.stream.push(pushTarget);
        }
        catch (IOException ioe) {
            this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            this.response.setErrorException(ioe);
        }
    }

    @Override
    protected boolean isTrailerFieldsReady() {
        return this.stream.isTrailerFieldsReady();
    }

    @Override
    protected boolean isTrailerFieldsSupported() {
        return this.stream.isTrailerFieldsSupported();
    }

    @Override
    protected Object getConnectionID() {
        return this.stream.getConnectionId();
    }

    @Override
    protected Object getStreamID() {
        return this.stream.getIdAsString().toString();
    }

    @Override
    public final void recycle() {
        RequestGroupInfo global = this.handler.getProtocol().getGlobal();
        if (global != null) {
            global.removeRequestProcessor(this.request.getRequestProcessor());
        }
        this.setSocketWrapper(null);
    }

    @Override
    protected final Log getLog() {
        return log;
    }

    @Override
    public final void pause() {
    }

    @Override
    public final AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        try {
            if (this.validateRequest()) {
                this.adapter.service(this.request, this.response);
            } else {
                this.response.setStatus(400);
                this.adapter.log(this.request, this.response, 0L);
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("streamProcessor.service.error"), (Throwable)e);
            }
            this.response.setStatus(500);
            this.setErrorState(ErrorState.CLOSE_NOW, e);
        }
        if (this.sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (this.getErrorState().isError()) {
            this.action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        this.action(ActionCode.CLOSE, null);
        this.request.updateCounters();
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private boolean validateRequest() {
        HttpParser httpParser = new HttpParser(((AbstractHttp11Protocol)this.handler.getProtocol().getHttp11Protocol()).getRelaxedPathChars(), ((AbstractHttp11Protocol)this.handler.getProtocol().getHttp11Protocol()).getRelaxedQueryChars());
        String method = this.request.method().toString();
        if (!HttpParser.isToken(method)) {
            return false;
        }
        String scheme = this.request.scheme().toString();
        if (!HttpParser.isScheme(scheme)) {
            return false;
        }
        ByteChunk bc = this.request.requestURI().getByteChunk();
        for (int i = bc.getStart(); i < bc.getEnd(); ++i) {
            if (!httpParser.isNotRequestTargetRelaxed(bc.getBuffer()[i])) continue;
            return false;
        }
        String qs = this.request.queryString().toString();
        if (qs != null) {
            for (char c : qs.toCharArray()) {
                if (httpParser.isQueryRelaxed(c)) continue;
                return false;
            }
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        Enumeration<String> names = headers.names();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (H2_PSEUDO_HEADERS_REQUEST.contains(name) || HttpParser.isToken(name)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected final boolean flushBufferedWrite() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("streamProcessor.flushBufferedWrite.entry", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
        }
        if (this.stream.flush(false)) {
            if (this.stream.isReadyForWrite()) {
                throw new IllegalStateException();
            }
            return true;
        }
        return false;
    }

    @Override
    protected final AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException {
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    static {
        H2_PSEUDO_HEADERS_REQUEST.add(":method");
        H2_PSEUDO_HEADERS_REQUEST.add(":scheme");
        H2_PSEUDO_HEADERS_REQUEST.add(":authority");
        H2_PSEUDO_HEADERS_REQUEST.add(":path");
    }
}

