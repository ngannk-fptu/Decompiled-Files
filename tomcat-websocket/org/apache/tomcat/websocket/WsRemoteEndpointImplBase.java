/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.DeploymentException
 *  javax.websocket.EncodeException
 *  javax.websocket.Encoder
 *  javax.websocket.Encoder$Binary
 *  javax.websocket.Encoder$BinaryStream
 *  javax.websocket.Encoder$Text
 *  javax.websocket.Encoder$TextStream
 *  javax.websocket.EndpointConfig
 *  javax.websocket.RemoteEndpoint
 *  javax.websocket.SendHandler
 *  javax.websocket.SendResult
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.Utf8Encoder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import javax.naming.NamingException;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.FutureToSendHandler;
import org.apache.tomcat.websocket.MessagePart;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WsSession;

public abstract class WsRemoteEndpointImplBase
implements RemoteEndpoint {
    protected static final StringManager sm = StringManager.getManager(WsRemoteEndpointImplBase.class);
    protected static final SendResult SENDRESULT_OK = new SendResult();
    private final Log log = LogFactory.getLog(WsRemoteEndpointImplBase.class);
    private final StateMachine stateMachine = new StateMachine();
    private final IntermediateMessageHandler intermediateMessageHandler = new IntermediateMessageHandler(this);
    private Transformation transformation = null;
    protected final Semaphore messagePartInProgress = new Semaphore(1);
    private final Queue<MessagePart> messagePartQueue = new ArrayDeque<MessagePart>();
    private final Object messagePartLock = new Object();
    private volatile boolean closed = false;
    private boolean fragmented = false;
    private boolean nextFragmented = false;
    private boolean text = false;
    private boolean nextText = false;
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(14);
    private final ByteBuffer outputBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final CharsetEncoder encoder = new Utf8Encoder();
    private final ByteBuffer encoderBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final AtomicBoolean batchingAllowed = new AtomicBoolean(false);
    private volatile long sendTimeout = -1L;
    private WsSession wsSession;
    private List<EncoderEntry> encoderEntries = new ArrayList<EncoderEntry>();

    protected void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public long getSendTimeout() {
        return this.sendTimeout;
    }

    public void setSendTimeout(long timeout) {
        this.sendTimeout = timeout;
    }

    public void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        boolean oldValue = this.batchingAllowed.getAndSet(batchingAllowed);
        if (oldValue && !batchingAllowed) {
            this.flushBatch();
        }
    }

    public boolean getBatchingAllowed() {
        return this.batchingAllowed.get();
    }

    public void flushBatch() throws IOException {
        this.sendMessageBlock((byte)24, null, true);
    }

    public void sendBytes(ByteBuffer data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryStart();
        this.sendMessageBlock((byte)2, data, true);
        this.stateMachine.complete(true);
    }

    public Future<Void> sendBytesByFuture(ByteBuffer data) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        this.sendBytesByCompletion(data, f2sh);
        return f2sh;
    }

    public void sendBytesByCompletion(ByteBuffer data, SendHandler handler) {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        StateUpdateSendHandler sush = new StateUpdateSendHandler(handler, this.stateMachine);
        this.stateMachine.binaryStart();
        this.startMessage((byte)2, data, true, sush);
    }

    public void sendPartialBytes(ByteBuffer partialByte, boolean last) throws IOException {
        if (partialByte == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryPartialStart();
        this.sendMessageBlock((byte)2, partialByte, last);
        this.stateMachine.complete(last);
    }

    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        this.sendMessageBlock((byte)9, applicationData, true);
    }

    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        this.sendMessageBlock((byte)10, applicationData, true);
    }

    public void sendString(String text) throws IOException {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textStart();
        this.sendMessageBlock(CharBuffer.wrap(text), true);
    }

    public Future<Void> sendStringByFuture(String text) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        this.sendStringByCompletion(text, f2sh);
        return f2sh;
    }

    public void sendStringByCompletion(String text, SendHandler handler) {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        this.stateMachine.textStart();
        TextMessageSendHandler tmsh = new TextMessageSendHandler(handler, CharBuffer.wrap(text), true, this.encoder, this.encoderBuffer, this);
        tmsh.write();
    }

    public void sendPartialString(String fragment, boolean isLast) throws IOException {
        if (fragment == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textPartialStart();
        this.sendMessageBlock(CharBuffer.wrap(fragment), isLast);
    }

    public OutputStream getSendStream() {
        this.stateMachine.streamStart();
        return new WsOutputStream(this);
    }

    public Writer getSendWriter() {
        this.stateMachine.writeStart();
        return new WsWriter(this);
    }

    void sendMessageBlock(CharBuffer part, boolean last) throws IOException {
        long timeoutExpiry = this.getTimeoutExpiry();
        boolean isDone = false;
        while (!isDone) {
            this.encoderBuffer.clear();
            CoderResult cr = this.encoder.encode(part, this.encoderBuffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            isDone = !cr.isOverflow();
            this.encoderBuffer.flip();
            this.sendMessageBlock((byte)1, this.encoderBuffer, last && isDone, timeoutExpiry);
        }
        this.stateMachine.complete(last);
    }

    void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last) throws IOException {
        this.sendMessageBlock(opCode, payload, last, this.getTimeoutExpiry());
    }

    private long getTimeoutExpiry() {
        long timeout = this.getBlockingSendTimeout();
        if (timeout < 0L) {
            return Long.MAX_VALUE;
        }
        return System.currentTimeMillis() + timeout;
    }

    private void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last, long timeoutExpiry) throws IOException {
        this.wsSession.updateLastActiveWrite();
        BlockingSendHandler bsh = new BlockingSendHandler();
        List<MessagePart> messageParts = new ArrayList<MessagePart>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, bsh, bsh, timeoutExpiry));
        messageParts = this.transformation.sendMessagePart(messageParts);
        if (messageParts.size() == 0) {
            return;
        }
        try {
            if (!this.acquireMessagePartInProgressSemaphore(opCode, timeoutExpiry)) {
                String msg = sm.getString("wsRemoteEndpoint.acquireTimeout");
                this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg), true);
                throw new SocketTimeoutException(msg);
            }
        }
        catch (InterruptedException e) {
            String msg = sm.getString("wsRemoteEndpoint.sendInterrupt");
            this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg), true);
            throw new IOException(msg, e);
        }
        for (MessagePart mp : messageParts) {
            try {
                this.writeMessagePart(mp);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.messagePartInProgress.release();
                this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, t.getMessage()), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage()), true);
                throw t;
            }
            if (!bsh.getSendResult().isOK()) {
                this.messagePartInProgress.release();
                t = bsh.getSendResult().getException();
                this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, t.getMessage()), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage()), true);
                throw new IOException(t);
            }
            this.fragmented = this.nextFragmented;
            this.text = this.nextText;
        }
        if (payload != null) {
            payload.clear();
        }
        this.endMessage(null, null);
    }

    protected boolean acquireMessagePartInProgressSemaphore(byte opCode, long timeoutExpiry) throws InterruptedException {
        long timeout = timeoutExpiry - System.currentTimeMillis();
        return this.messagePartInProgress.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void startMessage(byte opCode, ByteBuffer payload, boolean last, SendHandler handler) {
        this.wsSession.updateLastActiveWrite();
        List<MessagePart> messageParts = new ArrayList<MessagePart>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, this.intermediateMessageHandler, new EndMessageHandler(this, handler), -1L));
        try {
            messageParts = this.transformation.sendMessagePart(messageParts);
        }
        catch (IOException ioe) {
            handler.onResult(new SendResult((Throwable)ioe));
            return;
        }
        if (messageParts.size() == 0) {
            handler.onResult(new SendResult());
            return;
        }
        MessagePart mp = messageParts.remove(0);
        boolean doWrite = false;
        Object object = this.messagePartLock;
        synchronized (object) {
            if (8 == mp.getOpCode() && this.getBatchingAllowed()) {
                this.log.warn((Object)sm.getString("wsRemoteEndpoint.flushOnCloseFailed"));
            }
            if (this.messagePartInProgress.tryAcquire()) {
                doWrite = true;
            } else {
                this.messagePartQueue.add(mp);
            }
            this.messagePartQueue.addAll(messageParts);
        }
        if (doWrite) {
            this.writeMessagePart(mp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void endMessage(SendHandler handler, SendResult result) {
        boolean doWrite = false;
        MessagePart mpNext = null;
        Object object = this.messagePartLock;
        synchronized (object) {
            this.fragmented = this.nextFragmented;
            this.text = this.nextText;
            mpNext = this.messagePartQueue.poll();
            if (mpNext == null) {
                this.messagePartInProgress.release();
            } else if (!this.closed) {
                doWrite = true;
            }
        }
        if (doWrite) {
            this.writeMessagePart(mpNext);
        }
        this.wsSession.updateLastActiveWrite();
        if (handler != null) {
            handler.onResult(result);
        }
    }

    void writeMessagePart(MessagePart mp) {
        boolean first;
        if (this.closed) {
            throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closed"));
        }
        if (24 == mp.getOpCode()) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            this.outputBuffer.flip();
            OutputBufferFlushSendHandler flushHandler = new OutputBufferFlushSendHandler(this.outputBuffer, mp.getEndHandler());
            this.doWrite(flushHandler, mp.getBlockingWriteTimeoutExpiry(), this.outputBuffer);
            return;
        }
        if (Util.isControl(mp.getOpCode())) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            if (mp.getOpCode() == 8) {
                this.closed = true;
            }
            first = true;
        } else {
            boolean isText = Util.isText(mp.getOpCode());
            if (this.fragmented) {
                if (this.text != isText) {
                    throw new IllegalStateException(sm.getString("wsRemoteEndpoint.changeType"));
                }
                this.nextText = this.text;
                this.nextFragmented = !mp.isFin();
                first = false;
            } else {
                if (mp.isFin()) {
                    this.nextFragmented = false;
                } else {
                    this.nextFragmented = true;
                    this.nextText = isText;
                }
                first = true;
            }
        }
        Object mask = this.isMasked() ? Util.generateMask() : null;
        int payloadSize = mp.getPayload().remaining();
        this.headerBuffer.clear();
        WsRemoteEndpointImplBase.writeHeader(this.headerBuffer, mp.isFin(), mp.getRsv(), mp.getOpCode(), this.isMasked(), mp.getPayload(), mask, first);
        this.headerBuffer.flip();
        if (this.getBatchingAllowed() || this.isMasked()) {
            OutputBufferSendHandler obsh = new OutputBufferSendHandler(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload(), (byte[])mask, this.outputBuffer, !this.getBatchingAllowed(), this);
            obsh.write();
        } else {
            this.doWrite(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload());
        }
        this.updateStats(payloadSize);
    }

    protected void updateStats(long payloadLength) {
    }

    private long getBlockingSendTimeout() {
        Object obj = this.wsSession.getUserProperties().get("org.apache.tomcat.websocket.BLOCKING_SEND_TIMEOUT");
        Long userTimeout = null;
        if (obj instanceof Long) {
            userTimeout = (Long)obj;
        }
        if (userTimeout == null) {
            return 20000L;
        }
        return userTimeout;
    }

    public void sendObject(Object obj) throws IOException, EncodeException {
        if (obj == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        Encoder encoder = this.findEncoder(obj);
        if (encoder == null && Util.isPrimitive(obj.getClass())) {
            String msg = obj.toString();
            this.sendString(msg);
            return;
        }
        if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
            ByteBuffer msg = ByteBuffer.wrap((byte[])obj);
            this.sendBytes(msg);
            return;
        }
        if (encoder instanceof Encoder.Text) {
            String msg = ((Encoder.Text)encoder).encode(obj);
            this.sendString(msg);
        } else if (encoder instanceof Encoder.TextStream) {
            try (Writer w = this.getSendWriter();){
                ((Encoder.TextStream)encoder).encode(obj, w);
            }
        } else if (encoder instanceof Encoder.Binary) {
            ByteBuffer msg = ((Encoder.Binary)encoder).encode(obj);
            this.sendBytes(msg);
        } else if (encoder instanceof Encoder.BinaryStream) {
            try (OutputStream os = this.getSendStream();){
                ((Encoder.BinaryStream)encoder).encode(obj, os);
            }
        } else {
            throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", new Object[]{obj.getClass()}));
        }
    }

    public Future<Void> sendObjectByFuture(Object obj) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        this.sendObjectByCompletion(obj, f2sh);
        return f2sh;
    }

    public void sendObjectByCompletion(Object obj, SendHandler completion) {
        block22: {
            if (obj == null) {
                throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
            }
            if (completion == null) {
                throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
            }
            Encoder encoder = this.findEncoder(obj);
            if (encoder == null && Util.isPrimitive(obj.getClass())) {
                String msg = obj.toString();
                this.sendStringByCompletion(msg, completion);
                return;
            }
            if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
                ByteBuffer msg = ByteBuffer.wrap((byte[])obj);
                this.sendBytesByCompletion(msg, completion);
                return;
            }
            try {
                if (encoder instanceof Encoder.Text) {
                    String msg = ((Encoder.Text)encoder).encode(obj);
                    this.sendStringByCompletion(msg, completion);
                    break block22;
                }
                if (encoder instanceof Encoder.TextStream) {
                    try (Writer w = this.getSendWriter();){
                        ((Encoder.TextStream)encoder).encode(obj, w);
                    }
                    completion.onResult(new SendResult());
                    break block22;
                }
                if (encoder instanceof Encoder.Binary) {
                    ByteBuffer msg = ((Encoder.Binary)encoder).encode(obj);
                    this.sendBytesByCompletion(msg, completion);
                    break block22;
                }
                if (encoder instanceof Encoder.BinaryStream) {
                    try (OutputStream os = this.getSendStream();){
                        ((Encoder.BinaryStream)encoder).encode(obj, os);
                    }
                    completion.onResult(new SendResult());
                    break block22;
                }
                throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", new Object[]{obj.getClass()}));
            }
            catch (Exception e) {
                SendResult sr = new SendResult((Throwable)e);
                completion.onResult(sr);
            }
        }
    }

    protected void setSession(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    protected void setEncoders(EndpointConfig endpointConfig) throws DeploymentException {
        this.encoderEntries.clear();
        for (Class encoderClazz : endpointConfig.getEncoders()) {
            Encoder instance;
            InstanceManager instanceManager = this.wsSession.getInstanceManager();
            try {
                instance = instanceManager == null ? (Encoder)encoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]) : (Encoder)instanceManager.newInstance(encoderClazz);
                instance.init(endpointConfig);
            }
            catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("wsRemoteEndpoint.invalidEncoder", new Object[]{encoderClazz.getName()}), (Throwable)e);
            }
            EncoderEntry entry = new EncoderEntry(Util.getEncoderType(encoderClazz), instance);
            this.encoderEntries.add(entry);
        }
    }

    private Encoder findEncoder(Object obj) {
        for (EncoderEntry entry : this.encoderEntries) {
            if (!entry.getClazz().isAssignableFrom(obj.getClass())) continue;
            return entry.getEncoder();
        }
        return null;
    }

    public final void close() {
        InstanceManager instanceManager = this.wsSession.getInstanceManager();
        for (EncoderEntry entry : this.encoderEntries) {
            entry.getEncoder().destroy();
            if (instanceManager == null) continue;
            try {
                instanceManager.destroyInstance((Object)entry);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                this.log.warn((Object)sm.getString("wsRemoteEndpoint.encoderDestoryFailed", new Object[]{this.encoder.getClass()}), (Throwable)e);
            }
        }
        this.transformation.close();
        this.doClose();
    }

    protected abstract void doWrite(SendHandler var1, long var2, ByteBuffer ... var4);

    protected abstract boolean isMasked();

    protected abstract void doClose();

    protected abstract Lock getLock();

    private static void writeHeader(ByteBuffer headerBuffer, boolean fin, int rsv, byte opCode, boolean masked, ByteBuffer payload, byte[] mask, boolean first) {
        int b = 0;
        if (fin) {
            b = (byte)(b - 128);
        }
        b = (byte)(b + (rsv << 4));
        if (first) {
            b = (byte)(b + opCode);
        }
        headerBuffer.put((byte)b);
        b = masked ? -128 : 0;
        if (payload.remaining() < 126) {
            headerBuffer.put((byte)(payload.remaining() | b));
        } else if (payload.remaining() < 65536) {
            headerBuffer.put((byte)(0x7E | b));
            headerBuffer.put((byte)(payload.remaining() >>> 8));
            headerBuffer.put((byte)(payload.remaining() & 0xFF));
        } else {
            headerBuffer.put((byte)(0x7F | b));
            headerBuffer.put((byte)0);
            headerBuffer.put((byte)0);
            headerBuffer.put((byte)0);
            headerBuffer.put((byte)0);
            headerBuffer.put((byte)(payload.remaining() >>> 24));
            headerBuffer.put((byte)(payload.remaining() >>> 16));
            headerBuffer.put((byte)(payload.remaining() >>> 8));
            headerBuffer.put((byte)(payload.remaining() & 0xFF));
        }
        if (masked) {
            headerBuffer.put(mask[0]);
            headerBuffer.put(mask[1]);
            headerBuffer.put(mask[2]);
            headerBuffer.put(mask[3]);
        }
    }

    private static class StateMachine {
        private State state = State.OPEN;

        private StateMachine() {
        }

        public synchronized void streamStart() {
            this.checkState(State.OPEN);
            this.state = State.STREAM_WRITING;
        }

        public synchronized void writeStart() {
            this.checkState(State.OPEN);
            this.state = State.WRITER_WRITING;
        }

        public synchronized void binaryPartialStart() {
            this.checkState(State.OPEN, State.BINARY_PARTIAL_READY);
            this.state = State.BINARY_PARTIAL_WRITING;
        }

        public synchronized void binaryStart() {
            this.checkState(State.OPEN);
            this.state = State.BINARY_FULL_WRITING;
        }

        public synchronized void textPartialStart() {
            this.checkState(State.OPEN, State.TEXT_PARTIAL_READY);
            this.state = State.TEXT_PARTIAL_WRITING;
        }

        public synchronized void textStart() {
            this.checkState(State.OPEN);
            this.state = State.TEXT_FULL_WRITING;
        }

        public synchronized void complete(boolean last) {
            if (last) {
                this.checkState(State.TEXT_PARTIAL_WRITING, State.TEXT_FULL_WRITING, State.BINARY_PARTIAL_WRITING, State.BINARY_FULL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
                this.state = State.OPEN;
            } else {
                this.checkState(State.TEXT_PARTIAL_WRITING, State.BINARY_PARTIAL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
                if (this.state == State.TEXT_PARTIAL_WRITING) {
                    this.state = State.TEXT_PARTIAL_READY;
                } else if (this.state == State.BINARY_PARTIAL_WRITING) {
                    this.state = State.BINARY_PARTIAL_READY;
                } else if (this.state == State.WRITER_WRITING || this.state == State.STREAM_WRITING) {
                    // empty if block
                }
            }
        }

        private void checkState(State ... required) {
            for (State state : required) {
                if (this.state != state) continue;
                return;
            }
            throw new IllegalStateException(sm.getString("wsRemoteEndpoint.wrongState", new Object[]{this.state}));
        }
    }

    private static class IntermediateMessageHandler
    implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;

        IntermediateMessageHandler(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        public void onResult(SendResult result) {
            this.endpoint.endMessage(null, result);
        }
    }

    private static class StateUpdateSendHandler
    implements SendHandler {
        private final SendHandler handler;
        private final StateMachine stateMachine;

        StateUpdateSendHandler(SendHandler handler, StateMachine stateMachine) {
            this.handler = handler;
            this.stateMachine = stateMachine;
        }

        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.stateMachine.complete(true);
            }
            this.handler.onResult(result);
        }
    }

    private class TextMessageSendHandler
    implements SendHandler {
        private final SendHandler handler;
        private final CharBuffer message;
        private final boolean isLast;
        private final CharsetEncoder encoder;
        private final ByteBuffer buffer;
        private final WsRemoteEndpointImplBase endpoint;
        private volatile boolean isDone = false;

        TextMessageSendHandler(SendHandler handler, CharBuffer message, boolean isLast, CharsetEncoder encoder, ByteBuffer encoderBuffer, WsRemoteEndpointImplBase endpoint) {
            this.handler = handler;
            this.message = message;
            this.isLast = isLast;
            this.encoder = encoder.reset();
            this.buffer = encoderBuffer;
            this.endpoint = endpoint;
        }

        public void write() {
            this.buffer.clear();
            CoderResult cr = this.encoder.encode(this.message, this.buffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            this.isDone = !cr.isOverflow();
            this.buffer.flip();
            this.endpoint.startMessage((byte)1, this.buffer, this.isDone && this.isLast, this);
        }

        public void onResult(SendResult result) {
            if (this.isDone) {
                this.endpoint.stateMachine.complete(this.isLast);
                this.handler.onResult(result);
            } else if (!result.isOK()) {
                this.handler.onResult(result);
            } else if (WsRemoteEndpointImplBase.this.closed) {
                SendResult sr = new SendResult((Throwable)new IOException(sm.getString("wsRemoteEndpoint.closedDuringMessage")));
                this.handler.onResult(sr);
            } else {
                this.write();
            }
        }
    }

    private static class WsOutputStream
    extends OutputStream {
        private final WsRemoteEndpointImplBase endpoint;
        private final ByteBuffer buffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        WsOutputStream(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void write(int b) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            this.used = true;
            if (this.buffer.remaining() == 0) {
                this.flush();
            }
            this.buffer.put((byte)b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (len == 0) {
                return;
            }
            if (this.buffer.remaining() == 0) {
                this.flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(b, off + written, remaining);
                written += remaining;
                this.flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(b, off + written, len - written);
        }

        @Override
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (this.buffer.position() > 0) {
                this.doWrite(false);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() throws IOException {
            Object object = this.closeLock;
            synchronized (object) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
            }
            this.doWrite(true);
        }

        private void doWrite(boolean last) throws IOException {
            if (this.used) {
                this.buffer.flip();
                this.endpoint.sendMessageBlock((byte)2, this.buffer, last);
            }
            this.endpoint.stateMachine.complete(last);
            this.buffer.clear();
        }
    }

    private static class WsWriter
    extends Writer {
        private final WsRemoteEndpointImplBase endpoint;
        private final CharBuffer buffer = CharBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        WsWriter(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (len == 0) {
                return;
            }
            if (this.buffer.remaining() == 0) {
                this.flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(cbuf, off + written, remaining);
                written += remaining;
                this.flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(cbuf, off + written, len - written);
        }

        @Override
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (this.buffer.position() > 0) {
                this.doWrite(false);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() throws IOException {
            Object object = this.closeLock;
            synchronized (object) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
            }
            this.doWrite(true);
        }

        private void doWrite(boolean last) throws IOException {
            if (this.used) {
                this.buffer.flip();
                this.endpoint.sendMessageBlock(this.buffer, last);
                this.buffer.clear();
            } else {
                this.endpoint.stateMachine.complete(last);
            }
        }
    }

    private static class BlockingSendHandler
    implements SendHandler {
        private volatile SendResult sendResult = null;

        private BlockingSendHandler() {
        }

        public void onResult(SendResult result) {
            this.sendResult = result;
        }

        public SendResult getSendResult() {
            return this.sendResult;
        }
    }

    private static class EndMessageHandler
    implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;
        private final SendHandler handler;

        EndMessageHandler(WsRemoteEndpointImplBase endpoint, SendHandler handler) {
            this.endpoint = endpoint;
            this.handler = handler;
        }

        public void onResult(SendResult result) {
            this.endpoint.endMessage(this.handler, result);
        }
    }

    private static class OutputBufferFlushSendHandler
    implements SendHandler {
        private final ByteBuffer outputBuffer;
        private final SendHandler handler;

        OutputBufferFlushSendHandler(ByteBuffer outputBuffer, SendHandler handler) {
            this.outputBuffer = outputBuffer;
            this.handler = handler;
        }

        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.outputBuffer.clear();
            }
            this.handler.onResult(result);
        }
    }

    private static class OutputBufferSendHandler
    implements SendHandler {
        private final SendHandler handler;
        private final long blockingWriteTimeoutExpiry;
        private final ByteBuffer headerBuffer;
        private final ByteBuffer payload;
        private final byte[] mask;
        private final ByteBuffer outputBuffer;
        private final boolean flushRequired;
        private final WsRemoteEndpointImplBase endpoint;
        private volatile int maskIndex = 0;

        OutputBufferSendHandler(SendHandler completion, long blockingWriteTimeoutExpiry, ByteBuffer headerBuffer, ByteBuffer payload, byte[] mask, ByteBuffer outputBuffer, boolean flushRequired, WsRemoteEndpointImplBase endpoint) {
            this.blockingWriteTimeoutExpiry = blockingWriteTimeoutExpiry;
            this.handler = completion;
            this.headerBuffer = headerBuffer;
            this.payload = payload;
            this.mask = mask;
            this.outputBuffer = outputBuffer;
            this.flushRequired = flushRequired;
            this.endpoint = endpoint;
        }

        public void write() {
            while (this.headerBuffer.hasRemaining() && this.outputBuffer.hasRemaining()) {
                this.outputBuffer.put(this.headerBuffer.get());
            }
            if (this.headerBuffer.hasRemaining()) {
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                return;
            }
            int payloadLeft = this.payload.remaining();
            int payloadLimit = this.payload.limit();
            int outputSpace = this.outputBuffer.remaining();
            int toWrite = payloadLeft;
            if (payloadLeft > outputSpace) {
                toWrite = outputSpace;
                this.payload.limit(this.payload.position() + toWrite);
            }
            if (this.mask == null) {
                this.outputBuffer.put(this.payload);
            } else {
                for (int i = 0; i < toWrite; ++i) {
                    this.outputBuffer.put((byte)(this.payload.get() ^ this.mask[this.maskIndex++] & 0xFF));
                    if (this.maskIndex <= 3) continue;
                    this.maskIndex = 0;
                }
            }
            if (payloadLeft > outputSpace) {
                this.payload.limit(payloadLimit);
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                return;
            }
            if (this.flushRequired) {
                this.outputBuffer.flip();
                if (this.outputBuffer.remaining() == 0) {
                    this.handler.onResult(SENDRESULT_OK);
                } else {
                    this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                }
            } else {
                this.handler.onResult(SENDRESULT_OK);
            }
        }

        public void onResult(SendResult result) {
            if (result.isOK()) {
                if (this.outputBuffer.hasRemaining()) {
                    this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                } else {
                    this.outputBuffer.clear();
                    this.write();
                }
            } else {
                this.handler.onResult(result);
            }
        }
    }

    private static class EncoderEntry {
        private final Class<?> clazz;
        private final Encoder encoder;

        EncoderEntry(Class<?> clazz, Encoder encoder) {
            this.clazz = clazz;
            this.encoder = encoder;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public Encoder getEncoder() {
            return this.encoder;
        }
    }

    private static enum State {
        OPEN,
        STREAM_WRITING,
        WRITER_WRITING,
        BINARY_PARTIAL_WRITING,
        BINARY_PARTIAL_READY,
        BINARY_FULL_WRITING,
        TEXT_PARTIAL_WRITING,
        TEXT_PARTIAL_READY,
        TEXT_FULL_WRITING;

    }
}

