/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.Extension
 *  javax.websocket.MessageHandler
 *  javax.websocket.MessageHandler$Partial
 *  javax.websocket.MessageHandler$Whole
 *  javax.websocket.PongMessage
 *  javax.websocket.Session
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.Utf8Decoder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.Utf8Decoder;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.MessagePart;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.TransformationResult;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WrappedMessageHandler;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.websocket.WsPongMessage;
import org.apache.tomcat.websocket.WsSession;

public abstract class WsFrameBase {
    private static final StringManager sm = StringManager.getManager(WsFrameBase.class);
    protected final WsSession wsSession;
    protected final ByteBuffer inputBuffer;
    private final Transformation transformation;
    private final ByteBuffer controlBufferBinary = ByteBuffer.allocate(125);
    private final CharBuffer controlBufferText = CharBuffer.allocate(125);
    private final CharsetDecoder utf8DecoderControl = new Utf8Decoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    private final CharsetDecoder utf8DecoderMessage = new Utf8Decoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    private boolean continuationExpected = false;
    private boolean textMessage = false;
    private ByteBuffer messageBufferBinary;
    private CharBuffer messageBufferText;
    private MessageHandler binaryMsgHandler = null;
    private MessageHandler textMsgHandler = null;
    private boolean fin = false;
    private int rsv = 0;
    private byte opCode = 0;
    private final byte[] mask = new byte[4];
    private int maskIndex = 0;
    private long payloadLength = 0L;
    private volatile long payloadWritten = 0L;
    private volatile State state = State.NEW_FRAME;
    private volatile boolean open = true;
    private static final AtomicReferenceFieldUpdater<WsFrameBase, ReadState> READ_STATE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(WsFrameBase.class, ReadState.class, "readState");
    private volatile ReadState readState = ReadState.WAITING;

    public WsFrameBase(WsSession wsSession, Transformation transformation) {
        this.inputBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        this.inputBuffer.position(0).limit(0);
        this.messageBufferBinary = ByteBuffer.allocate(wsSession.getMaxBinaryMessageBufferSize());
        this.messageBufferText = CharBuffer.allocate(wsSession.getMaxTextMessageBufferSize());
        wsSession.setWsFrame(this);
        this.wsSession = wsSession;
        TerminalTransformation finalTransformation = this.isMasked() ? new UnmaskTransformation() : new NoopTransformation();
        if (transformation == null) {
            this.transformation = finalTransformation;
        } else {
            transformation.setNext(finalTransformation);
            this.transformation = transformation;
        }
    }

    protected void processInputBuffer() throws IOException {
        while (!this.isSuspended()) {
            this.wsSession.updateLastActiveRead();
            if (this.state == State.NEW_FRAME) {
                if (!this.processInitialHeader()) break;
                if (!this.open) {
                    throw new IOException(sm.getString("wsFrame.closed"));
                }
            }
            if ((this.state != State.PARTIAL_HEADER || this.processRemainingHeader()) && (this.state != State.DATA || this.processData())) continue;
            break;
        }
    }

    private boolean processInitialHeader() throws IOException {
        if (this.inputBuffer.remaining() < 2) {
            return false;
        }
        byte b = this.inputBuffer.get();
        this.fin = (b & 0x80) != 0;
        this.rsv = (b & 0x70) >>> 4;
        this.opCode = (byte)(b & 0xF);
        if (!this.transformation.validateRsv(this.rsv, this.opCode)) {
            throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.wrongRsv", new Object[]{this.rsv, (int)this.opCode})));
        }
        if (Util.isControl(this.opCode)) {
            if (!this.fin) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlFragmented")));
            }
            if (this.opCode != 9 && this.opCode != 10 && this.opCode != 8) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", new Object[]{(int)this.opCode})));
            }
        } else {
            block17: {
                if (this.continuationExpected) {
                    if (!Util.isContinuation(this.opCode)) {
                        throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.noContinuation")));
                    }
                } else {
                    try {
                        if (this.opCode == 2) {
                            this.textMessage = false;
                            int size = this.wsSession.getMaxBinaryMessageBufferSize();
                            if (size != this.messageBufferBinary.capacity()) {
                                this.messageBufferBinary = ByteBuffer.allocate(size);
                            }
                            this.binaryMsgHandler = this.wsSession.getBinaryMessageHandler();
                            this.textMsgHandler = null;
                            break block17;
                        }
                        if (this.opCode == 1) {
                            this.textMessage = true;
                            int size = this.wsSession.getMaxTextMessageBufferSize();
                            if (size != this.messageBufferText.capacity()) {
                                this.messageBufferText = CharBuffer.allocate(size);
                            }
                            this.binaryMsgHandler = null;
                            this.textMsgHandler = this.wsSession.getTextMessageHandler();
                            break block17;
                        }
                        throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", new Object[]{(int)this.opCode})));
                    }
                    catch (IllegalStateException ise) {
                        throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.sessionClosed")));
                    }
                }
            }
            boolean bl = this.continuationExpected = !this.fin;
        }
        if (((b = this.inputBuffer.get()) & 0x80) == 0 && this.isMasked()) {
            throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.notMasked")));
        }
        this.payloadLength = b & 0x7F;
        this.state = State.PARTIAL_HEADER;
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)sm.getString("wsFrame.partialHeaderComplete", new Object[]{Boolean.toString(this.fin), Integer.toString(this.rsv), Integer.toString(this.opCode), Long.toString(this.payloadLength)}));
        }
        return true;
    }

    protected abstract boolean isMasked();

    protected abstract Log getLog();

    private boolean processRemainingHeader() throws IOException {
        int headerLength = this.isMasked() ? 4 : 0;
        if (this.payloadLength == 126L) {
            headerLength += 2;
        } else if (this.payloadLength == 127L) {
            headerLength += 8;
        }
        if (this.inputBuffer.remaining() < headerLength) {
            return false;
        }
        if (this.payloadLength == 126L) {
            this.payloadLength = WsFrameBase.byteArrayToLong(this.inputBuffer.array(), this.inputBuffer.arrayOffset() + this.inputBuffer.position(), 2);
            this.inputBuffer.position(this.inputBuffer.position() + 2);
        } else if (this.payloadLength == 127L) {
            this.payloadLength = WsFrameBase.byteArrayToLong(this.inputBuffer.array(), this.inputBuffer.arrayOffset() + this.inputBuffer.position(), 8);
            if (this.payloadLength < 0L) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.payloadMsbInvalid")));
            }
            this.inputBuffer.position(this.inputBuffer.position() + 8);
        }
        if (Util.isControl(this.opCode)) {
            if (this.payloadLength > 125L) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlPayloadTooBig", new Object[]{this.payloadLength})));
            }
            if (!this.fin) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlNoFin")));
            }
        }
        if (this.isMasked()) {
            this.inputBuffer.get(this.mask, 0, 4);
        }
        this.state = State.DATA;
        return true;
    }

    private boolean processData() throws IOException {
        boolean result = Util.isControl(this.opCode) ? this.processDataControl() : (this.textMessage ? (this.textMsgHandler == null ? this.swallowInput() : this.processDataText()) : (this.binaryMsgHandler == null ? this.swallowInput() : this.processDataBinary()));
        if (result) {
            this.updateStats(this.payloadLength);
        }
        this.checkRoomPayload();
        return result;
    }

    protected void updateStats(long payloadLength) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean processDataControl() throws IOException {
        TransformationResult tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.controlBufferBinary);
        if (TransformationResult.UNDERFLOW.equals((Object)tr)) {
            return false;
        }
        this.controlBufferBinary.flip();
        if (this.opCode == 8) {
            this.open = false;
            String reason = null;
            int code = CloseReason.CloseCodes.NORMAL_CLOSURE.getCode();
            if (this.controlBufferBinary.remaining() == 1) {
                this.controlBufferBinary.clear();
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.oneByteCloseCode")));
            }
            if (this.controlBufferBinary.remaining() > 1) {
                code = this.controlBufferBinary.getShort();
                if (this.controlBufferBinary.remaining() > 0) {
                    CoderResult cr = this.utf8DecoderControl.decode(this.controlBufferBinary, this.controlBufferText, true);
                    if (cr.isError()) {
                        this.controlBufferBinary.clear();
                        this.controlBufferText.clear();
                        throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidUtf8Close")));
                    }
                    this.controlBufferText.flip();
                    reason = this.controlBufferText.toString();
                }
            }
            this.wsSession.onClose(new CloseReason(Util.getCloseCode(code), reason));
        } else if (this.opCode == 9) {
            if (this.wsSession.isOpen()) {
                this.wsSession.getBasicRemote().sendPong(this.controlBufferBinary);
            }
        } else if (this.opCode == 10) {
            MessageHandler.Whole<PongMessage> mhPong = this.wsSession.getPongMessageHandler();
            if (mhPong != null) {
                try {
                    mhPong.onMessage((Object)new WsPongMessage(this.controlBufferBinary));
                }
                catch (Throwable t) {
                    this.handleThrowableOnSend(t);
                }
                finally {
                    this.controlBufferBinary.clear();
                }
            }
        } else {
            this.controlBufferBinary.clear();
            throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", new Object[]{(int)this.opCode})));
        }
        this.controlBufferBinary.clear();
        this.newFrame();
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendMessageText(boolean last) throws WsIOException {
        long maxMessageSize;
        if (this.textMsgHandler instanceof WrappedMessageHandler && (maxMessageSize = ((WrappedMessageHandler)this.textMsgHandler).getMaxMessageSize()) > -1L && (long)this.messageBufferText.remaining() > maxMessageSize) {
            throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.messageTooBig", new Object[]{(long)this.messageBufferText.remaining(), maxMessageSize})));
        }
        try {
            if (this.textMsgHandler instanceof MessageHandler.Partial) {
                ((MessageHandler.Partial)this.textMsgHandler).onMessage((Object)this.messageBufferText.toString(), last);
            } else {
                ((MessageHandler.Whole)this.textMsgHandler).onMessage((Object)this.messageBufferText.toString());
            }
        }
        catch (Throwable t) {
            this.handleThrowableOnSend(t);
        }
        finally {
            this.messageBufferText.clear();
        }
    }

    private boolean processDataText() throws IOException {
        TransformationResult tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
        while (!TransformationResult.END_OF_FRAME.equals((Object)tr)) {
            this.messageBufferBinary.flip();
            while (true) {
                CoderResult cr;
                if ((cr = this.utf8DecoderMessage.decode(this.messageBufferBinary, this.messageBufferText, false)).isError()) {
                    throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.NOT_CONSISTENT, sm.getString("wsFrame.invalidUtf8")));
                }
                if (cr.isOverflow()) {
                    if (this.usePartial()) {
                        this.messageBufferText.flip();
                        this.sendMessageText(false);
                        this.messageBufferText.clear();
                        continue;
                    }
                    throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.textMessageTooBig")));
                }
                if (cr.isUnderflow()) break;
            }
            this.messageBufferBinary.compact();
            if (!TransformationResult.OVERFLOW.equals((Object)tr)) {
                return false;
            }
            tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
        }
        this.messageBufferBinary.flip();
        boolean last = false;
        while (true) {
            CoderResult cr;
            if ((cr = this.utf8DecoderMessage.decode(this.messageBufferBinary, this.messageBufferText, last)).isError()) {
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.NOT_CONSISTENT, sm.getString("wsFrame.invalidUtf8")));
            }
            if (cr.isOverflow()) {
                if (this.usePartial()) {
                    this.messageBufferText.flip();
                    this.sendMessageText(false);
                    this.messageBufferText.clear();
                    continue;
                }
                throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.textMessageTooBig")));
            }
            if (!cr.isUnderflow() || last) break;
            if (this.continuationExpected) {
                if (this.usePartial()) {
                    this.messageBufferText.flip();
                    this.sendMessageText(false);
                    this.messageBufferText.clear();
                }
                this.messageBufferBinary.compact();
                this.newFrame();
                return true;
            }
            last = true;
        }
        this.messageBufferText.flip();
        this.sendMessageText(true);
        this.newMessage();
        return true;
    }

    private boolean processDataBinary() throws IOException {
        ByteBuffer copy;
        TransformationResult tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
        while (!TransformationResult.END_OF_FRAME.equals((Object)tr)) {
            if (TransformationResult.UNDERFLOW.equals((Object)tr)) {
                return false;
            }
            if (!this.usePartial()) {
                CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.bufferTooSmall", new Object[]{this.messageBufferBinary.capacity(), this.payloadLength}));
                throw new WsIOException(cr);
            }
            this.messageBufferBinary.flip();
            copy = ByteBuffer.allocate(this.messageBufferBinary.limit());
            copy.put(this.messageBufferBinary);
            copy.flip();
            this.sendMessageBinary(copy, false);
            this.messageBufferBinary.clear();
            tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
        }
        if (this.usePartial() || !this.continuationExpected) {
            this.messageBufferBinary.flip();
            copy = ByteBuffer.allocate(this.messageBufferBinary.limit());
            copy.put(this.messageBufferBinary);
            copy.flip();
            this.sendMessageBinary(copy, !this.continuationExpected);
            this.messageBufferBinary.clear();
        }
        if (this.continuationExpected) {
            this.newFrame();
        } else {
            this.newMessage();
        }
        return true;
    }

    private void handleThrowableOnSend(Throwable t) throws WsIOException {
        ExceptionUtils.handleThrowable((Throwable)t);
        this.wsSession.getLocal().onError((Session)this.wsSession, t);
        CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, sm.getString("wsFrame.ioeTriggeredClose"));
        throw new WsIOException(cr);
    }

    protected void sendMessageBinary(ByteBuffer msg, boolean last) throws WsIOException {
        long maxMessageSize;
        if (this.binaryMsgHandler instanceof WrappedMessageHandler && (maxMessageSize = ((WrappedMessageHandler)this.binaryMsgHandler).getMaxMessageSize()) > -1L && (long)msg.remaining() > maxMessageSize) {
            throw new WsIOException(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.messageTooBig", new Object[]{(long)msg.remaining(), maxMessageSize})));
        }
        try {
            if (this.binaryMsgHandler instanceof MessageHandler.Partial) {
                ((MessageHandler.Partial)this.binaryMsgHandler).onMessage((Object)msg, last);
            } else {
                ((MessageHandler.Whole)this.binaryMsgHandler).onMessage((Object)msg);
            }
        }
        catch (Throwable t) {
            this.handleThrowableOnSend(t);
        }
    }

    private void newMessage() {
        this.messageBufferBinary.clear();
        this.messageBufferText.clear();
        this.utf8DecoderMessage.reset();
        this.continuationExpected = false;
        this.newFrame();
    }

    private void newFrame() {
        if (this.inputBuffer.remaining() == 0) {
            this.inputBuffer.position(0).limit(0);
        }
        this.maskIndex = 0;
        this.payloadWritten = 0L;
        this.state = State.NEW_FRAME;
        this.checkRoomHeaders();
    }

    private void checkRoomHeaders() {
        if (this.inputBuffer.capacity() - this.inputBuffer.position() < 131) {
            this.makeRoom();
        }
    }

    private void checkRoomPayload() {
        if ((long)(this.inputBuffer.capacity() - this.inputBuffer.position()) - this.payloadLength + this.payloadWritten < 0L) {
            this.makeRoom();
        }
    }

    private void makeRoom() {
        this.inputBuffer.compact();
        this.inputBuffer.flip();
    }

    private boolean usePartial() {
        if (Util.isControl(this.opCode)) {
            return false;
        }
        if (this.textMessage) {
            return this.textMsgHandler instanceof MessageHandler.Partial;
        }
        return this.binaryMsgHandler instanceof MessageHandler.Partial;
    }

    private boolean swallowInput() {
        long toSkip = Math.min(this.payloadLength - this.payloadWritten, (long)this.inputBuffer.remaining());
        this.inputBuffer.position(this.inputBuffer.position() + (int)toSkip);
        this.payloadWritten += toSkip;
        if (this.payloadWritten == this.payloadLength) {
            if (this.continuationExpected) {
                this.newFrame();
            } else {
                this.newMessage();
            }
            return true;
        }
        return false;
    }

    protected static long byteArrayToLong(byte[] b, int start, int len) throws IOException {
        if (len > 8) {
            throw new IOException(sm.getString("wsFrame.byteToLongFail", new Object[]{(long)len}));
        }
        int shift = 0;
        long result = 0L;
        for (int i = start + len - 1; i >= start; --i) {
            result += ((long)b[i] & 0xFFL) << shift;
            shift += 8;
        }
        return result;
    }

    protected boolean isOpen() {
        return this.open;
    }

    protected Transformation getTransformation() {
        return this.transformation;
    }

    /*
     * Unable to fully structure code
     */
    public void suspend() {
        block8: while (true) {
            switch (1.$SwitchMap$org$apache$tomcat$websocket$WsFrameBase$ReadState[this.readState.ordinal()]) {
                case 1: {
                    if (!WsFrameBase.READ_STATE_UPDATER.compareAndSet(this, ReadState.WAITING, ReadState.SUSPENDING_WAIT)) continue block8;
                    return;
                }
                case 2: {
                    if (!WsFrameBase.READ_STATE_UPDATER.compareAndSet(this, ReadState.PROCESSING, ReadState.SUSPENDING_PROCESS)) continue block8;
                    return;
                }
                case 3: {
                    if (this.readState != ReadState.SUSPENDING_WAIT) continue block8;
                    if (this.getLog().isWarnEnabled()) {
                        this.getLog().warn((Object)WsFrameBase.sm.getString("wsFrame.suspendRequested"));
                    }
                    return;
                }
                case 4: {
                    if (this.readState != ReadState.SUSPENDING_PROCESS) continue block8;
                    if (this.getLog().isWarnEnabled()) {
                        this.getLog().warn((Object)WsFrameBase.sm.getString("wsFrame.suspendRequested"));
                    }
                    return;
                }
                case 5: {
                    if (this.readState == ReadState.SUSPENDED) ** break;
                    continue block8;
                    if (this.getLog().isWarnEnabled()) {
                        this.getLog().warn((Object)WsFrameBase.sm.getString("wsFrame.alreadySuspended"));
                    }
                    return;
                }
                case 6: {
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException(WsFrameBase.sm.getString("wsFrame.illegalReadState", new Object[]{this.state}));
    }

    /*
     * Unable to fully structure code
     */
    public void resume() {
        block8: while (true) {
            switch (1.$SwitchMap$org$apache$tomcat$websocket$WsFrameBase$ReadState[this.readState.ordinal()]) {
                case 1: {
                    if (this.readState != ReadState.WAITING) continue block8;
                    if (this.getLog().isWarnEnabled()) {
                        this.getLog().warn((Object)WsFrameBase.sm.getString("wsFrame.alreadyResumed"));
                    }
                    return;
                }
                case 2: {
                    if (this.readState != ReadState.PROCESSING) continue block8;
                    if (this.getLog().isWarnEnabled()) {
                        this.getLog().warn((Object)WsFrameBase.sm.getString("wsFrame.alreadyResumed"));
                    }
                    return;
                }
                case 3: {
                    if (!WsFrameBase.READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDING_WAIT, ReadState.WAITING)) continue block8;
                    return;
                }
                case 4: {
                    if (!WsFrameBase.READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDING_PROCESS, ReadState.PROCESSING)) continue block8;
                    return;
                }
                case 5: {
                    if (WsFrameBase.READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDED, ReadState.WAITING)) ** break;
                    continue block8;
                    this.resumeProcessing();
                    return;
                }
                case 6: {
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException(WsFrameBase.sm.getString("wsFrame.illegalReadState", new Object[]{this.state}));
    }

    protected boolean isSuspended() {
        return this.readState.isSuspended();
    }

    protected ReadState getReadState() {
        return this.readState;
    }

    protected void changeReadState(ReadState newState) {
        READ_STATE_UPDATER.set(this, newState);
    }

    protected boolean changeReadState(ReadState oldState, ReadState newState) {
        return READ_STATE_UPDATER.compareAndSet(this, oldState, newState);
    }

    protected abstract void resumeProcessing();

    private static enum State {
        NEW_FRAME,
        PARTIAL_HEADER,
        DATA;

    }

    protected static enum ReadState {
        WAITING(false),
        PROCESSING(false),
        SUSPENDING_WAIT(true),
        SUSPENDING_PROCESS(true),
        SUSPENDED(true),
        CLOSING(false);

        private final boolean isSuspended;

        private ReadState(boolean isSuspended) {
            this.isSuspended = isSuspended;
        }

        public boolean isSuspended() {
            return this.isSuspended;
        }
    }

    private final class UnmaskTransformation
    extends TerminalTransformation {
        private UnmaskTransformation() {
        }

        @Override
        public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) {
            while (WsFrameBase.this.payloadWritten < WsFrameBase.this.payloadLength && WsFrameBase.this.inputBuffer.remaining() > 0 && dest.hasRemaining()) {
                byte b = (byte)((WsFrameBase.this.inputBuffer.get() ^ WsFrameBase.this.mask[WsFrameBase.this.maskIndex]) & 0xFF);
                WsFrameBase.this.maskIndex++;
                if (WsFrameBase.this.maskIndex == 4) {
                    WsFrameBase.this.maskIndex = 0;
                }
                WsFrameBase.this.payloadWritten++;
                dest.put(b);
            }
            if (WsFrameBase.this.payloadWritten == WsFrameBase.this.payloadLength) {
                return TransformationResult.END_OF_FRAME;
            }
            if (WsFrameBase.this.inputBuffer.remaining() == 0) {
                return TransformationResult.UNDERFLOW;
            }
            return TransformationResult.OVERFLOW;
        }

        @Override
        public List<MessagePart> sendMessagePart(List<MessagePart> messageParts) {
            return messageParts;
        }
    }

    private final class NoopTransformation
    extends TerminalTransformation {
        private NoopTransformation() {
        }

        @Override
        public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) {
            long toWrite = Math.min(WsFrameBase.this.payloadLength - WsFrameBase.this.payloadWritten, (long)WsFrameBase.this.inputBuffer.remaining());
            toWrite = Math.min(toWrite, (long)dest.remaining());
            int orgLimit = WsFrameBase.this.inputBuffer.limit();
            WsFrameBase.this.inputBuffer.limit(WsFrameBase.this.inputBuffer.position() + (int)toWrite);
            dest.put(WsFrameBase.this.inputBuffer);
            WsFrameBase.this.inputBuffer.limit(orgLimit);
            WsFrameBase.this.payloadWritten += toWrite;
            if (WsFrameBase.this.payloadWritten == WsFrameBase.this.payloadLength) {
                return TransformationResult.END_OF_FRAME;
            }
            if (WsFrameBase.this.inputBuffer.remaining() == 0) {
                return TransformationResult.UNDERFLOW;
            }
            return TransformationResult.OVERFLOW;
        }

        @Override
        public List<MessagePart> sendMessagePart(List<MessagePart> messageParts) {
            return messageParts;
        }
    }

    private static abstract class TerminalTransformation
    implements Transformation {
        private TerminalTransformation() {
        }

        @Override
        public boolean validateRsvBits(int i) {
            return true;
        }

        @Override
        public Extension getExtensionResponse() {
            return null;
        }

        @Override
        public void setNext(Transformation t) {
        }

        @Override
        public boolean validateRsv(int rsv, byte opCode) {
            return rsv == 0;
        }

        @Override
        public void close() {
        }
    }
}

