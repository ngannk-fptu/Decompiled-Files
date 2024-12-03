/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Constants;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.coyote.http11.filters.VoidOutputFilter;
import org.apache.coyote.http2.AbstractNonZeroStream;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.HpackException;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.Http2OutputBuffer;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.coyote.http2.RecycledStream;
import org.apache.coyote.http2.StreamException;
import org.apache.coyote.http2.StreamProcessor;
import org.apache.coyote.http2.WindowAllocationManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.tomcat.util.res.StringManager;

class Stream
extends AbstractNonZeroStream
implements HpackDecoder.HeaderEmitter {
    private static final Log log = LogFactory.getLog(Stream.class);
    private static final StringManager sm = StringManager.getManager(Stream.class);
    private static final int HEADER_STATE_START = 0;
    private static final int HEADER_STATE_PSEUDO = 1;
    private static final int HEADER_STATE_REGULAR = 2;
    private static final int HEADER_STATE_TRAILER = 3;
    private static final MimeHeaders ACK_HEADERS;
    private static final Integer HTTP_UPGRADE_STREAM;
    private static final Set<String> HTTP_CONNECTION_SPECIFIC_HEADERS;
    private volatile long contentLengthReceived = 0L;
    private final Http2UpgradeHandler handler;
    private final WindowAllocationManager allocationManager = new WindowAllocationManager(this);
    private final Request coyoteRequest;
    private final Response coyoteResponse = new Response();
    private final StreamInputBuffer inputBuffer;
    private final StreamOutputBuffer streamOutputBuffer = new StreamOutputBuffer();
    private final Http2OutputBuffer http2OutputBuffer = new Http2OutputBuffer(this.coyoteResponse, this.streamOutputBuffer);
    private int headerState = 0;
    private StreamException headerException = null;
    private volatile StringBuilder cookieHeader = null;
    private volatile boolean hostHeaderSeen = false;
    private Object pendingWindowUpdateForStreamLock = new Object();
    private int pendingWindowUpdateForStream = 0;
    private volatile int urgency = 3;
    private volatile boolean incremental = false;

    Stream(Integer identifier, Http2UpgradeHandler handler) {
        this(identifier, handler, null);
    }

    Stream(Integer identifier, Http2UpgradeHandler handler, Request coyoteRequest) {
        super(handler.getConnectionId(), identifier);
        this.handler = handler;
        this.setWindowSize(handler.getRemoteSettings().getInitialWindowSize());
        if (coyoteRequest == null) {
            this.coyoteRequest = new Request();
            this.inputBuffer = new StandardStreamInputBuffer();
            this.coyoteRequest.setInputBuffer(this.inputBuffer);
        } else {
            this.coyoteRequest = coyoteRequest;
            this.inputBuffer = new SavedRequestStreamInputBuffer((SavedRequestInputFilter)coyoteRequest.getInputBuffer());
            this.state.receivedStartOfHeaders();
            if (HTTP_UPGRADE_STREAM.equals(identifier)) {
                try {
                    this.prepareRequest();
                }
                catch (IllegalArgumentException iae) {
                    this.coyoteResponse.setStatus(400);
                    this.coyoteResponse.setError();
                }
            }
            this.state.receivedEndOfStream();
        }
        this.coyoteRequest.setSendfile(handler.hasAsyncIO() && handler.getProtocol().getUseSendfile());
        this.coyoteResponse.setOutputBuffer(this.http2OutputBuffer);
        this.coyoteRequest.setResponse(this.coyoteResponse);
        this.coyoteRequest.protocol().setString("HTTP/2.0");
        if (this.coyoteRequest.getStartTime() < 0L) {
            this.coyoteRequest.setStartTime(System.currentTimeMillis());
        }
    }

    private void prepareRequest() {
        int i;
        MessageBytes hostValueMB;
        if (this.coyoteRequest.scheme().isNull()) {
            if (((AbstractHttp11Protocol)this.handler.getProtocol().getHttp11Protocol()).isSSLEnabled()) {
                this.coyoteRequest.scheme().setString("https");
            } else {
                this.coyoteRequest.scheme().setString("http");
            }
        }
        if ((hostValueMB = this.coyoteRequest.getMimeHeaders().getUniqueValue("host")) == null) {
            throw new IllegalArgumentException();
        }
        hostValueMB.toBytes();
        ByteChunk valueBC = hostValueMB.getByteChunk();
        byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        int valueS = valueBC.getStart();
        int colonPos = Host.parse(hostValueMB);
        if (colonPos != -1) {
            int port = 0;
            for (i = colonPos + 1; i < valueL; ++i) {
                char c = (char)valueB[i + valueS];
                if (c < '0' || c > '9') {
                    throw new IllegalArgumentException();
                }
                port = port * 10 + c - 48;
            }
            this.coyoteRequest.setServerPort(port);
            valueL = colonPos;
        }
        char[] hostNameC = new char[valueL];
        for (i = 0; i < valueL; ++i) {
            hostNameC[i] = (char)valueB[i + valueS];
        }
        this.coyoteRequest.serverName().setChars(hostNameC, 0, valueL);
    }

    final void receiveReset(long errorCode) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("stream.reset.receive", new Object[]{this.getConnectionId(), this.getIdAsString(), Long.toString(errorCode)}));
        }
        this.state.receivedReset();
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        this.cancelAllocationRequests();
    }

    final void cancelAllocationRequests() {
        this.allocationManager.notifyAny();
    }

    @Override
    final void incrementWindowSize(int windowSizeIncrement) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            boolean notify = this.getWindowSize() < 1L;
            super.incrementWindowSize(windowSizeIncrement);
            if (notify && this.getWindowSize() > 0L) {
                this.allocationManager.notifyStream();
            }
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final int reserveWindowSize(int reservation, boolean block) throws IOException {
        this.windowAllocationLock.lock();
        try {
            long windowSize = this.getWindowSize();
            while (windowSize < 1L) {
                if (!this.canWrite()) {
                    throw new CloseNowException(sm.getString("stream.notWritable", new Object[]{this.getConnectionId(), this.getIdAsString()}));
                }
                if (block) {
                    try {
                        long writeTimeout = this.handler.getProtocol().getStreamWriteTimeout();
                        this.allocationManager.waitForStream(writeTimeout);
                        windowSize = this.getWindowSize();
                        if (windowSize != 0L) continue;
                        this.doStreamCancel(sm.getString("stream.writeTimeout"), Http2Error.ENHANCE_YOUR_CALM);
                        continue;
                    }
                    catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                this.allocationManager.waitForStreamNonBlocking();
                int e = 0;
                return e;
            }
            int allocation = windowSize < (long)reservation ? (int)windowSize : reservation;
            this.decrementWindowSize(allocation);
            int n = allocation;
            return n;
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    void doStreamCancel(String msg, Http2Error error) throws CloseNowException {
        StreamException se = new StreamException(msg, error, this.getIdAsInt());
        this.streamOutputBuffer.closed = true;
        this.coyoteResponse.setError();
        this.coyoteResponse.setErrorReported();
        this.streamOutputBuffer.reset = se;
        throw new CloseNowException(msg, se);
    }

    void waitForConnectionAllocation(long timeout) throws InterruptedException {
        this.allocationManager.waitForConnection(timeout);
    }

    void waitForConnectionAllocationNonBlocking() {
        this.allocationManager.waitForConnectionNonBlocking();
    }

    void notifyConnection() {
        this.allocationManager.notifyConnection();
    }

    @Override
    public final void emitHeader(String name, String value) throws HpackException {
        boolean pseudoHeader;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("stream.header.debug", new Object[]{this.getConnectionId(), this.getIdAsString(), name, value}));
        }
        if (!name.toLowerCase(Locale.US).equals(name)) {
            throw new HpackException(sm.getString("stream.header.case", new Object[]{this.getConnectionId(), this.getIdAsString(), name}));
        }
        if (HTTP_CONNECTION_SPECIFIC_HEADERS.contains(name)) {
            throw new HpackException(sm.getString("stream.header.connection", new Object[]{this.getConnectionId(), this.getIdAsString(), name}));
        }
        if ("te".equals(name) && !"trailers".equals(value)) {
            throw new HpackException(sm.getString("stream.header.te", new Object[]{this.getConnectionId(), this.getIdAsString(), value}));
        }
        if (this.headerException != null) {
            return;
        }
        if (name.length() == 0) {
            throw new HpackException(sm.getString("stream.header.empty", new Object[]{this.getConnectionId(), this.getIdAsString()}));
        }
        boolean bl = pseudoHeader = name.charAt(0) == ':';
        if (pseudoHeader && this.headerState != 1) {
            this.headerException = new StreamException(sm.getString("stream.header.unexpectedPseudoHeader", new Object[]{this.getConnectionId(), this.getIdAsString(), name}), Http2Error.PROTOCOL_ERROR, this.getIdAsInt());
            return;
        }
        if (this.headerState == 1 && !pseudoHeader) {
            this.headerState = 2;
        }
        switch (name) {
            case ":method": {
                if (this.coyoteRequest.method().isNull()) {
                    this.coyoteRequest.method().setString(value);
                    break;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", new Object[]{this.getConnectionId(), this.getIdAsString(), ":method"}));
            }
            case ":scheme": {
                if (this.coyoteRequest.scheme().isNull()) {
                    this.coyoteRequest.scheme().setString(value);
                    break;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", new Object[]{this.getConnectionId(), this.getIdAsString(), ":scheme"}));
            }
            case ":path": {
                String uri;
                if (!this.coyoteRequest.requestURI().isNull()) {
                    throw new HpackException(sm.getString("stream.header.duplicate", new Object[]{this.getConnectionId(), this.getIdAsString(), ":path"}));
                }
                if (value.length() == 0) {
                    throw new HpackException(sm.getString("stream.header.noPath", new Object[]{this.getConnectionId(), this.getIdAsString()}));
                }
                int queryStart = value.indexOf(63);
                if (queryStart == -1) {
                    uri = value;
                } else {
                    uri = value.substring(0, queryStart);
                    String query = value.substring(queryStart + 1);
                    this.coyoteRequest.queryString().setString(query);
                }
                byte[] uriBytes = uri.getBytes(StandardCharsets.ISO_8859_1);
                this.coyoteRequest.requestURI().setBytes(uriBytes, 0, uriBytes.length);
                break;
            }
            case ":authority": {
                if (this.coyoteRequest.serverName().isNull()) {
                    this.parseAuthority(value, false);
                    break;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", new Object[]{this.getConnectionId(), this.getIdAsString(), ":authority"}));
            }
            case "cookie": {
                if (this.cookieHeader == null) {
                    this.cookieHeader = new StringBuilder();
                } else {
                    this.cookieHeader.append("; ");
                }
                this.cookieHeader.append(value);
                break;
            }
            case "host": {
                if (this.coyoteRequest.serverName().isNull()) {
                    this.hostHeaderSeen = true;
                    this.parseAuthority(value, true);
                    break;
                }
                if (!this.hostHeaderSeen) {
                    this.hostHeaderSeen = true;
                    this.compareAuthority(value);
                    break;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", new Object[]{this.getConnectionId(), this.getIdAsString(), "host"}));
            }
            case "priority": {
                try {
                    Priority p = Priority.parsePriority(new StringReader(value));
                    this.setUrgency(p.getUrgency());
                    this.setIncremental(p.getIncremental());
                }
                catch (IOException iOException) {}
                break;
            }
            default: {
                if (this.headerState == 3 && !this.handler.getProtocol().isTrailerHeaderAllowed(name)) break;
                if ("expect".equals(name) && "100-continue".equals(value)) {
                    this.coyoteRequest.setExpectation(true);
                }
                if (pseudoHeader) {
                    this.headerException = new StreamException(sm.getString("stream.header.unknownPseudoHeader", new Object[]{this.getConnectionId(), this.getIdAsString(), name}), Http2Error.PROTOCOL_ERROR, this.getIdAsInt());
                }
                if (this.headerState == 3) {
                    this.coyoteRequest.getTrailerFields().put(name, value);
                    break;
                }
                this.coyoteRequest.getMimeHeaders().addValue(name).setString(value);
            }
        }
    }

    void configureVoidOutputFilter() {
        this.addOutputFilter(new VoidOutputFilter());
        this.streamOutputBuffer.closed = true;
    }

    private void parseAuthority(String value, boolean host) throws HpackException {
        int i;
        try {
            i = Host.parse(value);
        }
        catch (IllegalArgumentException iae) {
            throw new HpackException(sm.getString("stream.header.invalid", new Object[]{this.getConnectionId(), this.getIdAsString(), host ? "host" : ":authority", value}));
        }
        if (i > -1) {
            this.coyoteRequest.serverName().setString(value.substring(0, i));
            this.coyoteRequest.setServerPort(Integer.parseInt(value.substring(i + 1)));
        } else {
            this.coyoteRequest.serverName().setString(value);
        }
    }

    private void compareAuthority(String value) throws HpackException {
        int i;
        try {
            i = Host.parse(value);
        }
        catch (IllegalArgumentException iae) {
            throw new HpackException(sm.getString("stream.header.invalid", new Object[]{this.getConnectionId(), this.getIdAsString(), "host", value}));
        }
        if (i == -1 && (!value.equals(this.coyoteRequest.serverName().getString()) || this.coyoteRequest.getServerPort() != -1) || i > -1 && (!value.substring(0, i).equals(this.coyoteRequest.serverName().getString()) || Integer.parseInt(value.substring(i + 1)) != this.coyoteRequest.getServerPort())) {
            throw new HpackException(sm.getString("stream.host.inconsistent", new Object[]{this.getConnectionId(), this.getIdAsString(), value, this.coyoteRequest.serverName().getString(), Integer.toString(this.coyoteRequest.getServerPort())}));
        }
    }

    @Override
    public void setHeaderException(StreamException streamException) {
        if (this.headerException == null) {
            this.headerException = streamException;
        }
    }

    @Override
    public void validateHeaders() throws StreamException {
        if (this.headerException == null) {
            return;
        }
        throw this.headerException;
    }

    final boolean receivedEndOfHeaders() throws ConnectionException {
        if (this.coyoteRequest.method().isNull() || this.coyoteRequest.scheme().isNull() || !this.coyoteRequest.method().equals("CONNECT") && this.coyoteRequest.requestURI().isNull()) {
            throw new ConnectionException(sm.getString("stream.header.required", new Object[]{this.getConnectionId(), this.getIdAsString()}), Http2Error.PROTOCOL_ERROR);
        }
        if (this.cookieHeader != null) {
            this.coyoteRequest.getMimeHeaders().addValue("cookie").setString(this.cookieHeader.toString());
        }
        return this.headerState == 2 || this.headerState == 1;
    }

    final void writeHeaders() throws IOException {
        boolean endOfStream = this.streamOutputBuffer.hasNoBody() && this.coyoteResponse.getTrailerFields() == null;
        this.handler.writeHeaders(this, 0, this.coyoteResponse.getMimeHeaders(), endOfStream, 1024);
    }

    final void addOutputFilter(OutputFilter filter) {
        this.http2OutputBuffer.addFilter(filter);
    }

    final void writeTrailers() throws IOException {
        Supplier<Map<String, String>> supplier = this.coyoteResponse.getTrailerFields();
        if (supplier == null) {
            return;
        }
        MimeHeaders mimeHeaders = this.coyoteResponse.getMimeHeaders();
        mimeHeaders.recycle();
        Map<String, String> headerMap = supplier.get();
        if (headerMap == null) {
            headerMap = Collections.emptyMap();
        }
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            MessageBytes mb = mimeHeaders.addValue(headerEntry.getKey());
            mb.setString(headerEntry.getValue());
        }
        this.handler.writeHeaders(this, 0, mimeHeaders, true, 1024);
    }

    final void writeAck() throws IOException {
        this.handler.writeHeaders(this, 0, ACK_HEADERS, false, 64);
    }

    @Override
    final String getConnectionId() {
        return this.handler.getConnectionId();
    }

    final Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    final Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    @Override
    final ByteBuffer getInputByteBuffer() {
        if (this.inputBuffer == null) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return this.inputBuffer.getInBuffer();
    }

    final void receivedStartOfHeaders(boolean headersEndStream) throws Http2Exception {
        if (this.headerState == 0) {
            this.headerState = 1;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxHeaderCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxHeaderSize());
        } else if (this.headerState == 1 || this.headerState == 2) {
            if (headersEndStream) {
                this.headerState = 3;
                this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxTrailerCount());
                this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxTrailerSize());
            } else {
                throw new ConnectionException(sm.getString("stream.trailerHeader.noEndOfStream", new Object[]{this.getConnectionId(), this.getIdAsString()}), Http2Error.PROTOCOL_ERROR);
            }
        }
        this.state.receivedStartOfHeaders();
    }

    @Override
    final void receivedData(int payloadSize) throws Http2Exception {
        this.contentLengthReceived += (long)payloadSize;
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1L && this.contentLengthReceived > contentLengthHeader) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", new Object[]{this.getConnectionId(), this.getIdAsString(), contentLengthHeader, this.contentLengthReceived}), Http2Error.PROTOCOL_ERROR);
        }
    }

    final void receivedEndOfStream() throws ConnectionException {
        if (this.isContentLengthInconsistent()) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", new Object[]{this.getConnectionId(), this.getIdAsString(), this.coyoteRequest.getContentLengthLong(), this.contentLengthReceived}), Http2Error.PROTOCOL_ERROR);
        }
        this.state.receivedEndOfStream();
        if (this.inputBuffer != null) {
            this.inputBuffer.notifyEof();
        }
    }

    final boolean isContentLengthInconsistent() {
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        return contentLengthHeader > -1L && this.contentLengthReceived != contentLengthHeader;
    }

    final void sentHeaders() {
        this.state.sentHeaders();
    }

    final void sentEndOfStream() {
        this.streamOutputBuffer.endOfStreamSent = true;
        this.state.sentEndOfStream();
    }

    final boolean isReadyForWrite() {
        return this.streamOutputBuffer.isReady();
    }

    final boolean flush(boolean block) throws IOException {
        return this.streamOutputBuffer.flush(block);
    }

    final StreamInputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    final HttpOutputBuffer getOutputBuffer() {
        return this.http2OutputBuffer;
    }

    final void sentPushPromise() {
        this.state.sentPushPromise();
    }

    final boolean isActive() {
        return this.state.isActive();
    }

    final boolean canWrite() {
        return this.state.canWrite();
    }

    final void closeIfIdle() {
        this.state.closeIfIdle();
    }

    final boolean isInputFinished() {
        return !this.state.isFrameTypePermitted(FrameType.DATA);
    }

    final void close(Http2Exception http2Exception) {
        if (http2Exception instanceof StreamException) {
            try {
                StreamException se = (StreamException)http2Exception;
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("stream.reset.send", new Object[]{this.getConnectionId(), this.getIdAsString(), se.getError()}));
                }
                this.handler.sendStreamReset(this.state, se);
                this.cancelAllocationRequests();
                if (this.inputBuffer != null) {
                    this.inputBuffer.swallowUnread();
                }
            }
            catch (IOException ioe) {
                ConnectionException ce = new ConnectionException(sm.getString("stream.reset.fail", new Object[]{this.getConnectionId(), this.getIdAsString()}), Http2Error.PROTOCOL_ERROR, ioe);
                this.handler.closeConnection(ce);
            }
        } else {
            this.handler.closeConnection(http2Exception);
        }
        this.recycle();
    }

    final void recycle() {
        ByteBuffer inputByteBuffer;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("stream.recycle", new Object[]{this.getConnectionId(), this.getIdAsString()}));
        }
        int remaining = (inputByteBuffer = this.getInputByteBuffer()) == null ? 0 : inputByteBuffer.remaining();
        this.handler.replaceStream(this, new RecycledStream(this.getConnectionId(), this.getIdentifier(), this.state, remaining));
    }

    final boolean isPushSupported() {
        return this.handler.getRemoteSettings().getEnablePush();
    }

    final void push(Request request) throws IOException {
        if (!this.isPushSupported() || this.getIdAsInt() % 2 == 0) {
            return;
        }
        request.getMimeHeaders().addValue(":method").duplicate(request.method());
        request.getMimeHeaders().addValue(":scheme").duplicate(request.scheme());
        StringBuilder path = new StringBuilder(request.requestURI().toString());
        if (!request.queryString().isNull()) {
            path.append('?');
            path.append(request.queryString().toString());
        }
        request.getMimeHeaders().addValue(":path").setString(path.toString());
        if (!(request.scheme().equals("http") && request.getServerPort() == 80 || request.scheme().equals("https") && request.getServerPort() == 443)) {
            request.getMimeHeaders().addValue(":authority").setString(request.serverName().getString() + ":" + request.getServerPort());
        } else {
            request.getMimeHeaders().addValue(":authority").duplicate(request.serverName());
        }
        Stream.push(this.handler, request, this);
    }

    boolean isTrailerFieldsReady() {
        return !this.state.canRead();
    }

    boolean isTrailerFieldsSupported() {
        return !this.streamOutputBuffer.endOfStreamSent;
    }

    StreamException getResetException() {
        return this.streamOutputBuffer.reset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getWindowUpdateSizeToWrite(int increment) {
        int result;
        int threshold = this.handler.getProtocol().getOverheadWindowUpdateThreshold();
        Object object = this.pendingWindowUpdateForStreamLock;
        synchronized (object) {
            if (increment > threshold) {
                result = increment + this.pendingWindowUpdateForStream;
                this.pendingWindowUpdateForStream = 0;
            } else {
                this.pendingWindowUpdateForStream += increment;
                if (this.pendingWindowUpdateForStream > threshold) {
                    result = this.pendingWindowUpdateForStream;
                    this.pendingWindowUpdateForStream = 0;
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }

    public int getUrgency() {
        return this.urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public boolean getIncremental() {
        return this.incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    private static void push(Http2UpgradeHandler handler, Request request, Stream stream) throws IOException {
        if (Constants.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(new PrivilegedPush(handler, request, stream));
            }
            catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(ex);
            }
        } else {
            handler.push(request, stream);
        }
    }

    static {
        HTTP_UPGRADE_STREAM = 1;
        HTTP_CONNECTION_SPECIFIC_HEADERS = new HashSet<String>();
        Response response = new Response();
        response.setStatus(100);
        StreamProcessor.prepareHeaders(null, response, true, null, null);
        ACK_HEADERS = response.getMimeHeaders();
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("proxy-connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("keep-alive");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("transfer-encoding");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("upgrade");
    }

    class StreamOutputBuffer
    implements HttpOutputBuffer,
    WriteBuffer.Sink {
        private final Lock writeLock = new ReentrantLock();
        private final ByteBuffer buffer = ByteBuffer.allocate(8192);
        private final WriteBuffer writeBuffer = new WriteBuffer(32768);
        private boolean dataLeft;
        private volatile long written = 0L;
        private int streamReservation = 0;
        private volatile boolean closed = false;
        private volatile StreamException reset = null;
        private volatile boolean endOfStreamSent = false;

        StreamOutputBuffer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final int doWrite(ByteBuffer chunk) throws IOException {
            this.writeLock.lock();
            try {
                if (this.closed) {
                    throw new IOException(sm.getString("stream.closed", new Object[]{Stream.this.getConnectionId(), Stream.this.getIdAsString()}));
                }
                int result = chunk.remaining();
                if (this.writeBuffer.isEmpty()) {
                    int chunkLimit = chunk.limit();
                    while (chunk.remaining() > 0) {
                        int thisTime = Math.min(this.buffer.remaining(), chunk.remaining());
                        chunk.limit(chunk.position() + thisTime);
                        this.buffer.put(chunk);
                        chunk.limit(chunkLimit);
                        if (chunk.remaining() <= 0 || this.buffer.hasRemaining() || !this.flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) continue;
                        this.writeBuffer.add(chunk);
                        this.dataLeft = true;
                        break;
                    }
                } else {
                    this.writeBuffer.add(chunk);
                }
                this.written += (long)result;
                int n = result;
                return n;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final boolean flush(boolean block) throws IOException {
            this.writeLock.lock();
            try {
                boolean dataInBuffer = this.buffer.position() > 0;
                boolean flushed = false;
                if (dataInBuffer) {
                    dataInBuffer = this.flush(false, block);
                    flushed = true;
                }
                this.dataLeft = dataInBuffer ? true : (this.writeBuffer.isEmpty() ? (flushed ? false : this.flush(false, block)) : this.writeBuffer.write(this, block));
                boolean bl = this.dataLeft;
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean flush(boolean writeInProgress, boolean block) throws IOException {
            this.writeLock.lock();
            try {
                boolean bl;
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("stream.outputBuffer.flush.debug", new Object[]{Stream.this.getConnectionId(), Stream.this.getIdAsString(), Integer.toString(this.buffer.position()), Boolean.toString(writeInProgress), Boolean.toString(this.closed)}));
                }
                if (this.buffer.position() == 0) {
                    if (this.closed && !this.endOfStreamSent) {
                        Stream.this.handler.writeBody(Stream.this, this.buffer, 0, Stream.this.coyoteResponse.getTrailerFields() == null);
                    }
                    boolean bl2 = false;
                    return bl2;
                }
                this.buffer.flip();
                int left = this.buffer.remaining();
                while (left > 0) {
                    if (this.streamReservation == 0) {
                        this.streamReservation = Stream.this.reserveWindowSize(left, block);
                        if (this.streamReservation == 0) {
                            this.buffer.compact();
                            bl = true;
                            return bl;
                        }
                    }
                    while (this.streamReservation > 0) {
                        int connectionReservation = Stream.this.handler.reserveWindowSize(Stream.this, this.streamReservation, block);
                        if (connectionReservation == 0) {
                            this.buffer.compact();
                            boolean bl3 = true;
                            return bl3;
                        }
                        Stream.this.handler.writeBody(Stream.this, this.buffer, connectionReservation, !writeInProgress && this.closed && left == connectionReservation && Stream.this.coyoteResponse.getTrailerFields() == null);
                        this.streamReservation -= connectionReservation;
                        left -= connectionReservation;
                    }
                }
                this.buffer.clear();
                bl = false;
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        final boolean isReady() {
            this.writeLock.lock();
            try {
                if (Stream.this.getWindowSize() > 0L && Stream.this.allocationManager.isWaitingForStream() || Stream.this.handler.getWindowSize() > 0L && Stream.this.allocationManager.isWaitingForConnection() || this.dataLeft) {
                    boolean bl = false;
                    return bl;
                }
                boolean bl = true;
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public final long getBytesWritten() {
            return this.written;
        }

        @Override
        public final void end() throws IOException {
            if (this.reset != null) {
                throw new CloseNowException(this.reset);
            }
            if (!this.closed) {
                this.closed = true;
                this.flush(true);
                Stream.this.writeTrailers();
            }
        }

        final boolean hasNoBody() {
            return this.written == 0L && this.closed;
        }

        @Override
        public void flush() throws IOException {
            this.flush(Stream.this.getCoyoteResponse().getWriteListener() == null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean writeFromBuffer(ByteBuffer src, boolean blocking) throws IOException {
            this.writeLock.lock();
            try {
                int chunkLimit = src.limit();
                while (src.remaining() > 0) {
                    int thisTime = Math.min(this.buffer.remaining(), src.remaining());
                    src.limit(src.position() + thisTime);
                    this.buffer.put(src);
                    src.limit(chunkLimit);
                    if (!this.flush(false, blocking)) continue;
                    boolean bl = true;
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }
    }

    class StandardStreamInputBuffer
    extends StreamInputBuffer {
        private final Lock readStateLock;
        private byte[] outBuffer;
        private volatile ByteBuffer inBuffer;
        private volatile boolean readInterest;
        private volatile boolean closed;
        private boolean resetReceived;

        StandardStreamInputBuffer() {
            this.readStateLock = new ReentrantLock();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException {
            this.ensureBuffersExist();
            int written = -1;
            ByteBuffer tmpInBuffer = this.inBuffer;
            if (tmpInBuffer == null) {
                return -1;
            }
            ByteBuffer byteBuffer = tmpInBuffer;
            synchronized (byteBuffer) {
                if (this.inBuffer == null) {
                    return -1;
                }
                boolean canRead = false;
                while (this.inBuffer.position() == 0 && (canRead = Stream.this.isActive() && !Stream.this.isInputFinished())) {
                    try {
                        long readTimeout;
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("stream.inputBuffer.empty"));
                        }
                        if ((readTimeout = Stream.this.handler.getProtocol().getStreamReadTimeout()) < 0L) {
                            this.inBuffer.wait();
                        } else {
                            this.inBuffer.wait(readTimeout);
                        }
                        if (this.resetReceived) {
                            throw new IOException(sm.getString("stream.inputBuffer.reset"));
                        }
                        if (this.inBuffer.position() != 0 || !Stream.this.isActive() || Stream.this.isInputFinished()) continue;
                        String msg = sm.getString("stream.inputBuffer.readTimeout");
                        StreamException se = new StreamException(msg, Http2Error.ENHANCE_YOUR_CALM, Stream.this.getIdAsInt());
                        Stream.this.coyoteResponse.setError();
                        Stream.this.streamOutputBuffer.reset = se;
                        throw new CloseNowException(msg, se);
                    }
                    catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    written = this.inBuffer.remaining();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("stream.inputBuffer.copy", new Object[]{Integer.toString(written)}));
                    }
                } else {
                    if (!canRead) {
                        return -1;
                    }
                    throw new IllegalStateException();
                }
                this.inBuffer.get(this.outBuffer, 0, written);
                this.inBuffer.clear();
            }
            applicationBufferHandler.setByteBuffer(ByteBuffer.wrap(this.outBuffer, 0, written));
            Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
            return written;
        }

        @Override
        final boolean isReadyForRead() {
            this.ensureBuffersExist();
            this.readStateLock.lock();
            try {
                if (this.available() > 0) {
                    boolean bl = true;
                    return bl;
                }
                if (!this.isRequestBodyFullyRead()) {
                    this.readInterest = true;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                this.readStateLock.unlock();
            }
        }

        @Override
        final boolean isRequestBodyFullyRead() {
            this.readStateLock.lock();
            try {
                boolean bl = (this.inBuffer == null || this.inBuffer.position() == 0) && Stream.this.isInputFinished();
                return bl;
            }
            finally {
                this.readStateLock.unlock();
            }
        }

        @Override
        public final int available() {
            this.readStateLock.lock();
            try {
                if (this.inBuffer == null) {
                    int n = 0;
                    return n;
                }
                int n = this.inBuffer.position();
                return n;
            }
            finally {
                this.readStateLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        final void onDataAvailable() throws IOException {
            block10: {
                this.readStateLock.lock();
                try {
                    if (this.closed) {
                        this.swallowUnread();
                        break block10;
                    }
                    if (this.readInterest) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("stream.inputBuffer.dispatch"));
                        }
                        this.readInterest = false;
                        Stream.this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                        Stream.this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
                        break block10;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("stream.inputBuffer.signal"));
                    }
                    ByteBuffer byteBuffer = this.inBuffer;
                    synchronized (byteBuffer) {
                        this.inBuffer.notifyAll();
                    }
                }
                finally {
                    this.readStateLock.unlock();
                }
            }
        }

        @Override
        final ByteBuffer getInBuffer() {
            this.ensureBuffersExist();
            return this.inBuffer;
        }

        @Override
        final void insertReplayedBody(ByteChunk body) {
            this.readStateLock.lock();
            try {
                this.inBuffer = ByteBuffer.wrap(body.getBytes(), body.getOffset(), body.getLength());
            }
            finally {
                this.readStateLock.unlock();
            }
        }

        private void ensureBuffersExist() {
            if (this.inBuffer == null && !this.closed) {
                int size = Stream.this.handler.getLocalSettings().getInitialWindowSize();
                this.readStateLock.lock();
                try {
                    if (this.inBuffer == null && !this.closed) {
                        this.inBuffer = ByteBuffer.allocate(size);
                        this.outBuffer = new byte[size];
                    }
                }
                finally {
                    this.readStateLock.unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        final void receiveReset() {
            if (this.inBuffer != null) {
                ByteBuffer byteBuffer = this.inBuffer;
                synchronized (byteBuffer) {
                    this.resetReceived = true;
                    this.inBuffer.notifyAll();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        final void notifyEof() {
            if (this.inBuffer != null) {
                ByteBuffer byteBuffer = this.inBuffer;
                synchronized (byteBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        final void swallowUnread() throws IOException {
            this.readStateLock.lock();
            try {
                this.closed = true;
            }
            finally {
                this.readStateLock.unlock();
            }
            if (this.inBuffer != null) {
                int unreadByteCount = 0;
                ByteBuffer byteBuffer = this.inBuffer;
                synchronized (byteBuffer) {
                    unreadByteCount = this.inBuffer.position();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("stream.inputBuffer.swallowUnread", new Object[]{unreadByteCount}));
                    }
                    if (unreadByteCount > 0) {
                        this.inBuffer.position(0);
                        this.inBuffer.limit(this.inBuffer.limit() - unreadByteCount);
                    }
                }
                if (unreadByteCount > 0) {
                    Stream.this.handler.onSwallowedDataFramePayload(Stream.this.getIdAsInt(), unreadByteCount);
                }
            }
        }
    }

    abstract class StreamInputBuffer
    implements InputBuffer {
        StreamInputBuffer() {
        }

        abstract void receiveReset();

        abstract void swallowUnread() throws IOException;

        abstract void notifyEof();

        abstract ByteBuffer getInBuffer();

        abstract void onDataAvailable() throws IOException;

        abstract boolean isReadyForRead();

        abstract boolean isRequestBodyFullyRead();

        abstract void insertReplayedBody(ByteChunk var1);
    }

    class SavedRequestStreamInputBuffer
    extends StreamInputBuffer {
        private final SavedRequestInputFilter inputFilter;

        SavedRequestStreamInputBuffer(SavedRequestInputFilter inputFilter) {
            this.inputFilter = inputFilter;
        }

        @Override
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            return this.inputFilter.doRead(handler);
        }

        @Override
        public int available() {
            return this.inputFilter.available();
        }

        @Override
        void receiveReset() {
        }

        @Override
        void swallowUnread() throws IOException {
        }

        @Override
        void notifyEof() {
        }

        @Override
        ByteBuffer getInBuffer() {
            return null;
        }

        @Override
        void onDataAvailable() throws IOException {
        }

        @Override
        boolean isReadyForRead() {
            return true;
        }

        @Override
        boolean isRequestBodyFullyRead() {
            return this.inputFilter.isFinished();
        }

        @Override
        void insertReplayedBody(ByteChunk body) {
        }
    }

    private static class PrivilegedPush
    implements PrivilegedExceptionAction<Void> {
        private final Http2UpgradeHandler handler;
        private final Request request;
        private final Stream stream;

        PrivilegedPush(Http2UpgradeHandler handler, Request request, Stream stream) {
            this.handler = handler;
            this.request = request;
            this.stream = stream;
        }

        @Override
        public Void run() throws IOException {
            this.handler.push(this.request, this.stream);
            return null;
        }
    }
}

