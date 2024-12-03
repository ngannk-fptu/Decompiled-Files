/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.WebConnection
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import javax.servlet.http.WebConnection;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http2.AbstractNonZeroStream;
import org.apache.coyote.http2.AbstractStream;
import org.apache.coyote.http2.ByteUtil;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.ConnectionSettingsLocal;
import org.apache.coyote.http2.ConnectionSettingsRemote;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.HeaderSink;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.HpackEncoder;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.Http2Parser;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.coyote.http2.SendfileData;
import org.apache.coyote.http2.Setting;
import org.apache.coyote.http2.Stream;
import org.apache.coyote.http2.StreamException;
import org.apache.coyote.http2.StreamProcessor;
import org.apache.coyote.http2.StreamRunnable;
import org.apache.coyote.http2.StreamStateMachine;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

class Http2UpgradeHandler
extends AbstractStream
implements InternalHttpUpgradeHandler,
Http2Parser.Input,
Http2Parser.Output {
    protected static final Log log = LogFactory.getLog(Http2UpgradeHandler.class);
    protected static final StringManager sm = StringManager.getManager(Http2UpgradeHandler.class);
    private static final AtomicInteger connectionIdGenerator = new AtomicInteger(0);
    private static final Integer STREAM_ID_ZERO = 0;
    protected static final int FLAG_END_OF_STREAM = 1;
    protected static final int FLAG_END_OF_HEADERS = 4;
    protected static final byte[] PING = new byte[]{0, 0, 8, 6, 0, 0, 0, 0, 0};
    protected static final byte[] PING_ACK = new byte[]{0, 0, 8, 6, 1, 0, 0, 0, 0};
    protected static final byte[] SETTINGS_ACK = new byte[]{0, 0, 0, 4, 1, 0, 0, 0, 0};
    protected static final byte[] GOAWAY = new byte[]{7, 0, 0, 0, 0, 0};
    private static final String HTTP2_SETTINGS_HEADER = "HTTP2-Settings";
    private static final HeaderSink HEADER_SINK = new HeaderSink();
    protected final String connectionId;
    protected final Http2Protocol protocol;
    private final Adapter adapter;
    protected volatile SocketWrapperBase<?> socketWrapper;
    private volatile SSLSupport sslSupport;
    private volatile Http2Parser parser;
    private AtomicReference<ConnectionState> connectionState = new AtomicReference<ConnectionState>(ConnectionState.NEW);
    private volatile long pausedNanoTime = Long.MAX_VALUE;
    private final ConnectionSettingsRemote remoteSettings;
    protected final ConnectionSettingsLocal localSettings;
    private HpackDecoder hpackDecoder;
    private HpackEncoder hpackEncoder;
    private final ConcurrentNavigableMap<Integer, AbstractNonZeroStream> streams = new ConcurrentSkipListMap<Integer, AbstractNonZeroStream>();
    protected final AtomicInteger activeRemoteStreamCount = new AtomicInteger(0);
    private volatile int maxActiveRemoteStreamId = -1;
    private volatile int maxProcessedStreamId;
    private final AtomicInteger nextLocalStreamId = new AtomicInteger(2);
    private final PingManager pingManager = this.getPingManager();
    private volatile int newStreamsSinceLastPrune = 0;
    private final Set<Stream> backLogStreams = new HashSet<Stream>();
    private long backLogSize = 0L;
    private volatile long connectionTimeout = -1L;
    private AtomicInteger streamConcurrency = null;
    private Queue<StreamRunnable> queuedRunnable = null;
    private final AtomicLong overheadCount;
    private volatile int lastNonFinalDataPayload;
    private volatile int lastWindowUpdate;
    protected final UserDataHelper userDataHelper = new UserDataHelper(log);

    Http2UpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest) {
        super(STREAM_ID_ZERO);
        this.protocol = protocol;
        this.adapter = adapter;
        this.connectionId = Integer.toString(connectionIdGenerator.getAndIncrement());
        this.overheadCount = new AtomicLong(-10 * protocol.getOverheadCountFactor());
        this.lastNonFinalDataPayload = protocol.getOverheadDataThreshold() * 2;
        this.lastWindowUpdate = protocol.getOverheadWindowUpdateThreshold() * 2;
        this.remoteSettings = new ConnectionSettingsRemote(this.connectionId);
        this.localSettings = new ConnectionSettingsLocal(this.connectionId);
        this.localSettings.set(Setting.MAX_CONCURRENT_STREAMS, protocol.getMaxConcurrentStreams());
        this.localSettings.set(Setting.INITIAL_WINDOW_SIZE, protocol.getInitialWindowSize());
        this.pingManager.initiateDisabled = protocol.getInitiatePingDisabled();
        if (coyoteRequest != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeHandler.upgrade", new Object[]{this.connectionId}));
            }
            Integer key = 1;
            Stream stream = new Stream(key, this, coyoteRequest);
            this.streams.put(key, stream);
            this.maxActiveRemoteStreamId = 1;
            this.activeRemoteStreamCount.set(1);
            this.maxProcessedStreamId = 1;
        }
    }

    protected PingManager getPingManager() {
        return new PingManager();
    }

    public void init(WebConnection webConnection) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.init", new Object[]{this.connectionId, this.connectionState.get()}));
        }
        if (!this.connectionState.compareAndSet(ConnectionState.NEW, ConnectionState.CONNECTED)) {
            return;
        }
        if ((long)this.protocol.getMaxConcurrentStreamExecution() < this.localSettings.getMaxConcurrentStreams()) {
            this.streamConcurrency = new AtomicInteger(0);
            this.queuedRunnable = new ConcurrentLinkedQueue<StreamRunnable>();
        }
        this.parser = this.getParser(this.connectionId);
        Stream stream = null;
        this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
        this.socketWrapper.setWriteTimeout(this.protocol.getWriteTimeout());
        if (webConnection != null) {
            try {
                stream = this.getStream(1, true);
                String base64Settings = stream.getCoyoteRequest().getHeader(HTTP2_SETTINGS_HEADER);
                byte[] settings = Base64.decodeBase64URLSafe((String)base64Settings);
                FrameType.SETTINGS.check(0, settings.length);
                for (int i = 0; i < settings.length % 6; ++i) {
                    int id = ByteUtil.getTwoBytes(settings, i * 6);
                    long value = ByteUtil.getFourBytes(settings, i * 6 + 2);
                    Setting key = Setting.valueOf(id);
                    if (key == Setting.UNKNOWN) {
                        log.warn((Object)sm.getString("connectionSettings.unknown", new Object[]{this.connectionId, Integer.toString(id), Long.toString(value)}));
                    }
                    this.remoteSettings.set(key, value);
                }
            }
            catch (Http2Exception e) {
                throw new ProtocolException(sm.getString("upgradeHandler.upgrade.fail", new Object[]{this.connectionId}));
            }
        }
        this.writeSettings();
        try {
            this.parser.readConnectionPreface(webConnection, stream);
        }
        catch (Http2Exception e) {
            String msg = sm.getString("upgradeHandler.invalidPreface", new Object[]{this.connectionId});
            if (log.isDebugEnabled()) {
                log.debug((Object)msg, (Throwable)e);
            }
            throw new ProtocolException(msg);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.prefaceReceived", new Object[]{this.connectionId}));
        }
        this.socketWrapper.setReadTimeout(-1L);
        this.socketWrapper.setWriteTimeout(-1L);
        this.processConnection(webConnection, stream);
    }

    protected void processConnection(WebConnection webConnection, Stream stream) {
        try {
            this.pingManager.sendPing(true);
        }
        catch (IOException ioe) {
            throw new ProtocolException(sm.getString("upgradeHandler.pingFailed", new Object[]{this.connectionId}), ioe);
        }
        if (webConnection != null) {
            this.processStreamOnContainerThread(stream);
        }
    }

    protected Http2Parser getParser(String connectionId) {
        return new Http2Parser(connectionId, this, this);
    }

    protected void processStreamOnContainerThread(Stream stream) {
        StreamProcessor streamProcessor = new StreamProcessor(this, stream, this.adapter, this.socketWrapper);
        streamProcessor.setSslSupport(this.sslSupport);
        this.processStreamOnContainerThread(streamProcessor, SocketEvent.OPEN_READ);
    }

    protected void decrementActiveRemoteStreamCount() {
        this.setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
    }

    void processStreamOnContainerThread(StreamProcessor streamProcessor, SocketEvent event) {
        StreamRunnable streamRunnable = new StreamRunnable(streamProcessor, event);
        if (this.streamConcurrency == null) {
            this.socketWrapper.execute(streamRunnable);
        } else if (this.getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution()) {
            this.increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        } else {
            this.queuedRunnable.offer(streamRunnable);
        }
    }

    @Override
    public void setSocketWrapper(SocketWrapperBase<?> wrapper) {
        this.socketWrapper = wrapper;
    }

    @Override
    public void setSslSupport(SSLSupport sslSupport) {
        this.sslSupport = sslSupport;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent status) {
        if (Http2UpgradeHandler.log.isDebugEnabled()) {
            Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.upgradeDispatch.entry", new Object[]{this.connectionId, status}));
        }
        this.init(null);
        result = AbstractEndpoint.Handler.SocketState.CLOSED;
        try {
            switch (1.$SwitchMap$org$apache$tomcat$util$net$SocketEvent[status.ordinal()]) {
                case 1: {
                    this.socketWrapper.getLock().lock();
                    try {
                        if (this.socketWrapper.canWrite()) {
                            this.pingManager.sendPing(false);
                        }
                    }
                    finally {
                        this.socketWrapper.getLock().unlock();
                    }
                    try {
                        this.setConnectionTimeout(-1L);
                        while (true) lbl-1000:
                        // 3 sources

                        {
                            try {
                                if (this.parser.readFrame()) ** GOTO lbl23
                                if (!this.isOverheadLimitExceeded()) break;
                            }
                            catch (StreamException se) {
                                try {
                                    logMode = this.userDataHelper.getNextMode();
                                    if (logMode != null) {
                                        message = Http2UpgradeHandler.sm.getString("upgradeHandler.stream.error", new Object[]{this.connectionId, Integer.toString(se.getStreamId())});
                                        switch (1.$SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[logMode.ordinal()]) {
                                            case 1: {
                                                message = message + Http2UpgradeHandler.sm.getString("upgradeHandler.fallToDebug");
                                            }
                                            case 2: {
                                                Http2UpgradeHandler.log.info((Object)message, (Throwable)se);
                                                break;
                                            }
                                            case 3: {
                                                Http2UpgradeHandler.log.debug((Object)message, (Throwable)se);
                                            }
                                        }
                                    }
                                    if ((stream = this.getStream(se.getStreamId(), false)) == null) {
                                        this.sendStreamReset(null, se);
                                        continue;
                                    }
                                    stream.close(se);
                                    if (!this.isOverheadLimitExceeded()) continue;
                                }
                                catch (Throwable var6_9) {
                                    if (this.isOverheadLimitExceeded()) {
                                        throw new ConnectionException(Http2UpgradeHandler.sm.getString("upgradeHandler.tooMuchOverhead", new Object[]{this.connectionId}), Http2Error.ENHANCE_YOUR_CALM);
                                    }
                                    throw var6_9;
                                }
                                throw new ConnectionException(Http2UpgradeHandler.sm.getString("upgradeHandler.tooMuchOverhead", new Object[]{this.connectionId}), Http2Error.ENHANCE_YOUR_CALM);
                            }
                            throw new ConnectionException(Http2UpgradeHandler.sm.getString("upgradeHandler.tooMuchOverhead", new Object[]{this.connectionId}), Http2Error.ENHANCE_YOUR_CALM);
lbl23:
                            // 1 sources

                            if (!this.isOverheadLimitExceeded()) ** GOTO lbl-1000
                            throw new ConnectionException(Http2UpgradeHandler.sm.getString("upgradeHandler.tooMuchOverhead", new Object[]{this.connectionId}), Http2Error.ENHANCE_YOUR_CALM);
                            break;
                        }
                        this.socketWrapper.setReadTimeout(-1L);
                        this.setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.get());
                    }
                    catch (Http2Exception ce) {
                        if (Http2UpgradeHandler.log.isDebugEnabled()) {
                            Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.connectionError"), (Throwable)ce);
                        }
                        this.closeConnection(ce);
                        break;
                    }
                    if (this.connectionState.get() == ConnectionState.CLOSED) break;
                    if (this.socketWrapper.hasAsyncIO()) {
                        result = AbstractEndpoint.Handler.SocketState.ASYNC_IO;
                        break;
                    }
                    result = AbstractEndpoint.Handler.SocketState.UPGRADED;
                    break;
                }
                case 2: {
                    this.processWrites();
                    if (this.socketWrapper.hasAsyncIO()) {
                        result = AbstractEndpoint.Handler.SocketState.ASYNC_IO;
                        break;
                    }
                    result = AbstractEndpoint.Handler.SocketState.UPGRADED;
                    break;
                }
                case 3: {
                    this.closeConnection(null);
                    break;
                }
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    this.close();
                }
            }
        }
        catch (IOException ioe) {
            if (Http2UpgradeHandler.log.isDebugEnabled()) {
                Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.ioerror", new Object[]{this.connectionId}), (Throwable)ioe);
            }
            this.close();
        }
        if (Http2UpgradeHandler.log.isDebugEnabled()) {
            Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.upgradeDispatch.exit", new Object[]{this.connectionId, result}));
        }
        return result;
    }

    protected void setConnectionTimeoutForStreamCount(int streamCount) {
        if (streamCount == 0) {
            long keepAliveTimeout = this.protocol.getKeepAliveTimeout();
            if (keepAliveTimeout == -1L) {
                this.setConnectionTimeout(-1L);
            } else {
                this.setConnectionTimeout(System.currentTimeMillis() + keepAliveTimeout);
            }
        } else {
            this.setConnectionTimeout(-1L);
        }
    }

    private void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public void timeoutAsync(long now) {
        long connectionTimeout = this.connectionTimeout;
        if (now == -1L || connectionTimeout > -1L && now > connectionTimeout) {
            this.socketWrapper.processSocket(SocketEvent.TIMEOUT, true);
        }
    }

    ConnectionSettingsRemote getRemoteSettings() {
        return this.remoteSettings;
    }

    ConnectionSettingsLocal getLocalSettings() {
        return this.localSettings;
    }

    Http2Protocol getProtocol() {
        return this.protocol;
    }

    @Override
    public void pause() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.pause.entry", new Object[]{this.connectionId}));
        }
        if (this.connectionState.compareAndSet(ConnectionState.CONNECTED, ConnectionState.PAUSING)) {
            this.pausedNanoTime = System.nanoTime();
            try {
                this.writeGoAwayFrame(Integer.MAX_VALUE, Http2Error.NO_ERROR.getCode(), null);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public void destroy() {
    }

    void checkPauseState() throws IOException {
        if (this.connectionState.get() == ConnectionState.PAUSING && this.pausedNanoTime + this.pingManager.getRoundTripTimeNano() < System.nanoTime()) {
            this.connectionState.compareAndSet(ConnectionState.PAUSING, ConnectionState.PAUSED);
            this.writeGoAwayFrame(this.maxProcessedStreamId, Http2Error.NO_ERROR.getCode(), null);
        }
    }

    private int increaseStreamConcurrency() {
        return this.streamConcurrency.incrementAndGet();
    }

    private int decreaseStreamConcurrency() {
        return this.streamConcurrency.decrementAndGet();
    }

    private int getStreamConcurrency() {
        return this.streamConcurrency.get();
    }

    void executeQueuedStream() {
        StreamRunnable streamRunnable;
        if (this.streamConcurrency == null) {
            return;
        }
        this.decreaseStreamConcurrency();
        if (this.getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution() && (streamRunnable = this.queuedRunnable.poll()) != null) {
            this.increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void sendStreamReset(StreamStateMachine state, StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.rst.debug", new Object[]{this.connectionId, Integer.toString(se.getStreamId()), se.getError(), se.getMessage()}));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.socketWrapper.getLock().lock();
        try {
            if (state != null) {
                boolean active = state.isActive();
                state.sendReset();
                if (active) {
                    this.decrementActiveRemoteStreamCount();
                }
            }
            this.socketWrapper.write(true, rstFrame, 0, rstFrame.length);
            this.socketWrapper.flush(true);
        }
        finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    void closeConnection(Http2Exception ce) {
        byte[] msg;
        long code;
        if (ce == null) {
            code = Http2Error.NO_ERROR.getCode();
            msg = null;
        } else {
            code = ce.getError().getCode();
            msg = ce.getMessage().getBytes(StandardCharsets.UTF_8);
        }
        try {
            this.writeGoAwayFrame(this.maxProcessedStreamId, code, msg);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.close();
    }

    protected void writeSettings() {
        try {
            byte[] settings = this.localSettings.getSettingsFrameForPending();
            this.socketWrapper.write(true, settings, 0, settings.length);
            byte[] windowUpdateFrame = this.createWindowUpdateForSettings();
            if (windowUpdateFrame.length > 0) {
                this.socketWrapper.write(true, windowUpdateFrame, 0, windowUpdateFrame.length);
            }
            this.socketWrapper.flush(true);
        }
        catch (IOException ioe) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", new Object[]{this.connectionId});
            if (log.isDebugEnabled()) {
                log.debug((Object)msg);
            }
            throw new ProtocolException(msg, ioe);
        }
    }

    protected byte[] createWindowUpdateForSettings() {
        byte[] windowUpdateFrame;
        int increment = this.protocol.getInitialWindowSize() - 65535;
        if (increment > 0) {
            windowUpdateFrame = new byte[13];
            ByteUtil.setThreeBytes(windowUpdateFrame, 0, 4);
            windowUpdateFrame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(windowUpdateFrame, 9, increment);
        } else {
            windowUpdateFrame = new byte[]{};
        }
        return windowUpdateFrame;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeGoAwayFrame(int maxStreamId, long errorCode, byte[] debugMsg) throws IOException {
        byte[] fixedPayload = new byte[8];
        ByteUtil.set31Bits(fixedPayload, 0, maxStreamId);
        ByteUtil.setFourBytes(fixedPayload, 4, errorCode);
        int len = 8;
        if (debugMsg != null) {
            len += debugMsg.length;
        }
        byte[] payloadLength = new byte[3];
        ByteUtil.setThreeBytes(payloadLength, 0, len);
        this.socketWrapper.getLock().lock();
        try {
            this.socketWrapper.write(true, payloadLength, 0, payloadLength.length);
            this.socketWrapper.write(true, GOAWAY, 0, GOAWAY.length);
            this.socketWrapper.write(true, fixedPayload, 0, 8);
            if (debugMsg != null) {
                this.socketWrapper.write(true, debugMsg, 0, debugMsg.length);
            }
            this.socketWrapper.flush(true);
        }
        finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        this.socketWrapper.getLock().lock();
        try {
            this.doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
        }
        finally {
            this.socketWrapper.getLock().unlock();
        }
        stream.sentHeaders();
        if (endOfStream) {
            this.sentEndOfStream(stream);
        }
    }

    protected HeaderFrameBuffers doWriteHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        if (log.isDebugEnabled()) {
            if (pushedStreamId == 0) {
                log.debug((Object)sm.getString("upgradeHandler.writeHeaders", new Object[]{this.connectionId, stream.getIdAsString(), endOfStream}));
            } else {
                log.debug((Object)sm.getString("upgradeHandler.writePushHeaders", new Object[]{this.connectionId, stream.getIdAsString(), pushedStreamId, endOfStream}));
            }
        }
        if (!stream.canWrite()) {
            return null;
        }
        HeaderFrameBuffers headerFrameBuffers = this.getHeaderFrameBuffers(payloadSize);
        byte[] pushedStreamIdBytes = null;
        if (pushedStreamId > 0) {
            pushedStreamIdBytes = new byte[4];
            ByteUtil.set31Bits(pushedStreamIdBytes, 0, pushedStreamId);
        }
        boolean first = true;
        HpackEncoder.State state = null;
        while (state != HpackEncoder.State.COMPLETE) {
            headerFrameBuffers.startFrame();
            if (first && pushedStreamIdBytes != null) {
                headerFrameBuffers.getPayload().put(pushedStreamIdBytes);
            }
            state = this.getHpackEncoder().encode(mimeHeaders, headerFrameBuffers.getPayload());
            headerFrameBuffers.getPayload().flip();
            if (state == HpackEncoder.State.COMPLETE || headerFrameBuffers.getPayload().limit() > 0) {
                ByteUtil.setThreeBytes(headerFrameBuffers.getHeader(), 0, headerFrameBuffers.getPayload().limit());
                if (first) {
                    first = false;
                    headerFrameBuffers.getHeader()[3] = pushedStreamIdBytes == null ? FrameType.HEADERS.getIdByte() : FrameType.PUSH_PROMISE.getIdByte();
                    if (endOfStream) {
                        headerFrameBuffers.getHeader()[4] = 1;
                    }
                } else {
                    headerFrameBuffers.getHeader()[3] = FrameType.CONTINUATION.getIdByte();
                }
                if (state == HpackEncoder.State.COMPLETE) {
                    byte[] byArray = headerFrameBuffers.getHeader();
                    byArray[4] = (byte)(byArray[4] + 4);
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)(headerFrameBuffers.getPayload().limit() + " bytes"));
                }
                ByteUtil.set31Bits(headerFrameBuffers.getHeader(), 5, stream.getIdAsInt());
                headerFrameBuffers.endFrame();
                continue;
            }
            if (state != HpackEncoder.State.UNDERFLOW) continue;
            headerFrameBuffers.expandPayload();
        }
        headerFrameBuffers.endHeaders();
        return headerFrameBuffers;
    }

    protected HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new DefaultHeaderFrameBuffers(initialPayloadSize);
    }

    protected HpackEncoder getHpackEncoder() {
        if (this.hpackEncoder == null) {
            this.hpackEncoder = new HpackEncoder();
        }
        this.hpackEncoder.setMaxTableSize(this.remoteSettings.getHeaderTableSize());
        return this.hpackEncoder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeBody(Stream stream, ByteBuffer data, int len, boolean finished) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.writeBody", new Object[]{this.connectionId, stream.getIdAsString(), Integer.toString(len), finished}));
        }
        this.reduceOverheadCount(FrameType.DATA);
        boolean writable = stream.canWrite();
        byte[] header = new byte[9];
        ByteUtil.setThreeBytes(header, 0, len);
        header[3] = FrameType.DATA.getIdByte();
        if (finished) {
            header[4] = 1;
            this.sentEndOfStream(stream);
        }
        if (writable) {
            ByteUtil.set31Bits(header, 5, stream.getIdAsInt());
            this.socketWrapper.getLock().lock();
            try {
                this.socketWrapper.write(true, header, 0, header.length);
                int orgLimit = data.limit();
                data.limit(data.position() + len);
                this.socketWrapper.write(true, data);
                data.limit(orgLimit);
                this.socketWrapper.flush(true);
            }
            catch (IOException ioe) {
                this.handleAppInitiatedIOException(ioe);
            }
            finally {
                this.socketWrapper.getLock().unlock();
            }
        }
    }

    protected void sentEndOfStream(Stream stream) {
        stream.sentEndOfStream();
        if (!stream.isActive()) {
            this.decrementActiveRemoteStreamCount();
        }
    }

    protected void handleAppInitiatedIOException(IOException ioe) throws IOException {
        this.close();
        throw ioe;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeWindowUpdate(AbstractNonZeroStream stream, int increment, boolean applicationInitiated) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.windowUpdateConnection", new Object[]{this.getConnectionId(), increment}));
        }
        this.socketWrapper.getLock().lock();
        try {
            int streamIncrement;
            byte[] frame = new byte[13];
            ByteUtil.setThreeBytes(frame, 0, 4);
            frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(frame, 9, increment);
            this.socketWrapper.write(true, frame, 0, frame.length);
            boolean needFlush = true;
            if (stream instanceof Stream && ((Stream)stream).canWrite() && (streamIncrement = ((Stream)stream).getWindowUpdateSizeToWrite(increment)) > 0) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("upgradeHandler.windowUpdateStream", new Object[]{this.getConnectionId(), this.getIdAsString(), streamIncrement}));
                }
                ByteUtil.set31Bits(frame, 5, stream.getIdAsInt());
                ByteUtil.set31Bits(frame, 9, streamIncrement);
                try {
                    this.socketWrapper.write(true, frame, 0, frame.length);
                    this.socketWrapper.flush(true);
                    needFlush = false;
                }
                catch (IOException ioe) {
                    if (applicationInitiated) {
                        this.handleAppInitiatedIOException(ioe);
                    }
                    throw ioe;
                }
            }
            if (needFlush) {
                this.socketWrapper.flush(true);
            }
        }
        finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    protected void processWrites() throws IOException {
        this.socketWrapper.getLock().lock();
        try {
            if (this.socketWrapper.flush(false)) {
                this.socketWrapper.registerWriteInterest();
            } else {
                this.pingManager.sendPing(false);
            }
        }
        finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int reserveWindowSize(Stream stream, int reservation, boolean block) throws IOException {
        int allocation;
        block23: {
            allocation = 0;
            stream.windowAllocationLock.lock();
            try {
                this.windowAllocationLock.lock();
                try {
                    if (!stream.canWrite()) {
                        stream.doStreamCancel(sm.getString("upgradeHandler.stream.notWritable", new Object[]{stream.getConnectionId(), stream.getIdAsString(), stream.state.getCurrentStateName()}), Http2Error.STREAM_CLOSED);
                    }
                    long windowSize = this.getWindowSize();
                    if (stream.getConnectionAllocationMade() > 0) {
                        allocation = stream.getConnectionAllocationMade();
                        stream.setConnectionAllocationMade(0);
                    } else if (windowSize < 1L) {
                        if (stream.getConnectionAllocationMade() == 0) {
                            stream.setConnectionAllocationRequested(reservation);
                            this.backLogSize += (long)reservation;
                            this.backLogStreams.add(stream);
                        }
                    } else if (windowSize < (long)reservation) {
                        allocation = (int)windowSize;
                        this.decrementWindowSize(allocation);
                    } else {
                        allocation = reservation;
                        this.decrementWindowSize(allocation);
                    }
                }
                finally {
                    this.windowAllocationLock.unlock();
                }
                if (allocation != 0) break block23;
                if (block) {
                    try {
                        long writeTimeout = this.protocol.getWriteTimeout();
                        stream.waitForConnectionAllocation(writeTimeout);
                        if (stream.getConnectionAllocationMade() == 0) {
                            Http2Error error;
                            String msg;
                            if (stream.isActive()) {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)sm.getString("upgradeHandler.noAllocation", new Object[]{this.connectionId, stream.getIdAsString()}));
                                }
                                this.close();
                                msg = sm.getString("stream.writeTimeout");
                                error = Http2Error.ENHANCE_YOUR_CALM;
                            } else {
                                msg = sm.getString("stream.clientCancel");
                                error = Http2Error.STREAM_CLOSED;
                            }
                            stream.doStreamCancel(msg, error);
                        } else {
                            allocation = stream.getConnectionAllocationMade();
                            stream.setConnectionAllocationMade(0);
                        }
                        break block23;
                    }
                    catch (InterruptedException e) {
                        throw new IOException(sm.getString("upgradeHandler.windowSizeReservationInterrupted", new Object[]{this.connectionId, stream.getIdAsString(), Integer.toString(reservation)}), e);
                    }
                }
                stream.waitForConnectionAllocationNonBlocking();
                int n = 0;
                return n;
            }
            finally {
                stream.windowAllocationLock.unlock();
            }
        }
        return allocation;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void incrementWindowSize(int increment) throws Http2Exception {
        Set<AbstractStream> streamsToNotify = null;
        this.windowAllocationLock.lock();
        try {
            long windowSize = this.getWindowSize();
            if (windowSize < 1L && windowSize + (long)increment > 0L) {
                streamsToNotify = this.releaseBackLog((int)(windowSize + (long)increment));
            } else {
                super.incrementWindowSize(increment);
            }
        }
        finally {
            this.windowAllocationLock.unlock();
        }
        if (streamsToNotify != null) {
            for (AbstractStream stream : streamsToNotify) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("upgradeHandler.releaseBacklog", new Object[]{this.connectionId, stream.getIdAsString()}));
                }
                if (this == stream) continue;
                ((Stream)stream).notifyConnection();
            }
        }
    }

    protected SendfileState processSendfile(SendfileData sendfileData) {
        return SendfileState.DONE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<AbstractStream> releaseBackLog(int increment) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            HashSet<AbstractStream> result = new HashSet<AbstractStream>();
            if (this.backLogSize < (long)increment) {
                for (AbstractStream abstractStream : this.backLogStreams) {
                    if (abstractStream.getConnectionAllocationRequested() <= 0) continue;
                    abstractStream.setConnectionAllocationMade(abstractStream.getConnectionAllocationRequested());
                    abstractStream.setConnectionAllocationRequested(0);
                    result.add(abstractStream);
                }
                int remaining = increment - (int)this.backLogSize;
                this.backLogSize = 0L;
                super.incrementWindowSize(remaining);
                this.backLogStreams.clear();
            } else {
                ConcurrentSkipListSet<Stream> orderedStreams = new ConcurrentSkipListSet<Stream>(Comparator.comparingInt(Stream::getUrgency).thenComparing(Stream::getIncremental).thenComparing(AbstractStream::getIdAsInt));
                orderedStreams.addAll(this.backLogStreams);
                long l = 0L;
                long requestedAllocationForIncrementalStreams = 0L;
                int remaining = increment;
                for (Stream s : orderedStreams) {
                    if (l < (long)s.getUrgency()) {
                        if (remaining < 1) break;
                        requestedAllocationForIncrementalStreams = 0L;
                    }
                    l = s.getUrgency();
                    if (s.getIncremental()) {
                        requestedAllocationForIncrementalStreams += (long)s.getConnectionAllocationRequested();
                        remaining -= s.getConnectionAllocationRequested();
                        continue;
                    }
                    if ((remaining -= s.getConnectionAllocationRequested()) >= 1) continue;
                    break;
                }
                remaining = increment;
                Iterator orderedStreamsIterator = orderedStreams.iterator();
                while (orderedStreamsIterator.hasNext()) {
                    Stream s;
                    s = (Stream)orderedStreamsIterator.next();
                    if ((long)s.getUrgency() < l) {
                        remaining = this.allocate(s, remaining);
                        result.add(s);
                        orderedStreamsIterator.remove();
                        this.backLogStreams.remove(s);
                        continue;
                    }
                    if (requestedAllocationForIncrementalStreams == 0L) {
                        remaining = this.allocate(s, remaining);
                        result.add(s);
                        if (s.getConnectionAllocationRequested() == 0) {
                            orderedStreamsIterator.remove();
                            this.backLogStreams.remove(s);
                        }
                        if (remaining >= 1) continue;
                        break;
                    }
                    if ((long)s.getUrgency() != l) break;
                    int share = (int)((long)(s.getConnectionAllocationRequested() * remaining) / requestedAllocationForIncrementalStreams);
                    if (share == 0) {
                        share = 1;
                    }
                    this.allocate(s, share);
                    result.add(s);
                    if (s.getConnectionAllocationRequested() != 0) continue;
                    orderedStreamsIterator.remove();
                    this.backLogStreams.remove(s);
                }
            }
            HashSet<AbstractStream> hashSet = result;
            return hashSet;
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int allocate(AbstractStream stream, int allocation) {
        this.windowAllocationLock.lock();
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeHandler.allocate.debug", new Object[]{this.getConnectionId(), stream.getIdAsString(), Integer.toString(allocation)}));
            }
            int leftToAllocate = allocation;
            if (stream.getConnectionAllocationRequested() > 0) {
                int allocatedThisTime = allocation >= stream.getConnectionAllocationRequested() ? stream.getConnectionAllocationRequested() : allocation;
                stream.setConnectionAllocationRequested(stream.getConnectionAllocationRequested() - allocatedThisTime);
                stream.setConnectionAllocationMade(stream.getConnectionAllocationMade() + allocatedThisTime);
                leftToAllocate -= allocatedThisTime;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeHandler.allocate.left", new Object[]{this.getConnectionId(), stream.getIdAsString(), Integer.toString(leftToAllocate)}));
            }
            int n = leftToAllocate;
            return n;
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    private Stream getStream(int streamId) {
        Integer key = streamId;
        AbstractStream result = (AbstractStream)this.streams.get(key);
        if (result instanceof Stream) {
            return (Stream)result;
        }
        return null;
    }

    private Stream getStream(int streamId, boolean unknownIsError) throws ConnectionException {
        Stream result = this.getStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", new Object[]{Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId) {
        Integer key = streamId;
        return (AbstractNonZeroStream)this.streams.get(key);
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId, boolean unknownIsError) throws ConnectionException {
        AbstractNonZeroStream result = this.getAbstractNonZeroStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", new Object[]{Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private Stream createRemoteStream(int streamId) throws ConnectionException {
        Integer key = streamId;
        if (streamId % 2 != 1) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.even", new Object[]{key}), Http2Error.PROTOCOL_ERROR);
        }
        this.pruneClosedStreams(streamId);
        Stream result = new Stream(key, this);
        this.streams.put(key, result);
        return result;
    }

    private Stream createLocalStream(Request request) {
        int streamId = this.nextLocalStreamId.getAndAdd(2);
        Integer key = streamId;
        Stream result = new Stream(key, this, request);
        this.streams.put(key, result);
        return result;
    }

    private void close() {
        ConnectionState previous = this.connectionState.getAndSet(ConnectionState.CLOSED);
        if (previous == ConnectionState.CLOSED) {
            return;
        }
        for (AbstractNonZeroStream stream : this.streams.values()) {
            if (!(stream instanceof Stream)) continue;
            ((Stream)stream).receiveReset(Http2Error.CANCEL.getCode());
        }
        try {
            this.socketWrapper.close();
        }
        catch (Exception e) {
            log.debug((Object)sm.getString("upgradeHandler.socketCloseFailed"), (Throwable)e);
        }
    }

    private void pruneClosedStreams(int streamId) {
        if (this.newStreamsSinceLastPrune < 9) {
            ++this.newStreamsSinceLastPrune;
            return;
        }
        this.newStreamsSinceLastPrune = 0;
        long max = this.localSettings.getMaxConcurrentStreams();
        if ((max *= 5L) > Integer.MAX_VALUE) {
            max = Integer.MAX_VALUE;
        }
        int size = this.streams.size();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.pruneStart", new Object[]{this.connectionId, Long.toString(max), Integer.toString(size)}));
        }
        int toClose = size - (int)max;
        for (AbstractNonZeroStream stream : this.streams.values()) {
            if (toClose < 1) {
                return;
            }
            if (stream instanceof Stream && ((Stream)stream).isActive()) continue;
            this.streams.remove(stream.getIdentifier());
            --toClose;
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)sm.getString("upgradeHandler.pruned", new Object[]{this.connectionId, stream.getIdAsString()}));
        }
        if (toClose > 0) {
            log.warn((Object)sm.getString("upgradeHandler.pruneIncomplete", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(toClose)}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void push(Request request, Stream associatedStream) throws IOException {
        Stream pushStream;
        if (this.localSettings.getMaxConcurrentStreams() < (long)this.activeRemoteStreamCount.incrementAndGet()) {
            this.setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
            return;
        }
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            pushStream = this.createLocalStream(request);
            this.writeHeaders(associatedStream, pushStream.getIdAsInt(), request.getMimeHeaders(), false, 1024);
        }
        finally {
            lock.unlock();
        }
        pushStream.sentPushPromise();
        this.processStreamOnContainerThread(pushStream);
    }

    @Override
    protected final String getConnectionId() {
        return this.connectionId;
    }

    void reduceOverheadCount(FrameType frameType) {
        this.updateOverheadCount(frameType, -20);
    }

    @Override
    public void increaseOverheadCount(FrameType frameType) {
        this.updateOverheadCount(frameType, this.getProtocol().getOverheadCountFactor());
    }

    private void increaseOverheadCount(FrameType frameType, int increment) {
        this.updateOverheadCount(frameType, increment);
    }

    private void updateOverheadCount(FrameType frameType, int increment) {
        long newOverheadCount = this.overheadCount.addAndGet(increment);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.overheadChange", new Object[]{this.connectionId, this.getIdAsString(), frameType.name(), newOverheadCount}));
        }
    }

    boolean isOverheadLimitExceeded() {
        return this.overheadCount.get() > 0L;
    }

    @Override
    public boolean fill(boolean block, byte[] data, int offset, int length) throws IOException {
        int pos = offset;
        boolean nextReadBlock = block;
        int thisRead = 0;
        for (int len = length; len > 0; len -= thisRead) {
            if (nextReadBlock) {
                this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
            } else {
                this.socketWrapper.setReadTimeout(-1L);
            }
            thisRead = this.socketWrapper.read(nextReadBlock, data, pos, len);
            if (thisRead == 0) {
                if (nextReadBlock) {
                    throw new IllegalStateException();
                }
                return false;
            }
            if (thisRead == -1) {
                if (this.connectionState.get().isNewStreamAllowed()) {
                    throw new EOFException();
                }
                return false;
            }
            pos += thisRead;
            nextReadBlock = true;
        }
        return true;
    }

    @Override
    public int getMaxFrameSize() {
        return this.localSettings.getMaxFrameSize();
    }

    @Override
    public HpackDecoder getHpackDecoder() {
        if (this.hpackDecoder == null) {
            this.hpackDecoder = new HpackDecoder(this.localSettings.getHeaderTableSize());
        }
        return this.hpackDecoder;
    }

    @Override
    public ByteBuffer startRequestBodyFrame(int streamId, int payloadSize, boolean endOfStream) throws Http2Exception {
        this.reduceOverheadCount(FrameType.DATA);
        if (!endOfStream) {
            int overheadThreshold = this.protocol.getOverheadDataThreshold();
            int average = (this.lastNonFinalDataPayload >> 1) + (payloadSize >> 1);
            this.lastNonFinalDataPayload = payloadSize;
            if (average == 0) {
                average = 1;
            }
            if (average < overheadThreshold) {
                this.increaseOverheadCount(FrameType.DATA, overheadThreshold / average);
            }
        }
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.DATA);
        abstractNonZeroStream.receivedData(payloadSize);
        ByteBuffer result = abstractNonZeroStream.getInputByteBuffer();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.startRequestBodyFrame.result", new Object[]{this.getConnectionId(), abstractNonZeroStream.getIdAsString(), result}));
        }
        return result;
    }

    @Override
    public void endRequestBodyFrame(int streamId, int dataLength) throws Http2Exception, IOException {
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId, true);
        if (abstractNonZeroStream instanceof Stream) {
            ((Stream)abstractNonZeroStream).getInputBuffer().onDataAvailable();
        } else if (dataLength > 0) {
            this.onSwallowedDataFramePayload(streamId, dataLength);
        }
    }

    @Override
    public void onSwallowedDataFramePayload(int streamId, int swallowedDataBytesCount) throws IOException {
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId);
        this.writeWindowUpdate(abstractNonZeroStream, swallowedDataBytesCount, false);
    }

    @Override
    public HpackDecoder.HeaderEmitter headersStart(int streamId, boolean headersEndStream) throws Http2Exception, IOException {
        this.checkPauseState();
        if (this.connectionState.get().isNewStreamAllowed()) {
            Stream stream = this.getStream(streamId, false);
            if (stream == null) {
                stream = this.createRemoteStream(streamId);
            }
            if (streamId < this.maxActiveRemoteStreamId) {
                throw new ConnectionException(sm.getString("upgradeHandler.stream.old", new Object[]{streamId, this.maxActiveRemoteStreamId}), Http2Error.PROTOCOL_ERROR);
            }
            stream.checkState(FrameType.HEADERS);
            stream.receivedStartOfHeaders(headersEndStream);
            this.closeIdleStreams(streamId);
            return stream;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.noNewStreams", new Object[]{this.connectionId, Integer.toString(streamId)}));
        }
        this.reduceOverheadCount(FrameType.HEADERS);
        return HEADER_SINK;
    }

    private void closeIdleStreams(int newMaxActiveRemoteStreamId) {
        NavigableMap subMap = this.streams.subMap((Object)this.maxActiveRemoteStreamId, false, (Object)newMaxActiveRemoteStreamId, false);
        for (AbstractNonZeroStream stream : subMap.values()) {
            if (!(stream instanceof Stream)) continue;
            ((Stream)stream).closeIfIdle();
        }
        this.maxActiveRemoteStreamId = newMaxActiveRemoteStreamId;
    }

    @Deprecated
    public void reprioritise(int streamId, int parentStreamId, boolean exclusive, int weight) throws Http2Exception {
    }

    @Override
    public void headersContinue(int payloadSize, boolean endOfHeaders) {
        int overheadThreshold;
        if (!endOfHeaders && payloadSize < (overheadThreshold = this.getProtocol().getOverheadContinuationThreshold())) {
            if (payloadSize == 0) {
                this.increaseOverheadCount(FrameType.HEADERS, overheadThreshold);
            } else {
                this.increaseOverheadCount(FrameType.HEADERS, overheadThreshold / payloadSize);
            }
        }
    }

    @Override
    public void headersEnd(int streamId, boolean endOfStream) throws Http2Exception {
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            boolean processStream = false;
            this.setMaxProcessedStream(streamId);
            Stream stream = (Stream)abstractNonZeroStream;
            if (stream.isActive() && stream.receivedEndOfHeaders()) {
                if (this.localSettings.getMaxConcurrentStreams() < (long)this.activeRemoteStreamCount.incrementAndGet()) {
                    this.decrementActiveRemoteStreamCount();
                    this.increaseOverheadCount(FrameType.HEADERS);
                    throw new StreamException(sm.getString("upgradeHandler.tooManyRemoteStreams", new Object[]{Long.toString(this.localSettings.getMaxConcurrentStreams())}), Http2Error.REFUSED_STREAM, streamId);
                }
                this.reduceOverheadCount(FrameType.HEADERS);
                processStream = true;
            }
            if (endOfStream) {
                this.receivedEndOfStream(stream);
            }
            if (processStream) {
                this.processStreamOnContainerThread(stream);
            }
        }
    }

    @Override
    public void receivedEndOfStream(int streamId) throws ConnectionException {
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream)abstractNonZeroStream;
            this.receivedEndOfStream(stream);
        }
    }

    private void receivedEndOfStream(Stream stream) throws ConnectionException {
        stream.receivedEndOfStream();
        if (!stream.isActive()) {
            this.decrementActiveRemoteStreamCount();
        }
    }

    private void setMaxProcessedStream(int streamId) {
        if (this.maxProcessedStreamId < streamId) {
            this.maxProcessedStreamId = streamId;
        }
    }

    @Override
    public void reset(int streamId, long errorCode) throws Http2Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.reset.receive", new Object[]{this.getConnectionId(), Integer.toString(streamId), Long.toString(errorCode)}));
        }
        this.increaseOverheadCount(FrameType.RST, this.getProtocol().getOverheadResetFactor());
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.RST);
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream)abstractNonZeroStream;
            boolean active = stream.isActive();
            stream.receiveReset(errorCode);
            if (active) {
                this.decrementActiveRemoteStreamCount();
            }
        }
    }

    @Override
    public void setting(Setting setting, long value) throws ConnectionException {
        this.increaseOverheadCount(FrameType.SETTINGS);
        if (setting == null) {
            return;
        }
        if (setting == Setting.INITIAL_WINDOW_SIZE) {
            long oldValue = this.remoteSettings.getInitialWindowSize();
            this.remoteSettings.set(setting, value);
            int diff = (int)(value - oldValue);
            for (AbstractNonZeroStream stream : this.streams.values()) {
                try {
                    stream.incrementWindowSize(diff);
                }
                catch (Http2Exception h2e) {
                    ((Stream)stream).close(new StreamException(sm.getString("upgradeHandler.windowSizeTooBig", new Object[]{this.connectionId, stream.getIdAsString()}), h2e.getError(), stream.getIdAsInt()));
                }
            }
        } else if (setting == Setting.NO_RFC7540_PRIORITIES) {
            if (value != 1L) {
                throw new ConnectionException(sm.getString("upgradeHandler.enableRfc7450Priorities", new Object[]{this.connectionId}), Http2Error.PROTOCOL_ERROR);
            }
        } else {
            this.remoteSettings.set(setting, value);
        }
    }

    @Override
    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn((Object)sm.getString("upgradeHandler.unexpectedAck", new Object[]{this.connectionId, this.getIdAsString()}));
            }
        } else {
            this.socketWrapper.getLock().lock();
            try {
                this.socketWrapper.write(true, SETTINGS_ACK, 0, SETTINGS_ACK.length);
                this.socketWrapper.flush(true);
            }
            finally {
                this.socketWrapper.getLock().unlock();
            }
        }
    }

    @Override
    public void pingReceive(byte[] payload, boolean ack) throws IOException {
        if (!ack) {
            this.increaseOverheadCount(FrameType.PING);
        }
        this.pingManager.receivePing(payload, ack);
    }

    @Override
    public void goaway(int lastStreamId, long errorCode, String debugData) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.goaway.debug", new Object[]{this.connectionId, Integer.toString(lastStreamId), Long.toHexString(errorCode), debugData}));
        }
        this.close();
    }

    @Override
    public void incrementWindowSize(int streamId, int increment) throws Http2Exception {
        int average = (this.lastWindowUpdate >> 1) + (increment >> 1);
        int overheadThreshold = this.protocol.getOverheadWindowUpdateThreshold();
        this.lastWindowUpdate = increment;
        if (average == 0) {
            average = 1;
        }
        if (streamId == 0) {
            if (average < overheadThreshold) {
                this.increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            this.incrementWindowSize(increment);
        } else {
            AbstractNonZeroStream stream = this.getAbstractNonZeroStream(streamId, true);
            if (average < overheadThreshold && increment < stream.getConnectionAllocationRequested()) {
                this.increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            stream.checkState(FrameType.WINDOW_UPDATE);
            stream.incrementWindowSize(increment);
        }
    }

    @Override
    public void priorityUpdate(int prioritizedStreamID, Priority p) throws Http2Exception {
        this.increaseOverheadCount(FrameType.PRIORITY_UPDATE);
        AbstractNonZeroStream abstractNonZeroStream = this.getAbstractNonZeroStream(prioritizedStreamID, true);
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream)abstractNonZeroStream;
            stream.setUrgency(p.getUrgency());
            stream.setIncremental(p.getIncremental());
        }
    }

    @Override
    public void onSwallowedUnknownFrame(int streamId, int frameTypeId, int flags, int size) throws IOException {
    }

    void replaceStream(AbstractNonZeroStream original, AbstractNonZeroStream replacement) {
        AbstractNonZeroStream current = (AbstractNonZeroStream)this.streams.get(original.getIdentifier());
        if (current instanceof Stream) {
            this.streams.put(original.getIdentifier(), replacement);
        }
    }

    private static enum ConnectionState {
        NEW(true),
        CONNECTED(true),
        PAUSING(true),
        PAUSED(false),
        CLOSED(false);

        private final boolean newStreamsAllowed;

        private ConnectionState(boolean newStreamsAllowed) {
            this.newStreamsAllowed = newStreamsAllowed;
        }

        public boolean isNewStreamAllowed() {
            return this.newStreamsAllowed;
        }
    }

    protected class PingManager {
        protected boolean initiateDisabled = false;
        protected final long pingIntervalNano = 10000000000L;
        protected int sequence = 0;
        protected long lastPingNanoTime = Long.MIN_VALUE;
        protected Queue<PingRecord> inflightPings = new ConcurrentLinkedQueue<PingRecord>();
        protected Queue<Long> roundTripTimes = new ConcurrentLinkedQueue<Long>();

        protected PingManager() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                Http2UpgradeHandler.this.socketWrapper.getLock().lock();
                try {
                    int sentSequence = ++this.sequence;
                    PingRecord pingRecord = new PingRecord(sentSequence, now);
                    this.inflightPings.add(pingRecord);
                    ByteUtil.set31Bits(payload, 4, sentSequence);
                    Http2UpgradeHandler.this.socketWrapper.write(true, PING, 0, PING.length);
                    Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                    Http2UpgradeHandler.this.socketWrapper.flush(true);
                }
                finally {
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void receivePing(byte[] payload, boolean ack) throws IOException {
            if (ack) {
                int receivedSequence = ByteUtil.get31Bits(payload, 4);
                PingRecord pingRecord = this.inflightPings.poll();
                while (pingRecord != null && pingRecord.getSequence() < receivedSequence) {
                    pingRecord = this.inflightPings.poll();
                }
                if (pingRecord != null) {
                    long roundTripTime = System.nanoTime() - pingRecord.getSentNanoTime();
                    this.roundTripTimes.add(roundTripTime);
                    while (this.roundTripTimes.size() > 3) {
                        this.roundTripTimes.poll();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("pingManager.roundTripTime", new Object[]{Http2UpgradeHandler.this.connectionId, roundTripTime}));
                    }
                }
            } else {
                Http2UpgradeHandler.this.socketWrapper.getLock().lock();
                try {
                    Http2UpgradeHandler.this.socketWrapper.write(true, PING_ACK, 0, PING_ACK.length);
                    Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                    Http2UpgradeHandler.this.socketWrapper.flush(true);
                }
                finally {
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                }
            }
        }

        public long getRoundTripTimeNano() {
            return (long)this.roundTripTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        }
    }

    protected static interface HeaderFrameBuffers {
        public void startFrame();

        public void endFrame() throws IOException;

        public void endHeaders() throws IOException;

        public byte[] getHeader();

        public ByteBuffer getPayload();

        public void expandPayload();
    }

    private class DefaultHeaderFrameBuffers
    implements HeaderFrameBuffers {
        private final byte[] header = new byte[9];
        private ByteBuffer payload;

        DefaultHeaderFrameBuffers(int initialPayloadSize) {
            this.payload = ByteBuffer.allocate(initialPayloadSize);
        }

        @Override
        public void startFrame() {
        }

        @Override
        public void endFrame() throws IOException {
            try {
                Http2UpgradeHandler.this.socketWrapper.write(true, this.header, 0, this.header.length);
                Http2UpgradeHandler.this.socketWrapper.write(true, this.payload);
                Http2UpgradeHandler.this.socketWrapper.flush(true);
            }
            catch (IOException ioe) {
                Http2UpgradeHandler.this.handleAppInitiatedIOException(ioe);
            }
            this.payload.clear();
        }

        @Override
        public void endHeaders() {
        }

        @Override
        public byte[] getHeader() {
            return this.header;
        }

        @Override
        public ByteBuffer getPayload() {
            return this.payload;
        }

        @Override
        public void expandPayload() {
            this.payload = ByteBuffer.allocate(this.payload.capacity() * 2);
        }
    }

    protected static class PingRecord {
        private final int sequence;
        private final long sentNanoTime;

        public PingRecord(int sequence, long sentNanoTime) {
            this.sequence = sequence;
            this.sentNanoTime = sentNanoTime;
        }

        public int getSequence() {
            return this.sequence;
        }

        public long getSentNanoTime() {
            return this.sentNanoTime;
        }
    }
}

