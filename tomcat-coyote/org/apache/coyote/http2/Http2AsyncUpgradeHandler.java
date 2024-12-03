/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.WebConnection
 */
package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.WebConnection;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http2.AbstractNonZeroStream;
import org.apache.coyote.http2.ByteUtil;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.Http2AsyncParser;
import org.apache.coyote.http2.Http2Parser;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.coyote.http2.SendfileData;
import org.apache.coyote.http2.Stream;
import org.apache.coyote.http2.StreamException;
import org.apache.coyote.http2.StreamStateMachine;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketWrapperBase;

public class Http2AsyncUpgradeHandler
extends Http2UpgradeHandler {
    private static final ByteBuffer[] BYTEBUFFER_ARRAY = new ByteBuffer[0];
    private final Lock headerWriteLock = new ReentrantLock();
    private final Lock sendResetLock = new ReentrantLock();
    private final AtomicReference<Throwable> error = new AtomicReference();
    private final AtomicReference<IOException> applicationIOE = new AtomicReference();
    private final CompletionHandler<Long, Void> errorCompletion = new CompletionHandler<Long, Void>(){

        @Override
        public void completed(Long result, Void attachment) {
        }

        @Override
        public void failed(Throwable t, Void attachment) {
            Http2AsyncUpgradeHandler.this.error.set(t);
        }
    };
    private final CompletionHandler<Long, Void> applicationErrorCompletion = new CompletionHandler<Long, Void>(){

        @Override
        public void completed(Long result, Void attachment) {
        }

        @Override
        public void failed(Throwable t, Void attachment) {
            if (t instanceof IOException) {
                Http2AsyncUpgradeHandler.this.applicationIOE.set((IOException)t);
            }
            Http2AsyncUpgradeHandler.this.error.set(t);
        }
    };

    public Http2AsyncUpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest) {
        super(protocol, adapter, coyoteRequest);
    }

    @Override
    protected Http2Parser getParser(String connectionId) {
        return new Http2AsyncParser(connectionId, this, this, this.socketWrapper, this);
    }

    @Override
    protected Http2UpgradeHandler.PingManager getPingManager() {
        return new AsyncPingManager();
    }

    @Override
    public boolean hasAsyncIO() {
        return true;
    }

    @Override
    protected void processConnection(WebConnection webConnection, Stream stream) {
    }

    void processConnectionCallback(WebConnection webConnection, Stream stream) {
        super.processConnection(webConnection, stream);
    }

    @Override
    protected void writeSettings() {
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(this.localSettings.getSettingsFrameForPending()), ByteBuffer.wrap(this.createWindowUpdateForSettings()));
        Throwable err = this.error.get();
        if (err != null) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", new Object[]{this.connectionId});
            if (log.isDebugEnabled()) {
                log.debug((Object)msg);
            }
            throw new ProtocolException(msg, err);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void sendStreamReset(StreamStateMachine state, StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.rst.debug", new Object[]{this.connectionId, Integer.toString(se.getStreamId()), se.getError(), se.getMessage()}));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.sendResetLock.lock();
        try {
            if (state != null) {
                boolean active = state.isActive();
                state.sendReset();
                if (active) {
                    this.decrementActiveRemoteStreamCount();
                }
            }
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(rstFrame));
        }
        finally {
            this.sendResetLock.unlock();
        }
        this.handleAsyncException();
    }

    @Override
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
        if (debugMsg != null) {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload), ByteBuffer.wrap(debugMsg));
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload));
        }
        this.handleAsyncException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        this.headerWriteLock.lock();
        try {
            AsyncHeaderFrameBuffers headerFrameBuffers = (AsyncHeaderFrameBuffers)this.doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
            if (headerFrameBuffers != null) {
                this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, headerFrameBuffers.bufs.toArray(BYTEBUFFER_ARRAY));
                this.handleAsyncException();
            }
        }
        finally {
            this.headerWriteLock.unlock();
        }
        if (endOfStream) {
            this.sentEndOfStream(stream);
        }
    }

    @Override
    protected Http2UpgradeHandler.HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new AsyncHeaderFrameBuffers(initialPayloadSize);
    }

    @Override
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
            int orgLimit = data.limit();
            data.limit(data.position() + len);
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, ByteBuffer.wrap(header), data);
            data.limit(orgLimit);
            this.handleAsyncException();
        }
    }

    @Override
    void writeWindowUpdate(AbstractNonZeroStream stream, int increment, boolean applicationInitiated) throws IOException {
        int streamIncrement;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("upgradeHandler.windowUpdateConnection", new Object[]{this.getConnectionId(), increment}));
        }
        byte[] frame = new byte[13];
        ByteUtil.setThreeBytes(frame, 0, 4);
        frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
        ByteUtil.set31Bits(frame, 9, increment);
        boolean neetToWriteConnectionUpdate = true;
        if (stream instanceof Stream && ((Stream)stream).canWrite() && (streamIncrement = ((Stream)stream).getWindowUpdateSizeToWrite(increment)) > 0) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeHandler.windowUpdateStream", new Object[]{this.getConnectionId(), this.getIdAsString(), streamIncrement}));
            }
            byte[] frame2 = new byte[13];
            ByteUtil.setThreeBytes(frame2, 0, 4);
            frame2[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(frame2, 9, streamIncrement);
            ByteUtil.set31Bits(frame2, 5, stream.getIdAsInt());
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(frame), ByteBuffer.wrap(frame2));
            neetToWriteConnectionUpdate = false;
        }
        if (neetToWriteConnectionUpdate) {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(frame));
        }
        this.handleAsyncException();
    }

    @Override
    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn((Object)sm.getString("upgradeHandler.unexpectedAck", new Object[]{this.connectionId, this.getIdAsString()}));
            }
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(SETTINGS_ACK));
        }
        this.handleAsyncException();
    }

    private void handleAsyncException() throws IOException {
        IOException ioe = this.applicationIOE.getAndSet(null);
        if (ioe != null) {
            this.handleAppInitiatedIOException(ioe);
        } else {
            Throwable err = this.error.getAndSet(null);
            if (err != null) {
                if (err instanceof IOException) {
                    throw (IOException)err;
                }
                throw new IOException(err);
            }
        }
    }

    @Override
    protected SendfileState processSendfile(SendfileData sendfile) {
        if (sendfile != null) {
            int frameSize;
            try {
                try (FileChannel channel = FileChannel.open(sendfile.path, StandardOpenOption.READ);){
                    sendfile.mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, sendfile.pos, sendfile.end - sendfile.pos);
                }
                int reservation = sendfile.end - sendfile.pos > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)(sendfile.end - sendfile.pos);
                sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                sendfile.connectionReservation = this.reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
            }
            catch (IOException e) {
                return SendfileState.ERROR;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeHandler.sendfile.reservation", new Object[]{this.connectionId, sendfile.stream.getIdAsString(), sendfile.connectionReservation, sendfile.streamReservation}));
            }
            boolean finished = (long)(frameSize = Integer.min(this.getMaxFrameSize(), sendfile.connectionReservation)) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
            boolean writable = sendfile.stream.canWrite();
            byte[] header = new byte[9];
            ByteUtil.setThreeBytes(header, 0, frameSize);
            header[3] = FrameType.DATA.getIdByte();
            if (finished) {
                header[4] = 1;
                this.sentEndOfStream(sendfile.stream);
            }
            if (writable) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("upgradeHandler.writeBody", new Object[]{this.connectionId, sendfile.stream.getIdAsString(), Integer.toString(frameSize), finished}));
                }
                ByteUtil.set31Bits(header, 5, sendfile.stream.getIdAsInt());
                sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, new SendfileCompletionHandler(), ByteBuffer.wrap(header), sendfile.mappedBuffer);
                try {
                    this.handleAsyncException();
                }
                catch (IOException e) {
                    return SendfileState.ERROR;
                }
            }
            return SendfileState.PENDING;
        }
        return SendfileState.DONE;
    }

    protected class AsyncPingManager
    extends Http2UpgradeHandler.PingManager {
        protected AsyncPingManager() {
            super(Http2AsyncUpgradeHandler.this);
        }

        @Override
        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                int sentSequence = ++this.sequence;
                Http2UpgradeHandler.PingRecord pingRecord = new Http2UpgradeHandler.PingRecord(sentSequence, now);
                this.inflightPings.add(pingRecord);
                ByteUtil.set31Bits(payload, 4, sentSequence);
                Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING), ByteBuffer.wrap(payload));
                Http2AsyncUpgradeHandler.this.handleAsyncException();
            }
        }

        @Override
        public void receivePing(byte[] payload, boolean ack) throws IOException {
            if (ack) {
                super.receivePing(payload, ack);
            } else {
                Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING_ACK), ByteBuffer.wrap(payload));
                Http2AsyncUpgradeHandler.this.handleAsyncException();
            }
        }
    }

    private static class AsyncHeaderFrameBuffers
    implements Http2UpgradeHandler.HeaderFrameBuffers {
        int payloadSize;
        private byte[] header;
        private ByteBuffer payload;
        private final List<ByteBuffer> bufs = new ArrayList<ByteBuffer>();

        AsyncHeaderFrameBuffers(int initialPayloadSize) {
            this.payloadSize = initialPayloadSize;
        }

        @Override
        public void startFrame() {
            this.header = new byte[9];
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }

        @Override
        public void endFrame() throws IOException {
            this.bufs.add(ByteBuffer.wrap(this.header));
            this.bufs.add(this.payload);
        }

        @Override
        public void endHeaders() throws IOException {
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
            this.payloadSize *= 2;
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }
    }

    protected class SendfileCompletionHandler
    implements CompletionHandler<Long, SendfileData> {
        protected SendfileCompletionHandler() {
        }

        @Override
        public void completed(Long nBytes, SendfileData sendfile) {
            SocketWrapperBase.CompletionState completionState = null;
            long bytesWritten = nBytes - 9L;
            do {
                int frameSize;
                sendfile.left -= bytesWritten;
                if (sendfile.left == 0L) {
                    try {
                        sendfile.stream.getOutputBuffer().end();
                    }
                    catch (IOException e) {
                        this.failed((Throwable)e, sendfile);
                    }
                    return;
                }
                sendfile.streamReservation = (int)((long)sendfile.streamReservation - bytesWritten);
                sendfile.connectionReservation = (int)((long)sendfile.connectionReservation - bytesWritten);
                sendfile.pos += bytesWritten;
                try {
                    if (sendfile.connectionReservation == 0) {
                        if (sendfile.streamReservation == 0) {
                            int reservation = sendfile.end - sendfile.pos > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)(sendfile.end - sendfile.pos);
                            sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                        }
                        sendfile.connectionReservation = Http2AsyncUpgradeHandler.this.reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
                    }
                }
                catch (IOException e) {
                    this.failed((Throwable)e, sendfile);
                    return;
                }
                if (Http2UpgradeHandler.log.isDebugEnabled()) {
                    Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.sendfile.reservation", new Object[]{Http2AsyncUpgradeHandler.this.connectionId, sendfile.stream.getIdAsString(), sendfile.connectionReservation, sendfile.streamReservation}));
                }
                boolean finished = (long)(frameSize = Integer.min(Http2AsyncUpgradeHandler.this.getMaxFrameSize(), sendfile.connectionReservation)) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
                boolean writable = sendfile.stream.canWrite();
                byte[] header = new byte[9];
                ByteUtil.setThreeBytes(header, 0, frameSize);
                header[3] = FrameType.DATA.getIdByte();
                if (finished) {
                    header[4] = 1;
                    Http2AsyncUpgradeHandler.this.sentEndOfStream(sendfile.stream);
                }
                if (writable) {
                    if (Http2UpgradeHandler.log.isDebugEnabled()) {
                        Http2UpgradeHandler.log.debug((Object)Http2UpgradeHandler.sm.getString("upgradeHandler.writeBody", new Object[]{Http2AsyncUpgradeHandler.this.connectionId, sendfile.stream.getIdAsString(), Integer.toString(frameSize), finished}));
                    }
                    ByteUtil.set31Bits(header, 5, sendfile.stream.getIdAsInt());
                    sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                    completionState = Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE, this, ByteBuffer.wrap(header), sendfile.mappedBuffer);
                    try {
                        Http2AsyncUpgradeHandler.this.handleAsyncException();
                    }
                    catch (IOException e) {
                        this.failed((Throwable)e, sendfile);
                        return;
                    }
                }
                bytesWritten = frameSize;
            } while (completionState == SocketWrapperBase.CompletionState.INLINE);
        }

        @Override
        public void failed(Throwable t, SendfileData sendfile) {
            Http2AsyncUpgradeHandler.this.applicationErrorCompletion.failed(t, null);
        }
    }
}

