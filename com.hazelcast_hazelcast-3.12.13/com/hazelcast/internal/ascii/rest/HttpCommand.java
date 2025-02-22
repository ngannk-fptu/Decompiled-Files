/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;
import java.util.Map;

@SuppressFBWarnings(value={"EI_EXPOSE_REP", "MS_MUTABLE_ARRAY", "MS_PKGPROTECT"})
public abstract class HttpCommand
extends AbstractTextCommand {
    public static final String HEADER_CONTENT_TYPE = "content-type: ";
    public static final String HEADER_CONTENT_LENGTH = "content-length: ";
    public static final String HEADER_CHUNKED = "transfer-encoding: chunked";
    public static final String HEADER_EXPECT_100 = "expect: 100";
    public static final String HEADER_CUSTOM_PREFIX = "Hazelcast-";
    public static final byte[] RES_200 = StringUtil.stringToBytes("HTTP/1.1 200 OK\r\n");
    public static final byte[] RES_200_WITH_NO_CONTENT = StringUtil.stringToBytes("HTTP/1.1 200 OK\nContent-Length: 0\n\n");
    public static final byte[] RES_400 = StringUtil.stringToBytes("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] RES_403 = StringUtil.stringToBytes("HTTP/1.1 403 Forbidden\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] RES_404 = StringUtil.stringToBytes("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] RES_100 = StringUtil.stringToBytes("HTTP/1.1 100 Continue\r\n\r\n");
    public static final byte[] RES_204 = StringUtil.stringToBytes("HTTP/1.1 204 No Content\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] RES_503 = StringUtil.stringToBytes("HTTP/1.1 503 Service Unavailable\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] RES_500 = StringUtil.stringToBytes("HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n");
    public static final byte[] CONTENT_TYPE = StringUtil.stringToBytes("Content-Type: ");
    public static final byte[] CONTENT_LENGTH = StringUtil.stringToBytes("Content-Length: ");
    public static final byte[] CONTENT_TYPE_PLAIN_TEXT = StringUtil.stringToBytes("text/plain");
    public static final byte[] CONTENT_TYPE_JSON = StringUtil.stringToBytes("application/javascript");
    public static final byte[] CONTENT_TYPE_BINARY = StringUtil.stringToBytes("application/binary");
    protected final String uri;
    protected ByteBuffer response;
    protected boolean nextLine;

    public HttpCommand(TextCommandConstants.TextCommandType type, String uri) {
        super(type);
        this.uri = uri;
        this.nextLine = true;
    }

    @Override
    public boolean shouldReply() {
        return true;
    }

    public String getURI() {
        return this.uri;
    }

    public void send204() {
        this.response = ByteBuffer.wrap(RES_204);
    }

    public void send400() {
        this.response = ByteBuffer.wrap(RES_400);
    }

    public void send403() {
        this.response = ByteBuffer.wrap(RES_403);
    }

    public void send404() {
        this.response = ByteBuffer.wrap(RES_404);
    }

    public void send500() {
        this.response = ByteBuffer.wrap(RES_500);
    }

    public void setResponse(byte[] value) {
        this.response = ByteBuffer.wrap(value);
    }

    public void send200() {
        this.setResponse(null, null);
    }

    public void setResponse(Map<String, Object> headers) {
        int size = RES_200.length;
        byte[] len = StringUtil.stringToBytes(String.valueOf(0));
        size += CONTENT_LENGTH.length;
        size += len.length;
        size += TextCommandConstants.RETURN.length;
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                size += StringUtil.stringToBytes(HEADER_CUSTOM_PREFIX + entry.getKey() + ": ").length;
                size += StringUtil.stringToBytes(entry.getValue().toString()).length;
                size += TextCommandConstants.RETURN.length;
            }
        }
        this.response = ByteBuffer.allocate(size += TextCommandConstants.RETURN.length);
        this.response.put(RES_200);
        this.response.put(CONTENT_LENGTH);
        this.response.put(len);
        this.response.put(TextCommandConstants.RETURN);
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                this.response.put(StringUtil.stringToBytes(HEADER_CUSTOM_PREFIX + entry.getKey() + ": "));
                this.response.put(StringUtil.stringToBytes(entry.getValue().toString()));
                this.response.put(TextCommandConstants.RETURN);
            }
        }
        this.response.put(TextCommandConstants.RETURN);
        this.response.flip();
    }

    public void setResponse(byte[] contentType, byte[] value) {
        int valueSize = value == null ? 0 : value.length;
        byte[] len = StringUtil.stringToBytes(String.valueOf(valueSize));
        int size = RES_200.length;
        if (contentType != null) {
            size += CONTENT_TYPE.length;
            size += contentType.length;
            size += TextCommandConstants.RETURN.length;
        }
        size += CONTENT_LENGTH.length;
        size += len.length;
        size += TextCommandConstants.RETURN.length;
        size += TextCommandConstants.RETURN.length;
        this.response = ByteBuffer.allocate(size += valueSize);
        this.response.put(RES_200);
        if (contentType != null) {
            this.response.put(CONTENT_TYPE);
            this.response.put(contentType);
            this.response.put(TextCommandConstants.RETURN);
        }
        this.response.put(CONTENT_LENGTH);
        this.response.put(len);
        this.response.put(TextCommandConstants.RETURN);
        this.response.put(TextCommandConstants.RETURN);
        if (value != null) {
            this.response.put(value);
        }
        this.response.flip();
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        IOUtil.copyToHeapBuffer(this.response, dst);
        return !this.response.hasRemaining();
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        while (src.hasRemaining()) {
            char c = (char)src.get();
            if (c == '\n') {
                if (this.nextLine) {
                    return true;
                }
                this.nextLine = true;
                continue;
            }
            if (c == '\r') continue;
            this.nextLine = false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "HttpCommand [" + (Object)((Object)this.type) + "]{uri='" + this.uri + '\'' + '}' + super.toString();
    }
}

