/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.WebConnection
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteBufferUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.WebConnection;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.http2.ByteUtil;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.Flags;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.HpackException;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.Setting;
import org.apache.coyote.http2.Stream;
import org.apache.coyote.http2.StreamException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.res.StringManager;

class Http2Parser {
    protected static final Log log = LogFactory.getLog(Http2Parser.class);
    protected static final StringManager sm = StringManager.getManager(Http2Parser.class);
    static final byte[] CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    protected final String connectionId;
    protected final Input input;
    private final Output output;
    private final byte[] frameHeaderBuffer = new byte[9];
    private volatile HpackDecoder hpackDecoder;
    private volatile ByteBuffer headerReadBuffer = ByteBuffer.allocate(1024);
    private volatile int headersCurrentStream = -1;
    private volatile boolean headersEndStream = false;

    Http2Parser(String connectionId, Input input, Output output) {
        this.connectionId = connectionId;
        this.input = input;
        this.output = output;
    }

    @Deprecated
    boolean readFrame(boolean block) throws Http2Exception, IOException {
        return this.readFrame(block, null);
    }

    boolean readFrame() throws Http2Exception, IOException {
        return this.readFrame(false, null);
    }

    protected boolean readFrame(boolean block, FrameType expected) throws IOException, Http2Exception {
        if (!this.input.fill(block, this.frameHeaderBuffer)) {
            return false;
        }
        int payloadSize = ByteUtil.getThreeBytes(this.frameHeaderBuffer, 0);
        int frameTypeId = ByteUtil.getOneByte(this.frameHeaderBuffer, 3);
        FrameType frameType = FrameType.valueOf(frameTypeId);
        int flags = ByteUtil.getOneByte(this.frameHeaderBuffer, 4);
        int streamId = ByteUtil.get31Bits(this.frameHeaderBuffer, 5);
        try {
            this.validateFrame(expected, frameType, streamId, flags, payloadSize);
        }
        catch (StreamException se) {
            this.swallowPayload(streamId, frameTypeId, payloadSize, false, null);
            throw se;
        }
        switch (frameType) {
            case DATA: {
                this.readDataFrame(streamId, flags, payloadSize, null);
                break;
            }
            case HEADERS: {
                this.readHeadersFrame(streamId, flags, payloadSize, null);
                break;
            }
            case PRIORITY: {
                this.readPriorityFrame(streamId, null);
                break;
            }
            case RST: {
                this.readRstFrame(streamId, null);
                break;
            }
            case SETTINGS: {
                this.readSettingsFrame(flags, payloadSize, null);
                break;
            }
            case PUSH_PROMISE: {
                this.readPushPromiseFrame(streamId, flags, payloadSize, null);
                break;
            }
            case PING: {
                this.readPingFrame(flags, null);
                break;
            }
            case GOAWAY: {
                this.readGoawayFrame(payloadSize, null);
                break;
            }
            case WINDOW_UPDATE: {
                this.readWindowUpdateFrame(streamId, null);
                break;
            }
            case CONTINUATION: {
                this.readContinuationFrame(streamId, flags, payloadSize, null);
                break;
            }
            case PRIORITY_UPDATE: {
                this.readPriorityUpdateFrame(payloadSize, null);
                break;
            }
            case UNKNOWN: {
                this.readUnknownFrame(streamId, frameTypeId, flags, payloadSize, null);
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readDataFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        ByteBuffer dest;
        int dataLength;
        int padLength = 0;
        boolean endOfStream = Flags.isEndOfStream(flags);
        if (Flags.hasPadding(flags)) {
            if (buffer == null) {
                byte[] b = new byte[1];
                this.input.fill(true, b);
                padLength = b[0] & 0xFF;
            } else {
                padLength = buffer.get() & 0xFF;
            }
            if (padLength >= payloadSize) {
                throw new ConnectionException(sm.getString("http2Parser.processFrame.tooMuchPadding", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize)}), Http2Error.PROTOCOL_ERROR);
            }
            dataLength = payloadSize - (padLength + 1);
        } else {
            dataLength = payloadSize;
        }
        if (log.isDebugEnabled()) {
            String padding = Flags.hasPadding(flags) ? Integer.toString(padLength) : "none";
            log.debug((Object)sm.getString("http2Parser.processFrameData.lengths", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(dataLength), padding}));
        }
        if ((dest = this.output.startRequestBodyFrame(streamId, payloadSize, endOfStream)) == null) {
            this.swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false, buffer);
            if (Flags.hasPadding(flags)) {
                this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
            }
            if (endOfStream) {
                this.output.receivedEndOfStream(streamId);
            }
        } else {
            ByteBuffer byteBuffer = dest;
            synchronized (byteBuffer) {
                if (dest.remaining() < payloadSize) {
                    this.swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false, buffer);
                    if (Flags.hasPadding(flags)) {
                        this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
                    }
                    throw new StreamException(sm.getString("http2Parser.processFrameData.window", new Object[]{this.connectionId}), Http2Error.FLOW_CONTROL_ERROR, streamId);
                }
                if (buffer == null) {
                    this.input.fill(true, dest, dataLength);
                } else {
                    int oldLimit = buffer.limit();
                    buffer.limit(buffer.position() + dataLength);
                    dest.put(buffer);
                    buffer.limit(oldLimit);
                }
                if (Flags.hasPadding(flags)) {
                    this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
                }
                if (endOfStream) {
                    this.output.receivedEndOfStream(streamId);
                }
                this.output.endRequestBodyFrame(streamId, dataLength);
            }
        }
    }

    protected void readHeadersFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        this.headersEndStream = Flags.isEndOfStream(flags);
        if (this.hpackDecoder == null) {
            this.hpackDecoder = this.output.getHpackDecoder();
        }
        try {
            this.hpackDecoder.setHeaderEmitter(this.output.headersStart(streamId, this.headersEndStream));
        }
        catch (StreamException se) {
            this.swallowPayload(streamId, FrameType.HEADERS.getId(), payloadSize, false, buffer);
            throw se;
        }
        int padLength = 0;
        boolean padding = Flags.hasPadding(flags);
        boolean priority = Flags.hasPriority(flags);
        int optionalLen = 0;
        if (padding) {
            optionalLen = 1;
        }
        if (priority) {
            optionalLen += 5;
        }
        if (optionalLen > 0) {
            byte[] optional = new byte[optionalLen];
            if (buffer == null) {
                this.input.fill(true, optional);
            } else {
                buffer.get(optional);
            }
            if (padding && (padLength = ByteUtil.getOneByte(optional, 0)) >= payloadSize) {
                throw new ConnectionException(sm.getString("http2Parser.processFrame.tooMuchPadding", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize)}), Http2Error.PROTOCOL_ERROR);
            }
            payloadSize -= optionalLen;
            payloadSize -= padLength;
        }
        this.readHeaderPayload(streamId, payloadSize, buffer);
        this.swallowPayload(streamId, FrameType.HEADERS.getId(), padLength, true, buffer);
        if (Flags.isEndOfHeaders(flags)) {
            this.onHeadersComplete(streamId);
        } else {
            this.headersCurrentStream = streamId;
        }
    }

    protected void readPriorityFrame(int streamId, ByteBuffer buffer) throws IOException {
        try {
            this.swallowPayload(streamId, FrameType.PRIORITY.getId(), 5, false, buffer);
        }
        catch (ConnectionException connectionException) {
            // empty catch block
        }
        this.output.increaseOverheadCount(FrameType.PRIORITY);
    }

    protected void readRstFrame(int streamId, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[4];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        long errorCode = ByteUtil.getFourBytes(payload, 0);
        this.output.reset(streamId, errorCode);
        this.headersCurrentStream = -1;
        this.headersEndStream = false;
    }

    protected void readSettingsFrame(int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        boolean ack = Flags.isAck(flags);
        if (payloadSize > 0 && ack) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameSettings.ackWithNonZeroPayload"), Http2Error.FRAME_SIZE_ERROR);
        }
        if (payloadSize == 0 && !ack) {
            this.output.setting(null, 0L);
        } else {
            byte[] setting = new byte[6];
            for (int i = 0; i < payloadSize / 6; ++i) {
                if (buffer == null) {
                    this.input.fill(true, setting);
                } else {
                    buffer.get(setting);
                }
                int id = ByteUtil.getTwoBytes(setting, 0);
                long value = ByteUtil.getFourBytes(setting, 2);
                Setting key = Setting.valueOf(id);
                if (key == Setting.UNKNOWN) {
                    log.warn((Object)sm.getString("connectionSettings.unknown", new Object[]{this.connectionId, Integer.toString(id), Long.toString(value)}));
                }
                this.output.setting(key, value);
            }
        }
        this.output.settingsEnd(ack);
    }

    protected void readPushPromiseFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        throw new ConnectionException(sm.getString("http2Parser.processFramePushPromise", new Object[]{this.connectionId, streamId}), Http2Error.PROTOCOL_ERROR);
    }

    protected void readPingFrame(int flags, ByteBuffer buffer) throws IOException {
        byte[] payload = new byte[8];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        this.output.pingReceive(payload, Flags.isAck(flags));
    }

    protected void readGoawayFrame(int payloadSize, ByteBuffer buffer) throws IOException {
        byte[] payload = new byte[payloadSize];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int lastStreamId = ByteUtil.get31Bits(payload, 0);
        long errorCode = ByteUtil.getFourBytes(payload, 4);
        String debugData = null;
        if (payloadSize > 8) {
            debugData = new String(payload, 8, payloadSize - 8, StandardCharsets.UTF_8);
        }
        this.output.goaway(lastStreamId, errorCode, debugData);
    }

    protected void readWindowUpdateFrame(int streamId, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[4];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int windowSizeIncrement = ByteUtil.get31Bits(payload, 0);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("http2Parser.processFrameWindowUpdate.debug", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(windowSizeIncrement)}));
        }
        if (windowSizeIncrement == 0) {
            if (streamId == 0) {
                throw new ConnectionException(sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement", new Object[]{this.connectionId, Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR);
            }
            throw new StreamException(sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement", new Object[]{this.connectionId, Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR, streamId);
        }
        this.output.incrementWindowSize(streamId, windowSizeIncrement);
    }

    protected void readContinuationFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        if (this.headersCurrentStream == -1) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameContinuation.notExpected", new Object[]{this.connectionId, Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR);
        }
        boolean endOfHeaders = Flags.isEndOfHeaders(flags);
        this.output.headersContinue(payloadSize, endOfHeaders);
        this.readHeaderPayload(streamId, payloadSize, buffer);
        if (endOfHeaders) {
            this.headersCurrentStream = -1;
            this.onHeadersComplete(streamId);
        }
    }

    protected void readPriorityUpdateFrame(int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[payloadSize];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int prioritizedStreamID = ByteUtil.get31Bits(payload, 0);
        if (prioritizedStreamID == 0) {
            throw new ConnectionException(sm.getString("http2Parser.processFramePriorityUpdate.streamZero"), Http2Error.PROTOCOL_ERROR);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(payload, 4, payloadSize - 4);
        BufferedReader r = new BufferedReader(new InputStreamReader((InputStream)bais, StandardCharsets.US_ASCII));
        Priority p = Priority.parsePriority(r);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("http2Parser.processFramePriorityUpdate.debug", new Object[]{this.connectionId, Integer.toString(prioritizedStreamID), Integer.toString(p.getUrgency()), p.getIncremental()}));
        }
        this.output.priorityUpdate(prioritizedStreamID, p);
    }

    protected void readHeaderPayload(int streamId, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("http2Parser.processFrameHeaders.payload", new Object[]{this.connectionId, streamId, payloadSize}));
        }
        int remaining = payloadSize;
        while (remaining > 0) {
            if (this.headerReadBuffer.remaining() == 0) {
                int newSize = this.headerReadBuffer.capacity() < payloadSize ? payloadSize : this.headerReadBuffer.capacity() * 2;
                this.headerReadBuffer = ByteBufferUtils.expand((ByteBuffer)this.headerReadBuffer, (int)newSize);
            }
            int toRead = Math.min(this.headerReadBuffer.remaining(), remaining);
            if (buffer == null) {
                this.input.fill(true, this.headerReadBuffer, toRead);
            } else {
                int oldLimit = buffer.limit();
                buffer.limit(buffer.position() + toRead);
                this.headerReadBuffer.put(buffer);
                buffer.limit(oldLimit);
            }
            this.headerReadBuffer.flip();
            try {
                this.hpackDecoder.decode(this.headerReadBuffer);
            }
            catch (HpackException hpe) {
                throw new ConnectionException(sm.getString("http2Parser.processFrameHeaders.decodingFailed"), Http2Error.COMPRESSION_ERROR, hpe);
            }
            this.headerReadBuffer.compact();
            remaining -= toRead;
            if (this.hpackDecoder.isHeaderCountExceeded()) {
                StreamException headerException = new StreamException(sm.getString("http2Parser.headerLimitCount", new Object[]{this.connectionId, streamId}), Http2Error.ENHANCE_YOUR_CALM, streamId);
                this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException);
            }
            if (this.hpackDecoder.isHeaderSizeExceeded(this.headerReadBuffer.position())) {
                StreamException headerException = new StreamException(sm.getString("http2Parser.headerLimitSize", new Object[]{this.connectionId, streamId}), Http2Error.ENHANCE_YOUR_CALM, streamId);
                this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException);
            }
            if (!this.hpackDecoder.isHeaderSwallowSizeExceeded(this.headerReadBuffer.position())) continue;
            throw new ConnectionException(sm.getString("http2Parser.headerLimitSize", new Object[]{this.connectionId, streamId}), Http2Error.ENHANCE_YOUR_CALM);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readUnknownFrame(int streamId, int frameTypeId, int flags, int payloadSize, ByteBuffer buffer) throws IOException {
        try {
            this.swallowPayload(streamId, frameTypeId, payloadSize, false, buffer);
        }
        catch (ConnectionException connectionException) {
        }
        finally {
            this.output.onSwallowedUnknownFrame(streamId, frameTypeId, flags, payloadSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void swallowPayload(int streamId, int frameTypeId, int len, boolean isPadding, ByteBuffer byteBuffer) throws IOException, ConnectionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("http2Parser.swallow.debug", new Object[]{this.connectionId, Integer.toString(streamId), Integer.toString(len)}));
        }
        try {
            if (len == 0) {
                return;
            }
            if (!isPadding && byteBuffer != null) {
                byteBuffer.position(byteBuffer.position() + len);
            } else {
                int thisTime;
                byte[] buffer = new byte[1024];
                for (int read = 0; read < len; read += thisTime) {
                    thisTime = Math.min(buffer.length, len - read);
                    if (byteBuffer == null) {
                        this.input.fill(true, buffer, 0, thisTime);
                    } else {
                        byteBuffer.get(buffer, 0, thisTime);
                    }
                    if (!isPadding) continue;
                    for (int i = 0; i < thisTime; ++i) {
                        if (buffer[i] == 0) continue;
                        throw new ConnectionException(sm.getString("http2Parser.nonZeroPadding", new Object[]{this.connectionId, Integer.toString(streamId)}), Http2Error.PROTOCOL_ERROR);
                    }
                }
            }
        }
        finally {
            if (FrameType.DATA.getIdByte() == frameTypeId) {
                if (isPadding) {
                    ++len;
                }
                if (len > 0) {
                    this.output.onSwallowedDataFramePayload(streamId, len);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void onHeadersComplete(int streamId) throws Http2Exception {
        if (this.headerReadBuffer.position() > 0) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameHeaders.decodingDataLeft"), Http2Error.COMPRESSION_ERROR);
        }
        this.hpackDecoder.getHeaderEmitter().validateHeaders();
        Output output = this.output;
        synchronized (output) {
            this.output.headersEnd(streamId, this.headersEndStream);
            if (this.headersEndStream) {
                this.headersEndStream = false;
            }
        }
        if (this.headerReadBuffer.capacity() > 1024) {
            this.headerReadBuffer = ByteBuffer.allocate(1024);
        }
    }

    protected void validateFrame(FrameType expected, FrameType frameType, int streamId, int flags, int payloadSize) throws Http2Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("http2Parser.processFrame", new Object[]{this.connectionId, Integer.toString(streamId), frameType, Integer.toString(flags), Integer.toString(payloadSize)}));
        }
        if (expected != null && frameType != expected) {
            throw new StreamException(sm.getString("http2Parser.processFrame.unexpectedType", new Object[]{expected, frameType}), Http2Error.PROTOCOL_ERROR, streamId);
        }
        int maxFrameSize = this.input.getMaxFrameSize();
        if (payloadSize > maxFrameSize) {
            throw new ConnectionException(sm.getString("http2Parser.payloadTooBig", new Object[]{Integer.toString(payloadSize), Integer.toString(maxFrameSize)}), Http2Error.FRAME_SIZE_ERROR);
        }
        if (this.headersCurrentStream != -1) {
            if (this.headersCurrentStream != streamId) {
                throw new ConnectionException(sm.getString("http2Parser.headers.wrongStream", new Object[]{this.connectionId, Integer.toString(this.headersCurrentStream), Integer.toString(streamId)}), Http2Error.COMPRESSION_ERROR);
            }
            if (frameType != FrameType.RST && frameType != FrameType.CONTINUATION) {
                throw new ConnectionException(sm.getString("http2Parser.headers.wrongFrameType", new Object[]{this.connectionId, Integer.toString(this.headersCurrentStream), frameType}), Http2Error.COMPRESSION_ERROR);
            }
        }
        frameType.check(streamId, payloadSize);
    }

    void readConnectionPreface(WebConnection webConnection, Stream stream) throws Http2Exception {
        byte[] data = new byte[CLIENT_PREFACE_START.length];
        try {
            this.input.fill(true, data);
            for (int i = 0; i < CLIENT_PREFACE_START.length; ++i) {
                if (CLIENT_PREFACE_START[i] == data[i]) continue;
                throw new ProtocolException(sm.getString("http2Parser.preface.invalid"));
            }
            this.readFrame(true, FrameType.SETTINGS);
        }
        catch (IOException ioe) {
            throw new ProtocolException(sm.getString("http2Parser.preface.io"), ioe);
        }
    }

    static interface Input {
        public boolean fill(boolean var1, byte[] var2, int var3, int var4) throws IOException;

        default public boolean fill(boolean block, byte[] data) throws IOException {
            return this.fill(block, data, 0, data.length);
        }

        default public boolean fill(boolean block, ByteBuffer data, int len) throws IOException {
            boolean result = this.fill(block, data.array(), data.arrayOffset() + data.position(), len);
            if (result) {
                data.position(data.position() + len);
            }
            return result;
        }

        public int getMaxFrameSize();
    }

    static interface Output {
        public HpackDecoder getHpackDecoder();

        public ByteBuffer startRequestBodyFrame(int var1, int var2, boolean var3) throws Http2Exception;

        public void endRequestBodyFrame(int var1, int var2) throws Http2Exception, IOException;

        public void receivedEndOfStream(int var1) throws ConnectionException;

        public void onSwallowedDataFramePayload(int var1, int var2) throws ConnectionException, IOException;

        public HpackDecoder.HeaderEmitter headersStart(int var1, boolean var2) throws Http2Exception, IOException;

        public void headersContinue(int var1, boolean var2);

        public void headersEnd(int var1, boolean var2) throws Http2Exception;

        public void reset(int var1, long var2) throws Http2Exception;

        public void setting(Setting var1, long var2) throws ConnectionException;

        public void settingsEnd(boolean var1) throws IOException;

        public void pingReceive(byte[] var1, boolean var2) throws IOException;

        public void goaway(int var1, long var2, String var4);

        public void incrementWindowSize(int var1, int var2) throws Http2Exception;

        public void priorityUpdate(int var1, Priority var2) throws Http2Exception;

        public void onSwallowedUnknownFrame(int var1, int var2, int var3, int var4) throws IOException;

        public void increaseOverheadCount(FrameType var1);
    }
}

