/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.CancellableDependency
 *  org.apache.hc.core5.http.ConnectionClosedException
 *  org.apache.hc.core5.http.EndpointDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpConnection
 *  org.apache.hc.core5.http.HttpConnectionMetrics
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpStreamResetException
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.RequestNotExecutedException
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.impl.BasicEndpointDetails
 *  org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics
 *  org.apache.hc.core5.http.impl.CharCodingSupport
 *  org.apache.hc.core5.http.io.HttpTransportMetrics
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncPushProducer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.command.ExecutableCommand
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.ByteArrayBuffer
 *  org.apache.hc.core5.util.Identifiable
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpConnectionMetrics;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStreamResetException;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.RequestNotExecutedException;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.impl.BasicEndpointDetails;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.impl.CharCodingSupport;
import org.apache.hc.core5.http.io.HttpTransportMetrics;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncPushProducer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ExecutableCommand;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.H2StreamResetException;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.config.H2Param;
import org.apache.hc.core5.http2.config.H2Setting;
import org.apache.hc.core5.http2.frame.FrameFactory;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.FrameType;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.frame.StreamIdGenerator;
import org.apache.hc.core5.http2.hpack.HPackDecoder;
import org.apache.hc.core5.http2.hpack.HPackEncoder;
import org.apache.hc.core5.http2.impl.BasicH2TransportMetrics;
import org.apache.hc.core5.http2.impl.nio.FrameInputBuffer;
import org.apache.hc.core5.http2.impl.nio.FrameOutputBuffer;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.NoopH2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.ServerPushH2StreamHandler;
import org.apache.hc.core5.http2.nio.AsyncPingHandler;
import org.apache.hc.core5.http2.nio.command.PingCommand;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.ByteArrayBuffer;
import org.apache.hc.core5.util.Identifiable;
import org.apache.hc.core5.util.Timeout;

abstract class AbstractH2StreamMultiplexer
implements Identifiable,
HttpConnection {
    private static final long LINGER_TIME = 1000L;
    private static final long CONNECTION_WINDOW_LOW_MARK = 0xA00000L;
    private final ProtocolIOSession ioSession;
    private final FrameFactory frameFactory;
    private final StreamIdGenerator idGenerator;
    private final HttpProcessor httpProcessor;
    private final H2Config localConfig;
    private final BasicH2TransportMetrics inputMetrics;
    private final BasicH2TransportMetrics outputMetrics;
    private final BasicHttpConnectionMetrics connMetrics;
    private final FrameInputBuffer inputBuffer;
    private final FrameOutputBuffer outputBuffer;
    private final Deque<RawFrame> outputQueue;
    private final HPackEncoder hPackEncoder;
    private final HPackDecoder hPackDecoder;
    private final Map<Integer, H2Stream> streamMap;
    private final Queue<AsyncPingHandler> pingHandlers;
    private final AtomicInteger connInputWindow;
    private final AtomicInteger connOutputWindow;
    private final AtomicInteger outputRequests;
    private final AtomicInteger lastStreamId;
    private final H2StreamListener streamListener;
    private ConnectionHandshake connState = ConnectionHandshake.READY;
    private SettingsHandshake localSettingState = SettingsHandshake.READY;
    private SettingsHandshake remoteSettingState = SettingsHandshake.READY;
    private int initInputWinSize;
    private int initOutputWinSize;
    private int lowMark;
    private volatile H2Config remoteConfig;
    private Continuation continuation;
    private int processedRemoteStreamId;
    private EndpointDetails endpointDetails;
    private boolean goAwayReceived;

    AbstractH2StreamMultiplexer(ProtocolIOSession ioSession, FrameFactory frameFactory, StreamIdGenerator idGenerator, HttpProcessor httpProcessor, CharCodingConfig charCodingConfig, H2Config h2Config, H2StreamListener streamListener) {
        this.ioSession = (ProtocolIOSession)Args.notNull((Object)ioSession, (String)"IO session");
        this.frameFactory = (FrameFactory)Args.notNull((Object)frameFactory, (String)"Frame factory");
        this.idGenerator = (StreamIdGenerator)Args.notNull((Object)idGenerator, (String)"Stream id generator");
        this.httpProcessor = (HttpProcessor)Args.notNull((Object)httpProcessor, (String)"HTTP processor");
        this.localConfig = h2Config != null ? h2Config : H2Config.DEFAULT;
        this.inputMetrics = new BasicH2TransportMetrics();
        this.outputMetrics = new BasicH2TransportMetrics();
        this.connMetrics = new BasicHttpConnectionMetrics((HttpTransportMetrics)this.inputMetrics, (HttpTransportMetrics)this.outputMetrics);
        this.inputBuffer = new FrameInputBuffer(this.inputMetrics, this.localConfig.getMaxFrameSize());
        this.outputBuffer = new FrameOutputBuffer(this.outputMetrics, this.localConfig.getMaxFrameSize());
        this.outputQueue = new ConcurrentLinkedDeque<RawFrame>();
        this.pingHandlers = new ConcurrentLinkedQueue<AsyncPingHandler>();
        this.outputRequests = new AtomicInteger(0);
        this.lastStreamId = new AtomicInteger(0);
        this.hPackEncoder = new HPackEncoder(CharCodingSupport.createEncoder((CharCodingConfig)charCodingConfig));
        this.hPackDecoder = new HPackDecoder(CharCodingSupport.createDecoder((CharCodingConfig)charCodingConfig));
        this.streamMap = new ConcurrentHashMap<Integer, H2Stream>();
        this.remoteConfig = H2Config.INIT;
        this.connInputWindow = new AtomicInteger(H2Config.INIT.getInitialWindowSize());
        this.connOutputWindow = new AtomicInteger(H2Config.INIT.getInitialWindowSize());
        this.initInputWinSize = H2Config.INIT.getInitialWindowSize();
        this.initOutputWinSize = H2Config.INIT.getInitialWindowSize();
        this.hPackDecoder.setMaxTableSize(H2Config.INIT.getHeaderTableSize());
        this.hPackEncoder.setMaxTableSize(H2Config.INIT.getHeaderTableSize());
        this.hPackDecoder.setMaxListSize(H2Config.INIT.getMaxHeaderListSize());
        this.lowMark = H2Config.INIT.getInitialWindowSize() / 2;
        this.streamListener = streamListener;
    }

    public String getId() {
        return this.ioSession.getId();
    }

    abstract void acceptHeaderFrame() throws H2ConnectionException;

    abstract void acceptPushRequest() throws H2ConnectionException;

    abstract void acceptPushFrame() throws H2ConnectionException;

    abstract H2StreamHandler createRemotelyInitiatedStream(H2StreamChannel var1, HttpProcessor var2, BasicHttpConnectionMetrics var3, HandlerFactory<AsyncPushConsumer> var4) throws IOException;

    abstract H2StreamHandler createLocallyInitiatedStream(ExecutableCommand var1, H2StreamChannel var2, HttpProcessor var3, BasicHttpConnectionMetrics var4) throws IOException;

    private int updateWindow(AtomicInteger window, int delta) throws ArithmeticException {
        long newValue;
        int current;
        do {
            if ((newValue = (long)(current = window.get()) + (long)delta) == 0x80000000L) {
                newValue = Integer.MAX_VALUE;
            }
            if (Math.abs(newValue) <= Integer.MAX_VALUE) continue;
            throw new ArithmeticException("Update causes flow control window to exceed 2147483647");
        } while (!window.compareAndSet(current, (int)newValue));
        return (int)newValue;
    }

    private int updateInputWindow(int streamId, AtomicInteger window, int delta) throws ArithmeticException {
        int newSize = this.updateWindow(window, delta);
        if (this.streamListener != null) {
            this.streamListener.onInputFlowControl(this, streamId, delta, newSize);
        }
        return newSize;
    }

    private int updateOutputWindow(int streamId, AtomicInteger window, int delta) throws ArithmeticException {
        int newSize = this.updateWindow(window, delta);
        if (this.streamListener != null) {
            this.streamListener.onOutputFlowControl(this, streamId, delta, newSize);
        }
        return newSize;
    }

    private void commitFrameInternal(RawFrame frame) throws IOException {
        if (this.outputBuffer.isEmpty() && this.outputQueue.isEmpty()) {
            if (this.streamListener != null) {
                this.streamListener.onFrameOutput(this, frame.getStreamId(), frame);
            }
            this.outputBuffer.write(frame, (WritableByteChannel)this.ioSession);
        } else {
            this.outputQueue.addLast(frame);
        }
        this.ioSession.setEvent(4);
    }

    private void commitFrame(RawFrame frame) throws IOException {
        Args.notNull((Object)frame, (String)"Frame");
        this.ioSession.getLock().lock();
        try {
            this.commitFrameInternal(frame);
        }
        finally {
            this.ioSession.getLock().unlock();
        }
    }

    private void commitHeaders(int streamId, List<? extends Header> headers, boolean endStream) throws IOException {
        if (this.streamListener != null) {
            this.streamListener.onHeaderOutput(this, streamId, headers);
        }
        ByteArrayBuffer buf = new ByteArrayBuffer(512);
        this.hPackEncoder.encodeHeaders(buf, headers, this.localConfig.isCompressionEnabled());
        int off = 0;
        int remaining = buf.length();
        boolean continuation = false;
        while (remaining > 0) {
            RawFrame frame;
            boolean endHeaders;
            int chunk = Math.min(this.remoteConfig.getMaxFrameSize(), remaining);
            ByteBuffer payload = ByteBuffer.wrap(buf.array(), off, chunk);
            off += chunk;
            boolean bl = endHeaders = (remaining -= chunk) == 0;
            if (!continuation) {
                frame = this.frameFactory.createHeaders(streamId, payload, endHeaders, endStream);
                continuation = true;
            } else {
                frame = this.frameFactory.createContinuation(streamId, payload, endHeaders);
            }
            this.commitFrameInternal(frame);
        }
    }

    private void commitPushPromise(int streamId, int promisedStreamId, List<Header> headers) throws IOException {
        if (headers == null || headers.isEmpty()) {
            throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Message headers are missing");
        }
        if (this.streamListener != null) {
            this.streamListener.onHeaderOutput(this, streamId, headers);
        }
        ByteArrayBuffer buf = new ByteArrayBuffer(512);
        buf.append((int)((byte)(promisedStreamId >> 24)));
        buf.append((int)((byte)(promisedStreamId >> 16)));
        buf.append((int)((byte)(promisedStreamId >> 8)));
        buf.append((int)((byte)promisedStreamId));
        this.hPackEncoder.encodeHeaders(buf, headers, this.localConfig.isCompressionEnabled());
        int off = 0;
        int remaining = buf.length();
        boolean continuation = false;
        while (remaining > 0) {
            RawFrame frame;
            boolean endHeaders;
            int chunk = Math.min(this.remoteConfig.getMaxFrameSize(), remaining);
            ByteBuffer payload = ByteBuffer.wrap(buf.array(), off, chunk);
            off += chunk;
            boolean bl = endHeaders = (remaining -= chunk) == 0;
            if (!continuation) {
                frame = this.frameFactory.createPushPromise(streamId, payload, endHeaders);
                continuation = true;
            } else {
                frame = this.frameFactory.createContinuation(streamId, payload, endHeaders);
            }
            this.commitFrameInternal(frame);
        }
    }

    private void streamDataFrame(int streamId, AtomicInteger streamOutputWindow, ByteBuffer payload, int chunk) throws IOException {
        RawFrame dataFrame = this.frameFactory.createData(streamId, payload, false);
        if (this.streamListener != null) {
            this.streamListener.onFrameOutput(this, streamId, dataFrame);
        }
        this.updateOutputWindow(0, this.connOutputWindow, -chunk);
        this.updateOutputWindow(streamId, streamOutputWindow, -chunk);
        this.outputBuffer.write(dataFrame, (WritableByteChannel)this.ioSession);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int streamData(int streamId, AtomicInteger streamOutputWindow, ByteBuffer payload) throws IOException {
        if (this.outputBuffer.isEmpty() && this.outputQueue.isEmpty()) {
            int chunk;
            int capacity = Math.min(this.connOutputWindow.get(), streamOutputWindow.get());
            if (capacity <= 0) {
                return 0;
            }
            int maxPayloadSize = Math.min(capacity, this.remoteConfig.getMaxFrameSize());
            if (payload.remaining() <= maxPayloadSize) {
                chunk = payload.remaining();
                this.streamDataFrame(streamId, streamOutputWindow, payload, chunk);
            } else {
                chunk = maxPayloadSize;
                int originalLimit = payload.limit();
                try {
                    payload.limit(payload.position() + chunk);
                    this.streamDataFrame(streamId, streamOutputWindow, payload, chunk);
                }
                finally {
                    payload.limit(originalLimit);
                }
            }
            payload.position(payload.position() + chunk);
            this.ioSession.setEvent(4);
            return chunk;
        }
        return 0;
    }

    private void incrementInputCapacity(int streamId, AtomicInteger inputWindow, int inputCapacity) throws IOException {
        int streamWinSize;
        int remainingCapacity;
        int chunk;
        if (inputCapacity > 0 && (chunk = Math.min(inputCapacity, remainingCapacity = Integer.MAX_VALUE - (streamWinSize = inputWindow.get()))) != 0) {
            RawFrame windowUpdateFrame = this.frameFactory.createWindowUpdate(streamId, chunk);
            this.commitFrame(windowUpdateFrame);
            this.updateInputWindow(streamId, inputWindow, chunk);
        }
    }

    private void requestSessionOutput() {
        this.outputRequests.incrementAndGet();
        this.ioSession.setEvent(4);
    }

    private void updateLastStreamId(int streamId) {
        int currentId = this.lastStreamId.get();
        if (streamId > currentId) {
            this.lastStreamId.compareAndSet(currentId, streamId);
        }
    }

    private int generateStreamId() {
        int newStreamId;
        int currentId;
        while (!this.lastStreamId.compareAndSet(currentId = this.lastStreamId.get(), newStreamId = this.idGenerator.generate(currentId))) {
        }
        return newStreamId;
    }

    public final void onConnect() throws HttpException, IOException {
        this.connState = ConnectionHandshake.ACTIVE;
        RawFrame settingsFrame = this.frameFactory.createSettings(new H2Setting(H2Param.HEADER_TABLE_SIZE, this.localConfig.getHeaderTableSize()), new H2Setting(H2Param.ENABLE_PUSH, this.localConfig.isPushEnabled() ? 1 : 0), new H2Setting(H2Param.MAX_CONCURRENT_STREAMS, this.localConfig.getMaxConcurrentStreams()), new H2Setting(H2Param.INITIAL_WINDOW_SIZE, this.localConfig.getInitialWindowSize()), new H2Setting(H2Param.MAX_FRAME_SIZE, this.localConfig.getMaxFrameSize()), new H2Setting(H2Param.MAX_HEADER_LIST_SIZE, this.localConfig.getMaxHeaderListSize()));
        this.commitFrame(settingsFrame);
        this.localSettingState = SettingsHandshake.TRANSMITTED;
        this.maximizeConnWindow(this.connInputWindow.get());
        if (this.streamListener != null) {
            int initInputWindow = this.connInputWindow.get();
            this.streamListener.onInputFlowControl(this, 0, initInputWindow, initInputWindow);
            int initOutputWindow = this.connOutputWindow.get();
            this.streamListener.onOutputFlowControl(this, 0, initOutputWindow, initOutputWindow);
        }
    }

    public final void onInput(ByteBuffer src) throws HttpException, IOException {
        if (this.connState == ConnectionHandshake.SHUTDOWN) {
            this.ioSession.clearEvent(1);
        } else {
            RawFrame frame;
            while ((frame = this.inputBuffer.read(src, (ReadableByteChannel)this.ioSession)) != null) {
                if (this.streamListener != null) {
                    this.streamListener.onFrameInput(this, frame.getStreamId(), frame);
                }
                this.consumeFrame(frame);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void onOutput() throws HttpException, IOException {
        this.ioSession.getLock().lock();
        try {
            RawFrame frame;
            if (!this.outputBuffer.isEmpty()) {
                this.outputBuffer.flush((WritableByteChannel)this.ioSession);
            }
            while (this.outputBuffer.isEmpty() && (frame = this.outputQueue.poll()) != null) {
                if (this.streamListener != null) {
                    this.streamListener.onFrameOutput(this, frame.getStreamId(), frame);
                }
                this.outputBuffer.write(frame, (WritableByteChannel)this.ioSession);
            }
        }
        finally {
            this.ioSession.getLock().unlock();
        }
        if (this.connState.compareTo(ConnectionHandshake.SHUTDOWN) < 0) {
            if (this.connOutputWindow.get() > 0 && this.remoteSettingState == SettingsHandshake.ACKED) {
                this.produceOutput();
            }
            int pendingOutputRequests = this.outputRequests.get();
            boolean outputPending = false;
            if (!this.streamMap.isEmpty() && this.connOutputWindow.get() > 0) {
                for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                    H2Stream stream = entry.getValue();
                    if (stream.isLocalClosed() || stream.getOutputWindow().get() <= 0 || !stream.isOutputReady()) continue;
                    outputPending = true;
                    break;
                }
            }
            this.ioSession.getLock().lock();
            try {
                if (!outputPending && this.outputBuffer.isEmpty() && this.outputQueue.isEmpty() && this.outputRequests.compareAndSet(pendingOutputRequests, 0)) {
                    this.ioSession.clearEvent(4);
                } else {
                    this.outputRequests.addAndGet(-pendingOutputRequests);
                }
            }
            finally {
                this.ioSession.getLock().unlock();
            }
        }
        if (this.connState.compareTo(ConnectionHandshake.ACTIVE) <= 0 && this.remoteSettingState == SettingsHandshake.ACKED) {
            this.processPendingCommands();
        }
        if (this.connState.compareTo(ConnectionHandshake.GRACEFUL_SHUTDOWN) == 0) {
            int liveStreams = 0;
            Iterator<Map.Entry<Integer, H2Stream>> it = this.streamMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, H2Stream> entry = it.next();
                H2Stream stream = entry.getValue();
                if (stream.isLocalClosed() && stream.isRemoteClosed()) {
                    stream.releaseResources();
                    it.remove();
                    continue;
                }
                if (!this.idGenerator.isSameSide(stream.getId()) && stream.getId() > this.processedRemoteStreamId) continue;
                ++liveStreams;
            }
            if (liveStreams == 0) {
                this.connState = ConnectionHandshake.SHUTDOWN;
            }
        }
        if (this.connState.compareTo(ConnectionHandshake.SHUTDOWN) >= 0) {
            if (!this.streamMap.isEmpty()) {
                for (H2Stream stream : this.streamMap.values()) {
                    stream.releaseResources();
                }
                this.streamMap.clear();
            }
            this.ioSession.getLock().lock();
            try {
                if (this.outputBuffer.isEmpty() && this.outputQueue.isEmpty()) {
                    this.ioSession.close();
                }
            }
            finally {
                this.ioSession.getLock().unlock();
            }
        }
    }

    public final void onTimeout(Timeout timeout) throws HttpException, IOException {
        this.connState = ConnectionHandshake.SHUTDOWN;
        RawFrame goAway = this.localSettingState != SettingsHandshake.ACKED ? this.frameFactory.createGoAway(this.processedRemoteStreamId, H2Error.SETTINGS_TIMEOUT, "Setting timeout (" + timeout + ")") : this.frameFactory.createGoAway(this.processedRemoteStreamId, H2Error.NO_ERROR, "Timeout due to inactivity (" + timeout + ")");
        this.commitFrame(goAway);
        for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
            H2Stream stream = entry.getValue();
            stream.reset((Exception)((Object)new H2StreamResetException(H2Error.NO_ERROR, "Timeout due to inactivity (" + timeout + ")")));
        }
        this.streamMap.clear();
    }

    public final void onDisconnect() {
        Command command;
        AsyncPingHandler pingHandler;
        while ((pingHandler = this.pingHandlers.poll()) != null) {
            pingHandler.cancel();
        }
        for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
            H2Stream stream = entry.getValue();
            stream.cancel();
        }
        while ((command = this.ioSession.poll()) != null) {
            if (command instanceof ExecutableCommand) {
                ((ExecutableCommand)command).failed((Exception)new ConnectionClosedException());
                continue;
            }
            command.cancel();
        }
    }

    private void processPendingCommands() throws IOException, HttpException {
        Command command;
        while (this.streamMap.size() < this.remoteConfig.getMaxConcurrentStreams() && (command = this.ioSession.poll()) != null) {
            CancellableDependency cancellableDependency;
            if (command instanceof ShutdownCommand) {
                ShutdownCommand shutdownCommand = (ShutdownCommand)command;
                if (shutdownCommand.getType() == CloseMode.IMMEDIATE) {
                    for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                        H2Stream stream = entry.getValue();
                        stream.cancel();
                    }
                    this.streamMap.clear();
                    this.connState = ConnectionHandshake.SHUTDOWN;
                    break;
                }
                if (this.connState.compareTo(ConnectionHandshake.ACTIVE) > 0) break;
                RawFrame goAway = this.frameFactory.createGoAway(this.processedRemoteStreamId, H2Error.NO_ERROR, "Graceful shutdown");
                this.commitFrame(goAway);
                this.connState = this.streamMap.isEmpty() ? ConnectionHandshake.SHUTDOWN : ConnectionHandshake.GRACEFUL_SHUTDOWN;
                break;
            }
            if (command instanceof PingCommand) {
                PingCommand pingCommand = (PingCommand)command;
                AsyncPingHandler handler = pingCommand.getHandler();
                this.pingHandlers.add(handler);
                RawFrame ping = this.frameFactory.createPing(handler.getData());
                this.commitFrame(ping);
                continue;
            }
            if (!(command instanceof ExecutableCommand)) continue;
            int streamId = this.generateStreamId();
            H2StreamChannelImpl channel = new H2StreamChannelImpl(streamId, true, this.initInputWinSize, this.initOutputWinSize);
            ExecutableCommand executableCommand = (ExecutableCommand)command;
            H2StreamHandler streamHandler = this.createLocallyInitiatedStream(executableCommand, channel, this.httpProcessor, this.connMetrics);
            H2Stream stream = new H2Stream(channel, streamHandler, false);
            this.streamMap.put(streamId, stream);
            if (this.streamListener != null) {
                int initInputWindow = stream.getInputWindow().get();
                this.streamListener.onInputFlowControl(this, streamId, initInputWindow, initInputWindow);
                int initOutputWindow = stream.getOutputWindow().get();
                this.streamListener.onOutputFlowControl(this, streamId, initOutputWindow, initOutputWindow);
            }
            if (stream.isOutputReady()) {
                stream.produceOutput();
            }
            if ((cancellableDependency = executableCommand.getCancellableDependency()) != null) {
                cancellableDependency.setDependency(stream::abort);
            }
            if (this.outputQueue.isEmpty()) continue;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void onException(Exception cause) {
        try {
            Command command;
            AsyncPingHandler pingHandler;
            while ((pingHandler = this.pingHandlers.poll()) != null) {
                pingHandler.failed(cause);
            }
            while ((command = this.ioSession.poll()) != null) {
                if (command instanceof ExecutableCommand) {
                    ((ExecutableCommand)command).failed((Exception)new ConnectionClosedException());
                    continue;
                }
                command.cancel();
            }
            for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                H2Stream stream = entry.getValue();
                stream.reset(cause);
            }
            this.streamMap.clear();
            if (!(cause instanceof ConnectionClosedException) && this.connState.compareTo(ConnectionHandshake.GRACEFUL_SHUTDOWN) <= 0) {
                H2Error errorCode = cause instanceof H2ConnectionException ? H2Error.getByCode(((H2ConnectionException)cause).getCode()) : (cause instanceof ProtocolException ? H2Error.PROTOCOL_ERROR : H2Error.INTERNAL_ERROR);
                RawFrame goAway = this.frameFactory.createGoAway(this.processedRemoteStreamId, errorCode, cause.getMessage());
                this.commitFrame(goAway);
            }
        }
        catch (IOException closeMode) {
        }
        finally {
            this.connState = ConnectionHandshake.SHUTDOWN;
            CloseMode closeMode = cause instanceof ConnectionClosedException ? CloseMode.GRACEFUL : (cause instanceof IOException ? CloseMode.IMMEDIATE : CloseMode.GRACEFUL);
            this.ioSession.close(closeMode);
        }
    }

    private H2Stream getValidStream(int streamId) throws H2ConnectionException {
        if (streamId == 0) {
            throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id: " + streamId);
        }
        H2Stream stream = this.streamMap.get(streamId);
        if (stream == null) {
            if (streamId <= this.lastStreamId.get()) {
                throw new H2ConnectionException(H2Error.STREAM_CLOSED, "Stream closed");
            }
            throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected stream id: " + streamId);
        }
        return stream;
    }

    private void consumeFrame(RawFrame frame) throws HttpException, IOException {
        FrameType frameType = FrameType.valueOf(frame.getType());
        int streamId = frame.getStreamId();
        if (this.continuation != null && frameType != FrameType.CONTINUATION) {
            throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "CONTINUATION frame expected");
        }
        switch (frameType) {
            case DATA: {
                H2Stream stream = this.getValidStream(streamId);
                try {
                    this.consumeDataFrame(frame, stream);
                }
                catch (H2StreamResetException ex) {
                    stream.localReset(ex);
                }
                catch (HttpStreamResetException ex) {
                    stream.localReset((Exception)((Object)ex), ex.getCause() != null ? H2Error.INTERNAL_ERROR : H2Error.CANCEL);
                }
                if (!stream.isTerminated()) break;
                this.streamMap.remove(streamId);
                stream.releaseResources();
                this.requestSessionOutput();
                break;
            }
            case HEADERS: {
                if (streamId == 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id: " + streamId);
                }
                H2Stream stream = this.streamMap.get(streamId);
                if (stream == null) {
                    H2StreamHandler streamHandler;
                    this.acceptHeaderFrame();
                    if (this.idGenerator.isSameSide(streamId)) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id: " + streamId);
                    }
                    if (this.goAwayReceived) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "GOAWAY received");
                    }
                    this.updateLastStreamId(streamId);
                    H2StreamChannelImpl channel = new H2StreamChannelImpl(streamId, false, this.initInputWinSize, this.initOutputWinSize);
                    if (this.connState.compareTo(ConnectionHandshake.ACTIVE) <= 0) {
                        streamHandler = this.createRemotelyInitiatedStream(channel, this.httpProcessor, this.connMetrics, null);
                    } else {
                        streamHandler = NoopH2StreamHandler.INSTANCE;
                        channel.setLocalEndStream();
                    }
                    stream = new H2Stream(channel, streamHandler, true);
                    if (stream.isOutputReady()) {
                        stream.produceOutput();
                    }
                    this.streamMap.put(streamId, stream);
                }
                try {
                    this.consumeHeaderFrame(frame, stream);
                    if (stream.isOutputReady()) {
                        stream.produceOutput();
                    }
                }
                catch (H2StreamResetException ex) {
                    stream.localReset(ex);
                }
                catch (HttpStreamResetException ex) {
                    stream.localReset((Exception)((Object)ex), ex.getCause() != null ? H2Error.INTERNAL_ERROR : H2Error.CANCEL);
                }
                catch (HttpException ex) {
                    stream.handle(ex);
                }
                if (!stream.isTerminated()) break;
                this.streamMap.remove(streamId);
                stream.releaseResources();
                this.requestSessionOutput();
                break;
            }
            case CONTINUATION: {
                if (this.continuation == null) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected CONTINUATION frame");
                }
                if (streamId != this.continuation.streamId) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected CONTINUATION stream id: " + streamId);
                }
                H2Stream stream = this.getValidStream(streamId);
                try {
                    this.consumeContinuationFrame(frame, stream);
                }
                catch (H2StreamResetException ex) {
                    stream.localReset(ex);
                }
                catch (HttpStreamResetException ex) {
                    stream.localReset((Exception)((Object)ex), ex.getCause() != null ? H2Error.INTERNAL_ERROR : H2Error.CANCEL);
                }
                if (!stream.isTerminated()) break;
                this.streamMap.remove(streamId);
                stream.releaseResources();
                this.requestSessionOutput();
                break;
            }
            case WINDOW_UPDATE: {
                ByteBuffer payload = frame.getPayload();
                if (payload == null || payload.remaining() != 4) {
                    throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid WINDOW_UPDATE frame payload");
                }
                int delta = payload.getInt();
                if (delta <= 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Invalid WINDOW_UPDATE delta");
                }
                if (streamId == 0) {
                    try {
                        this.updateOutputWindow(0, this.connOutputWindow, delta);
                    }
                    catch (ArithmeticException ex) {
                        throw new H2ConnectionException(H2Error.FLOW_CONTROL_ERROR, ex.getMessage());
                    }
                }
                H2Stream stream = this.streamMap.get(streamId);
                if (stream != null) {
                    try {
                        this.updateOutputWindow(streamId, stream.getOutputWindow(), delta);
                    }
                    catch (ArithmeticException ex) {
                        throw new H2ConnectionException(H2Error.FLOW_CONTROL_ERROR, ex.getMessage());
                    }
                }
                this.ioSession.setEvent(4);
                break;
            }
            case RST_STREAM: {
                if (streamId == 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id: " + streamId);
                }
                H2Stream stream = this.streamMap.get(streamId);
                if (stream == null) {
                    if (streamId <= this.lastStreamId.get()) break;
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected stream id: " + streamId);
                }
                ByteBuffer payload = frame.getPayload();
                if (payload == null || payload.remaining() != 4) {
                    throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid RST_STREAM frame payload");
                }
                int errorCode = payload.getInt();
                stream.reset((Exception)((Object)new H2StreamResetException(errorCode, "Stream reset (" + errorCode + ")")));
                this.streamMap.remove(streamId);
                stream.releaseResources();
                this.requestSessionOutput();
                break;
            }
            case PING: {
                if (streamId != 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id");
                }
                ByteBuffer ping = frame.getPayloadContent();
                if (ping == null || ping.remaining() != 8) {
                    throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid PING frame payload");
                }
                if (frame.isFlagSet(FrameFlag.ACK)) {
                    AsyncPingHandler pingHandler = this.pingHandlers.poll();
                    if (pingHandler == null) break;
                    pingHandler.consumeResponse(ping);
                    break;
                }
                ByteBuffer pong = ByteBuffer.allocate(ping.remaining());
                pong.put(ping);
                pong.flip();
                RawFrame response = this.frameFactory.createPingAck(pong);
                this.commitFrame(response);
                break;
            }
            case SETTINGS: {
                if (streamId != 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id");
                }
                if (frame.isFlagSet(FrameFlag.ACK)) {
                    if (this.localSettingState != SettingsHandshake.TRANSMITTED) break;
                    this.localSettingState = SettingsHandshake.ACKED;
                    this.ioSession.setEvent(4);
                    this.applyLocalSettings();
                    break;
                }
                ByteBuffer payload = frame.getPayload();
                if (payload != null) {
                    if (payload.remaining() % 6 != 0) {
                        throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid SETTINGS payload");
                    }
                    this.consumeSettingsFrame(payload);
                    this.remoteSettingState = SettingsHandshake.TRANSMITTED;
                }
                RawFrame response = this.frameFactory.createSettingsAck();
                this.commitFrame(response);
                this.remoteSettingState = SettingsHandshake.ACKED;
                break;
            }
            case PRIORITY: {
                break;
            }
            case PUSH_PROMISE: {
                H2StreamHandler streamHandler;
                this.acceptPushFrame();
                if (this.goAwayReceived) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "GOAWAY received");
                }
                if (!this.localConfig.isPushEnabled()) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Push is disabled");
                }
                H2Stream stream = this.getValidStream(streamId);
                if (stream.isRemoteClosed()) {
                    stream.localReset(new H2StreamResetException(H2Error.STREAM_CLOSED, "Stream closed"));
                    break;
                }
                ByteBuffer payload = frame.getPayloadContent();
                if (payload == null || payload.remaining() < 4) {
                    throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid PUSH_PROMISE payload");
                }
                int promisedStreamId = payload.getInt();
                if (promisedStreamId == 0 || this.idGenerator.isSameSide(promisedStreamId)) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal promised stream id: " + promisedStreamId);
                }
                if (this.streamMap.get(promisedStreamId) != null) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected promised stream id: " + promisedStreamId);
                }
                this.updateLastStreamId(promisedStreamId);
                H2StreamChannelImpl channel = new H2StreamChannelImpl(promisedStreamId, false, this.initInputWinSize, this.initOutputWinSize);
                if (this.connState.compareTo(ConnectionHandshake.ACTIVE) <= 0) {
                    streamHandler = this.createRemotelyInitiatedStream(channel, this.httpProcessor, this.connMetrics, stream.getPushHandlerFactory());
                } else {
                    streamHandler = NoopH2StreamHandler.INSTANCE;
                    channel.setLocalEndStream();
                }
                H2Stream promisedStream = new H2Stream(channel, streamHandler, true);
                this.streamMap.put(promisedStreamId, promisedStream);
                try {
                    this.consumePushPromiseFrame(frame, payload, promisedStream);
                }
                catch (H2StreamResetException ex) {
                    promisedStream.localReset(ex);
                }
                catch (HttpStreamResetException ex) {
                    promisedStream.localReset((Exception)((Object)ex), ex.getCause() != null ? H2Error.INTERNAL_ERROR : H2Error.NO_ERROR);
                }
                break;
            }
            case GOAWAY: {
                if (streamId != 0) {
                    throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal stream id");
                }
                ByteBuffer payload = frame.getPayload();
                if (payload == null || payload.remaining() < 8) {
                    throw new H2ConnectionException(H2Error.FRAME_SIZE_ERROR, "Invalid GOAWAY payload");
                }
                int processedLocalStreamId = payload.getInt();
                int errorCode = payload.getInt();
                this.goAwayReceived = true;
                if (errorCode == H2Error.NO_ERROR.getCode()) {
                    if (this.connState.compareTo(ConnectionHandshake.ACTIVE) <= 0) {
                        Iterator<Map.Entry<Integer, H2Stream>> it = this.streamMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, H2Stream> entry = it.next();
                            int activeStreamId = entry.getKey();
                            if (this.idGenerator.isSameSide(activeStreamId) || activeStreamId <= processedLocalStreamId) continue;
                            H2Stream stream = entry.getValue();
                            stream.cancel();
                            it.remove();
                        }
                    }
                    this.connState = this.streamMap.isEmpty() ? ConnectionHandshake.SHUTDOWN : ConnectionHandshake.GRACEFUL_SHUTDOWN;
                } else {
                    for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                        H2Stream stream = entry.getValue();
                        stream.reset((Exception)((Object)new H2StreamResetException(errorCode, "Connection terminated by the peer (" + errorCode + ")")));
                    }
                    this.streamMap.clear();
                    this.connState = ConnectionHandshake.SHUTDOWN;
                }
                this.ioSession.setEvent(4);
            }
        }
    }

    private void consumeDataFrame(RawFrame frame, H2Stream stream) throws HttpException, IOException {
        int streamId = stream.getId();
        ByteBuffer payload = frame.getPayloadContent();
        if (payload != null) {
            int connWinSize;
            int frameLength = frame.getLength();
            int streamWinSize = this.updateInputWindow(streamId, stream.getInputWindow(), -frameLength);
            if (streamWinSize < this.lowMark && !stream.isRemoteClosed()) {
                stream.produceInputCapacityUpdate();
            }
            if ((long)(connWinSize = this.updateInputWindow(0, this.connInputWindow, -frameLength)) < 0xA00000L) {
                this.maximizeConnWindow(connWinSize);
            }
        }
        if (stream.isRemoteClosed()) {
            throw new H2StreamResetException(H2Error.STREAM_CLOSED, "Stream already closed");
        }
        if (frame.isFlagSet(FrameFlag.END_STREAM)) {
            stream.setRemoteEndStream();
        }
        if (stream.isLocalReset()) {
            return;
        }
        stream.consumeData(payload);
    }

    private void maximizeConnWindow(int connWinSize) throws IOException {
        int delta = Integer.MAX_VALUE - connWinSize;
        if (delta > 0) {
            RawFrame windowUpdateFrame = this.frameFactory.createWindowUpdate(0, delta);
            this.commitFrame(windowUpdateFrame);
            this.updateInputWindow(0, this.connInputWindow, delta);
        }
    }

    private void consumePushPromiseFrame(RawFrame frame, ByteBuffer payload, H2Stream promisedStream) throws HttpException, IOException {
        int promisedStreamId = promisedStream.getId();
        if (!frame.isFlagSet(FrameFlag.END_HEADERS)) {
            this.continuation = new Continuation(promisedStreamId, frame.getType(), true);
        }
        if (this.continuation == null) {
            List<Header> headers = this.hPackDecoder.decodeHeaders(payload);
            if (promisedStreamId > this.processedRemoteStreamId) {
                this.processedRemoteStreamId = promisedStreamId;
            }
            if (this.streamListener != null) {
                this.streamListener.onHeaderInput(this, promisedStreamId, headers);
            }
            promisedStream.consumePromise(headers);
        } else {
            this.continuation.copyPayload(payload);
        }
    }

    List<Header> decodeHeaders(ByteBuffer payload) throws HttpException {
        return this.hPackDecoder.decodeHeaders(payload);
    }

    private void consumeHeaderFrame(RawFrame frame, H2Stream stream) throws HttpException, IOException {
        int streamId = stream.getId();
        if (!frame.isFlagSet(FrameFlag.END_HEADERS)) {
            this.continuation = new Continuation(streamId, frame.getType(), frame.isFlagSet(FrameFlag.END_STREAM));
        }
        ByteBuffer payload = frame.getPayloadContent();
        if (frame.isFlagSet(FrameFlag.PRIORITY)) {
            payload.getInt();
            payload.get();
        }
        if (this.continuation == null) {
            List<Header> headers = this.decodeHeaders(payload);
            if (stream.isRemoteInitiated() && streamId > this.processedRemoteStreamId) {
                this.processedRemoteStreamId = streamId;
            }
            if (this.streamListener != null) {
                this.streamListener.onHeaderInput(this, streamId, headers);
            }
            if (stream.isRemoteClosed()) {
                throw new H2StreamResetException(H2Error.STREAM_CLOSED, "Stream already closed");
            }
            if (stream.isLocalReset()) {
                return;
            }
            if (frame.isFlagSet(FrameFlag.END_STREAM)) {
                stream.setRemoteEndStream();
            }
            stream.consumeHeader(headers);
        } else {
            this.continuation.copyPayload(payload);
        }
    }

    private void consumeContinuationFrame(RawFrame frame, H2Stream stream) throws HttpException, IOException {
        int streamId = frame.getStreamId();
        ByteBuffer payload = frame.getPayload();
        this.continuation.copyPayload(payload);
        if (frame.isFlagSet(FrameFlag.END_HEADERS)) {
            List<Header> headers = this.decodeHeaders(this.continuation.getContent());
            if (stream.isRemoteInitiated() && streamId > this.processedRemoteStreamId) {
                this.processedRemoteStreamId = streamId;
            }
            if (this.streamListener != null) {
                this.streamListener.onHeaderInput(this, streamId, headers);
            }
            if (stream.isRemoteClosed()) {
                throw new H2StreamResetException(H2Error.STREAM_CLOSED, "Stream already closed");
            }
            if (stream.isLocalReset()) {
                return;
            }
            if (this.continuation.endStream) {
                stream.setRemoteEndStream();
            }
            if (this.continuation.type == FrameType.PUSH_PROMISE.getValue()) {
                stream.consumePromise(headers);
            } else {
                stream.consumeHeader(headers);
            }
            this.continuation = null;
        }
    }

    private void consumeSettingsFrame(ByteBuffer payload) throws HttpException, IOException {
        H2Config.Builder configBuilder = H2Config.initial();
        while (payload.hasRemaining()) {
            short code = payload.getShort();
            int value = payload.getInt();
            H2Param param = H2Param.valueOf(code);
            if (param == null) continue;
            switch (param) {
                case HEADER_TABLE_SIZE: {
                    try {
                        configBuilder.setHeaderTableSize(value);
                        break;
                    }
                    catch (IllegalArgumentException ex) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                    }
                }
                case MAX_CONCURRENT_STREAMS: {
                    try {
                        configBuilder.setMaxConcurrentStreams(value);
                        break;
                    }
                    catch (IllegalArgumentException ex) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                    }
                }
                case ENABLE_PUSH: {
                    configBuilder.setPushEnabled(value == 1);
                    break;
                }
                case INITIAL_WINDOW_SIZE: {
                    try {
                        configBuilder.setInitialWindowSize(value);
                        break;
                    }
                    catch (IllegalArgumentException ex) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                    }
                }
                case MAX_FRAME_SIZE: {
                    try {
                        configBuilder.setMaxFrameSize(value);
                        break;
                    }
                    catch (IllegalArgumentException ex) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                    }
                }
                case MAX_HEADER_LIST_SIZE: {
                    try {
                        configBuilder.setMaxHeaderListSize(value);
                        break;
                    }
                    catch (IllegalArgumentException ex) {
                        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                    }
                }
            }
        }
        this.applyRemoteSettings(configBuilder.build());
    }

    private void produceOutput() throws HttpException, IOException {
        Iterator<Map.Entry<Integer, H2Stream>> it = this.streamMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, H2Stream> entry = it.next();
            H2Stream stream = entry.getValue();
            if (!stream.isLocalClosed() && stream.getOutputWindow().get() > 0) {
                stream.produceOutput();
            }
            if (stream.isTerminated()) {
                it.remove();
                stream.releaseResources();
                this.requestSessionOutput();
            }
            if (this.outputQueue.isEmpty()) continue;
            break;
        }
    }

    private void applyRemoteSettings(H2Config config) throws H2ConnectionException {
        this.remoteConfig = config;
        this.hPackEncoder.setMaxTableSize(this.remoteConfig.getHeaderTableSize());
        int delta = this.remoteConfig.getInitialWindowSize() - this.initOutputWinSize;
        this.initOutputWinSize = this.remoteConfig.getInitialWindowSize();
        int maxFrameSize = this.remoteConfig.getMaxFrameSize();
        if (maxFrameSize > this.localConfig.getMaxFrameSize()) {
            this.outputBuffer.expand(maxFrameSize);
        }
        if (delta != 0 && !this.streamMap.isEmpty()) {
            for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                H2Stream stream = entry.getValue();
                try {
                    this.updateOutputWindow(stream.getId(), stream.getOutputWindow(), delta);
                }
                catch (ArithmeticException ex) {
                    throw new H2ConnectionException(H2Error.FLOW_CONTROL_ERROR, ex.getMessage());
                }
            }
        }
    }

    private void applyLocalSettings() throws H2ConnectionException {
        this.hPackDecoder.setMaxTableSize(this.localConfig.getHeaderTableSize());
        this.hPackDecoder.setMaxListSize(this.localConfig.getMaxHeaderListSize());
        int delta = this.localConfig.getInitialWindowSize() - this.initInputWinSize;
        this.initInputWinSize = this.localConfig.getInitialWindowSize();
        if (delta != 0 && !this.streamMap.isEmpty()) {
            for (Map.Entry<Integer, H2Stream> entry : this.streamMap.entrySet()) {
                H2Stream stream = entry.getValue();
                try {
                    this.updateInputWindow(stream.getId(), stream.getInputWindow(), delta);
                }
                catch (ArithmeticException ex) {
                    throw new H2ConnectionException(H2Error.FLOW_CONTROL_ERROR, ex.getMessage());
                }
            }
        }
        this.lowMark = this.initInputWinSize / 2;
    }

    public void close() throws IOException {
        this.ioSession.enqueue((Command)ShutdownCommand.GRACEFUL, Command.Priority.IMMEDIATE);
    }

    public void close(CloseMode closeMode) {
        this.ioSession.close(closeMode);
    }

    public boolean isOpen() {
        return this.connState == ConnectionHandshake.ACTIVE;
    }

    public void setSocketTimeout(Timeout timeout) {
        this.ioSession.setSocketTimeout(timeout);
    }

    public SSLSession getSSLSession() {
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        return tlsDetails != null ? tlsDetails.getSSLSession() : null;
    }

    public EndpointDetails getEndpointDetails() {
        if (this.endpointDetails == null) {
            this.endpointDetails = new BasicEndpointDetails(this.ioSession.getRemoteAddress(), this.ioSession.getLocalAddress(), (HttpConnectionMetrics)this.connMetrics, this.ioSession.getSocketTimeout());
        }
        return this.endpointDetails;
    }

    public Timeout getSocketTimeout() {
        return this.ioSession.getSocketTimeout();
    }

    public ProtocolVersion getProtocolVersion() {
        return HttpVersion.HTTP_2;
    }

    public SocketAddress getRemoteAddress() {
        return this.ioSession.getRemoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return this.ioSession.getLocalAddress();
    }

    void appendState(StringBuilder buf) {
        buf.append("connState=").append((Object)this.connState).append(", connInputWindow=").append(this.connInputWindow).append(", connOutputWindow=").append(this.connOutputWindow).append(", outputQueue=").append(this.outputQueue.size()).append(", streamMap=").append(this.streamMap.size()).append(", processedRemoteStreamId=").append(this.processedRemoteStreamId);
    }

    static class H2Stream {
        private final H2StreamChannelImpl channel;
        private final H2StreamHandler handler;
        private final boolean remoteInitiated;

        private H2Stream(H2StreamChannelImpl channel, H2StreamHandler handler, boolean remoteInitiated) {
            this.channel = channel;
            this.handler = handler;
            this.remoteInitiated = remoteInitiated;
        }

        int getId() {
            return this.channel.getId();
        }

        boolean isRemoteInitiated() {
            return this.remoteInitiated;
        }

        AtomicInteger getOutputWindow() {
            return this.channel.getOutputWindow();
        }

        AtomicInteger getInputWindow() {
            return this.channel.getInputWindow();
        }

        boolean isTerminated() {
            return this.channel.isLocalClosed() && (this.channel.isRemoteClosed() || this.channel.isResetDeadline());
        }

        boolean isRemoteClosed() {
            return this.channel.isRemoteClosed();
        }

        boolean isLocalClosed() {
            return this.channel.isLocalClosed();
        }

        boolean isLocalReset() {
            return this.channel.isLocalReset();
        }

        void setRemoteEndStream() {
            this.channel.setRemoteEndStream();
        }

        void consumePromise(List<Header> headers) throws HttpException, IOException {
            try {
                this.handler.consumePromise(headers);
                this.channel.setLocalEndStream();
            }
            catch (ProtocolException ex) {
                this.localReset((Exception)((Object)ex), H2Error.PROTOCOL_ERROR);
            }
        }

        void consumeHeader(List<Header> headers) throws HttpException, IOException {
            try {
                this.handler.consumeHeader(headers, this.channel.isRemoteClosed());
            }
            catch (ProtocolException ex) {
                this.localReset((Exception)((Object)ex), H2Error.PROTOCOL_ERROR);
            }
        }

        void consumeData(ByteBuffer src) throws HttpException, IOException {
            try {
                this.handler.consumeData(src, this.channel.isRemoteClosed());
            }
            catch (CharacterCodingException ex) {
                this.localReset((Exception)ex, H2Error.INTERNAL_ERROR);
            }
            catch (ProtocolException ex) {
                this.localReset((Exception)((Object)ex), H2Error.PROTOCOL_ERROR);
            }
        }

        boolean isOutputReady() {
            return this.handler.isOutputReady();
        }

        void produceOutput() throws HttpException, IOException {
            try {
                this.handler.produceOutput();
            }
            catch (ProtocolException ex) {
                this.localReset((Exception)((Object)ex), H2Error.PROTOCOL_ERROR);
            }
        }

        void produceInputCapacityUpdate() throws IOException {
            this.handler.updateInputCapacity();
        }

        void reset(Exception cause) {
            this.channel.setRemoteEndStream();
            this.channel.setLocalEndStream();
            this.handler.failed(cause);
        }

        void localReset(Exception cause, int code) throws IOException {
            this.channel.localReset(code);
            this.handler.failed(cause);
        }

        void localReset(Exception cause, H2Error error) throws IOException {
            this.localReset(cause, error != null ? error.getCode() : H2Error.INTERNAL_ERROR.getCode());
        }

        void localReset(H2StreamResetException ex) throws IOException {
            this.localReset((Exception)((Object)ex), ex.getCode());
        }

        void handle(HttpException ex) throws IOException, HttpException {
            this.handler.handle(ex, this.channel.isRemoteClosed());
        }

        HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
            return this.handler.getPushHandlerFactory();
        }

        void cancel() {
            this.reset((Exception)new RequestNotExecutedException());
        }

        boolean abort() {
            boolean cancelled = this.channel.cancel();
            this.handler.failed((Exception)new RequestNotExecutedException());
            return cancelled;
        }

        void releaseResources() {
            this.handler.releaseResources();
        }

        void appendState(StringBuilder buf) {
            buf.append("channel=[");
            this.channel.appendState(buf);
            buf.append("]");
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("[");
            this.appendState(buf);
            buf.append("]");
            return buf.toString();
        }
    }

    private class H2StreamChannelImpl
    implements H2StreamChannel {
        private final int id;
        private final AtomicInteger inputWindow;
        private final AtomicInteger outputWindow;
        private volatile boolean idle;
        private volatile boolean remoteEndStream;
        private volatile boolean localEndStream;
        private volatile long deadline;

        H2StreamChannelImpl(int id, boolean idle, int initialInputWindowSize, int initialOutputWindowSize) {
            this.id = id;
            this.idle = idle;
            this.inputWindow = new AtomicInteger(initialInputWindowSize);
            this.outputWindow = new AtomicInteger(initialOutputWindowSize);
        }

        int getId() {
            return this.id;
        }

        AtomicInteger getOutputWindow() {
            return this.outputWindow;
        }

        AtomicInteger getInputWindow() {
            return this.inputWindow;
        }

        @Override
        public void submit(List<Header> headers, boolean endStream) throws IOException {
            AbstractH2StreamMultiplexer.this.ioSession.getLock().lock();
            try {
                if (headers == null || headers.isEmpty()) {
                    throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Message headers are missing");
                }
                if (this.localEndStream) {
                    return;
                }
                this.idle = false;
                AbstractH2StreamMultiplexer.this.commitHeaders(this.id, headers, endStream);
                if (endStream) {
                    this.localEndStream = true;
                }
            }
            finally {
                AbstractH2StreamMultiplexer.this.ioSession.getLock().unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void push(List<Header> headers, AsyncPushProducer pushProducer) throws HttpException, IOException {
            AbstractH2StreamMultiplexer.this.acceptPushRequest();
            int promisedStreamId = AbstractH2StreamMultiplexer.this.generateStreamId();
            H2StreamChannelImpl channel = new H2StreamChannelImpl(promisedStreamId, true, AbstractH2StreamMultiplexer.this.localConfig.getInitialWindowSize(), AbstractH2StreamMultiplexer.this.remoteConfig.getInitialWindowSize());
            HttpCoreContext context = HttpCoreContext.create();
            context.setAttribute("http.ssl-session", (Object)AbstractH2StreamMultiplexer.this.getSSLSession());
            context.setAttribute("http.connection-endpoint", (Object)AbstractH2StreamMultiplexer.this.getEndpointDetails());
            ServerPushH2StreamHandler streamHandler = new ServerPushH2StreamHandler(channel, AbstractH2StreamMultiplexer.this.httpProcessor, AbstractH2StreamMultiplexer.this.connMetrics, pushProducer, context);
            H2Stream stream = new H2Stream(channel, streamHandler, false);
            AbstractH2StreamMultiplexer.this.streamMap.put(promisedStreamId, stream);
            AbstractH2StreamMultiplexer.this.ioSession.getLock().lock();
            try {
                if (this.localEndStream) {
                    stream.releaseResources();
                    return;
                }
                AbstractH2StreamMultiplexer.this.commitPushPromise(this.id, promisedStreamId, headers);
                this.idle = false;
            }
            finally {
                AbstractH2StreamMultiplexer.this.ioSession.getLock().unlock();
            }
        }

        public void update(int increment) throws IOException {
            if (this.remoteEndStream) {
                return;
            }
            AbstractH2StreamMultiplexer.this.incrementInputCapacity(0, AbstractH2StreamMultiplexer.this.connInputWindow, increment);
            AbstractH2StreamMultiplexer.this.incrementInputCapacity(this.id, this.inputWindow, increment);
        }

        public int write(ByteBuffer payload) throws IOException {
            AbstractH2StreamMultiplexer.this.ioSession.getLock().lock();
            try {
                if (this.localEndStream) {
                    int n = 0;
                    return n;
                }
                int n = AbstractH2StreamMultiplexer.this.streamData(this.id, this.outputWindow, payload);
                return n;
            }
            finally {
                AbstractH2StreamMultiplexer.this.ioSession.getLock().unlock();
            }
        }

        public void endStream(List<? extends Header> trailers) throws IOException {
            AbstractH2StreamMultiplexer.this.ioSession.getLock().lock();
            try {
                if (this.localEndStream) {
                    return;
                }
                this.localEndStream = true;
                if (trailers != null && !trailers.isEmpty()) {
                    AbstractH2StreamMultiplexer.this.commitHeaders(this.id, trailers, true);
                } else {
                    RawFrame frame = AbstractH2StreamMultiplexer.this.frameFactory.createData(this.id, null, true);
                    AbstractH2StreamMultiplexer.this.commitFrameInternal(frame);
                }
            }
            finally {
                AbstractH2StreamMultiplexer.this.ioSession.getLock().unlock();
            }
        }

        public void endStream() throws IOException {
            this.endStream(null);
        }

        public void requestOutput() {
            AbstractH2StreamMultiplexer.this.requestSessionOutput();
        }

        boolean isRemoteClosed() {
            return this.remoteEndStream;
        }

        void setRemoteEndStream() {
            this.remoteEndStream = true;
        }

        boolean isLocalClosed() {
            return this.localEndStream;
        }

        void setLocalEndStream() {
            this.localEndStream = true;
        }

        boolean isLocalReset() {
            return this.deadline > 0L;
        }

        boolean isResetDeadline() {
            long l = this.deadline;
            return l > 0L && l < System.currentTimeMillis();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean localReset(int code) throws IOException {
            AbstractH2StreamMultiplexer.this.ioSession.getLock().lock();
            try {
                if (this.localEndStream) {
                    boolean bl = false;
                    return bl;
                }
                this.localEndStream = true;
                this.deadline = System.currentTimeMillis() + 1000L;
                if (!this.idle) {
                    RawFrame resetStream = AbstractH2StreamMultiplexer.this.frameFactory.createResetStream(this.id, code);
                    AbstractH2StreamMultiplexer.this.commitFrameInternal(resetStream);
                    boolean bl = true;
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                AbstractH2StreamMultiplexer.this.ioSession.getLock().unlock();
            }
        }

        boolean localReset(H2Error error) throws IOException {
            return this.localReset(error != null ? error.getCode() : H2Error.INTERNAL_ERROR.getCode());
        }

        public boolean cancel() {
            try {
                return this.localReset(H2Error.CANCEL);
            }
            catch (IOException ignore) {
                return false;
            }
        }

        void appendState(StringBuilder buf) {
            buf.append("id=").append(this.id).append(", connState=").append((Object)AbstractH2StreamMultiplexer.this.connState).append(", inputWindow=").append(this.inputWindow).append(", outputWindow=").append(this.outputWindow).append(", localEndStream=").append(this.localEndStream).append(", idle=").append(this.idle);
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("[");
            this.appendState(buf);
            buf.append("]");
            return buf.toString();
        }
    }

    private static class Continuation {
        final int streamId;
        final int type;
        final boolean endStream;
        final ByteArrayBuffer headerBuffer;

        private Continuation(int streamId, int type, boolean endStream) {
            this.streamId = streamId;
            this.type = type;
            this.endStream = endStream;
            this.headerBuffer = new ByteArrayBuffer(1024);
        }

        void copyPayload(ByteBuffer payload) {
            if (payload == null) {
                return;
            }
            this.headerBuffer.ensureCapacity(payload.remaining());
            payload.get(this.headerBuffer.array(), this.headerBuffer.length(), payload.remaining());
        }

        ByteBuffer getContent() {
            return ByteBuffer.wrap(this.headerBuffer.array(), 0, this.headerBuffer.length());
        }
    }

    static enum SettingsHandshake {
        READY,
        TRANSMITTED,
        ACKED;

    }

    static enum ConnectionHandshake {
        READY,
        ACTIVE,
        GRACEFUL_SHUTDOWN,
        SHUTDOWN;

    }
}

