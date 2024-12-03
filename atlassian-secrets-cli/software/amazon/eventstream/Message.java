/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.eventstream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import software.amazon.eventstream.Checksums;
import software.amazon.eventstream.Header;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Prelude;

public class Message {
    private static final int TRAILING_CRC_LENGTH = 4;
    static final int MESSAGE_OVERHEAD = 16;
    private final Map<String, HeaderValue> headers;
    private final byte[] payload;

    public Message(Map<String, HeaderValue> headers, byte[] payload) {
        this.headers = Objects.requireNonNull(headers, "headers");
        this.payload = (byte[])Objects.requireNonNull(payload, "payload").clone();
    }

    public Map<String, HeaderValue> getHeaders() {
        return this.headers;
    }

    public byte[] getPayload() {
        return (byte[])this.payload.clone();
    }

    public static Message decode(ByteBuffer buf) {
        return Message.decode(Prelude.decode(buf), buf);
    }

    static Message decode(Prelude prelude, ByteBuffer buf) {
        int totalLength = prelude.getTotalLength();
        Message.validateMessageCrc(buf, totalLength);
        buf.position(buf.position() + 12);
        long headersLength = prelude.getHeadersLength();
        byte[] headerBytes = new byte[Math.toIntExact(headersLength)];
        buf.get(headerBytes);
        Map<String, HeaderValue> headers = Message.decodeHeaders(ByteBuffer.wrap(headerBytes));
        byte[] payload = new byte[Math.toIntExact((long)(totalLength - 16) - headersLength)];
        buf.get(payload);
        buf.getInt();
        return new Message(headers, payload);
    }

    private static void validateMessageCrc(ByteBuffer buf, int totalLength) {
        CRC32 crc = new CRC32();
        Checksums.update(crc, (ByteBuffer)buf.duplicate().limit(buf.position() + totalLength - 4));
        long computedMessageCrc = crc.getValue();
        long wireMessageCrc = Integer.toUnsignedLong(buf.getInt(buf.position() + totalLength - 4));
        if (wireMessageCrc != computedMessageCrc) {
            throw new IllegalArgumentException(String.format("Message checksum failure: expected 0x%x, computed 0x%x", wireMessageCrc, computedMessageCrc));
        }
    }

    static Map<String, HeaderValue> decodeHeaders(ByteBuffer buf) {
        LinkedHashMap<String, HeaderValue> headers = new LinkedHashMap<String, HeaderValue>();
        while (buf.hasRemaining()) {
            Header header = Header.decode(buf);
            headers.put(header.getName(), header.getValue());
        }
        return Collections.unmodifiableMap(headers);
    }

    public ByteBuffer toByteBuffer() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.encode(baos);
            baos.close();
            return ByteBuffer.wrap(baos.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void encode(OutputStream os) {
        try {
            CheckedOutputStream checkedOutputStream = new CheckedOutputStream(os, new CRC32());
            this.encodeOrThrow(checkedOutputStream);
            long messageCrc = checkedOutputStream.getChecksum().getValue();
            os.write((int)(0xFFL & messageCrc >> 24));
            os.write((int)(0xFFL & messageCrc >> 16));
            os.write((int)(0xFFL & messageCrc >> 8));
            os.write((int)(0xFFL & messageCrc));
            os.flush();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] encodeHeaders(Iterable<Map.Entry<String, HeaderValue>> headers) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            for (Map.Entry<String, HeaderValue> entry : headers) {
                Header.encode(entry, dos);
            }
            dos.close();
            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void encodeOrThrow(OutputStream os) throws IOException {
        ByteArrayOutputStream headersAndPayload = new ByteArrayOutputStream();
        headersAndPayload.write(Message.encodeHeaders(this.headers.entrySet()));
        headersAndPayload.write(this.payload);
        int totalLength = 12 + headersAndPayload.size() + 4;
        byte[] preludeBytes = this.getPrelude(totalLength);
        CRC32 crc = new CRC32();
        crc.update(preludeBytes, 0, preludeBytes.length);
        DataOutputStream dos = new DataOutputStream(os);
        dos.write(preludeBytes);
        dos.writeInt((int)crc.getValue());
        dos.flush();
        headersAndPayload.writeTo(os);
    }

    private byte[] getPrelude(int totalLength) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        DataOutputStream dos = new DataOutputStream(baos);
        int headerLength = totalLength - 16 - this.payload.length;
        dos.writeInt(totalLength);
        dos.writeInt(headerLength);
        dos.close();
        return baos.toByteArray();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message)o;
        if (!this.headers.equals(message.headers)) {
            return false;
        }
        return Arrays.equals(this.payload, message.payload);
    }

    public int hashCode() {
        int result = this.headers.hashCode();
        result = 31 * result + Arrays.hashCode(this.payload);
        return result;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Map.Entry<String, HeaderValue> entry : this.headers.entrySet()) {
            ret.append(entry.getKey());
            ret.append(": ");
            ret.append(entry.getValue().toString());
            ret.append('\n');
        }
        ret.append('\n');
        String contentType = this.headers.getOrDefault(":content-type", HeaderValue.fromString("application/octet-stream")).getString();
        if (contentType.contains("json") || contentType.contains("text")) {
            ret.append(new String(this.payload, StandardCharsets.UTF_8));
        } else {
            ret.append(Base64.getEncoder().encodeToString(this.payload));
        }
        ret.append('\n');
        return ret.toString();
    }
}

