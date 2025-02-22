/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.NoOpCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.util.StringUtil;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class HttpPostCommand
extends HttpCommand {
    private static final int RADIX = 16;
    private static final int INITIAL_CAPACITY = 256;
    private static final int MAX_CAPACITY = 65536;
    private static final byte LINE_FEED = 10;
    private static final byte CARRIAGE_RETURN = 13;
    private final TextDecoder decoder;
    private boolean chunked;
    private boolean readyToReadData;
    private ByteBuffer data;
    private String contentType;
    private ByteBuffer lineBuffer = ByteBuffer.allocate(256);

    public HttpPostCommand(TextDecoder decoder, String uri) {
        super(TextCommandConstants.TextCommandType.HTTP_POST, uri);
        this.decoder = decoder;
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        boolean complete = this.doActualRead(src);
        while (!complete && this.readyToReadData && this.chunked && src.hasRemaining()) {
            complete = this.doActualRead(src);
        }
        if (complete && this.data != null) {
            this.data.flip();
        }
        return complete;
    }

    private boolean doActualRead(ByteBuffer cb) {
        this.setReadyToReadData(cb);
        if (!this.readyToReadData) {
            return false;
        }
        if (!this.isSpaceForData()) {
            if (this.chunked) {
                if (this.data != null && cb.hasRemaining()) {
                    this.readCRLFOrPositionChunkSize(cb);
                }
                if (this.readChunkSize(cb)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        if (this.data != null) {
            IOUtil.copyToHeapBuffer(cb, this.data);
        }
        return !this.chunked && !this.isSpaceForData();
    }

    private boolean isSpaceForData() {
        return this.data != null && this.data.hasRemaining();
    }

    private void setReadyToReadData(ByteBuffer cb) {
        while (!this.readyToReadData && cb.hasRemaining()) {
            byte b = cb.get();
            if (b == 13) {
                this.readLF(cb);
                this.processLine(StringUtil.lowerCaseInternal(this.toStringAndClear(this.lineBuffer)));
                if (this.nextLine) {
                    this.readyToReadData = true;
                }
                this.nextLine = true;
                break;
            }
            this.nextLine = false;
            this.appendToBuffer(b);
        }
    }

    public byte[] getData() {
        if (this.data == null) {
            return null;
        }
        return this.data.array();
    }

    byte[] getContentType() {
        if (this.contentType == null) {
            return null;
        }
        return StringUtil.stringToBytes(this.contentType);
    }

    private void readCRLFOrPositionChunkSize(ByteBuffer cb) {
        byte b = cb.get();
        if (b == 13) {
            this.readLF(cb);
        } else {
            cb.position(cb.position() - 1);
        }
    }

    private void readLF(ByteBuffer cb) {
        assert (cb.hasRemaining()) : "'\\n' should follow '\\r'";
        byte b = cb.get();
        if (b != 10) {
            throw new IllegalStateException("'\\n' should follow '\\r', but got '" + (char)b + "'");
        }
    }

    private String toStringAndClear(ByteBuffer bb) {
        if (bb == null) {
            return "";
        }
        String result = bb.position() == 0 ? "" : StringUtil.bytesToString(bb.array(), 0, bb.position());
        bb.clear();
        return result;
    }

    private boolean readChunkSize(ByteBuffer cb) {
        boolean hasLine = false;
        while (cb.hasRemaining()) {
            byte b = cb.get();
            if (b == 13) {
                this.readLF(cb);
                hasLine = true;
                break;
            }
            this.appendToBuffer(b);
        }
        if (hasLine) {
            int dataSize;
            String lineStr = this.toStringAndClear(this.lineBuffer).trim();
            int n = dataSize = lineStr.length() == 0 ? 0 : Integer.parseInt(lineStr, 16);
            if (dataSize == 0) {
                return true;
            }
            this.dataNullCheck(dataSize);
        }
        return false;
    }

    private void dataNullCheck(int dataSize) {
        if (this.data != null) {
            ByteBuffer newData = ByteBuffer.allocate(this.data.capacity() + dataSize);
            newData.put(this.data.array());
            this.data = newData;
        } else {
            this.data = ByteBuffer.allocate(dataSize);
        }
    }

    private void appendToBuffer(byte b) {
        if (!this.lineBuffer.hasRemaining()) {
            this.expandBuffer();
        }
        this.lineBuffer.put(b);
    }

    private void expandBuffer() {
        if (this.lineBuffer.capacity() == 65536) {
            throw new BufferOverflowException();
        }
        int capacity = this.lineBuffer.capacity() << 1;
        ByteBuffer newBuffer = ByteBuffer.allocate(capacity);
        this.lineBuffer.flip();
        newBuffer.put(this.lineBuffer);
        this.lineBuffer = newBuffer;
    }

    private void processLine(String currentLine) {
        if (this.contentType == null && currentLine.startsWith("content-type: ")) {
            this.contentType = currentLine.substring(currentLine.indexOf(32) + 1);
        } else if (this.data == null && currentLine.startsWith("content-length: ")) {
            this.data = ByteBuffer.allocate(Integer.parseInt(currentLine.substring(currentLine.indexOf(32) + 1)));
        } else if (!this.chunked && currentLine.startsWith("transfer-encoding: chunked")) {
            this.chunked = true;
        } else if (currentLine.startsWith("expect: 100")) {
            this.decoder.sendResponse(new NoOpCommand(RES_100));
        }
    }
}

