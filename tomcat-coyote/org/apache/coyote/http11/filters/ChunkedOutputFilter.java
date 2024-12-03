/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.HexUtils
 */
package org.apache.coyote.http11.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.tomcat.util.buf.HexUtils;

public class ChunkedOutputFilter
implements OutputFilter {
    private static final byte[] LAST_CHUNK_BYTES = new byte[]{48, 13, 10};
    private static final byte[] CRLF_BYTES = new byte[]{13, 10};
    private static final byte[] END_CHUNK_BYTES = new byte[]{48, 13, 10, 13, 10};
    private static final Set<String> disallowedTrailerFieldNames = new HashSet<String>();
    protected HttpOutputBuffer buffer;
    protected final ByteBuffer chunkHeader = ByteBuffer.allocate(10);
    protected final ByteBuffer lastChunk = ByteBuffer.wrap(LAST_CHUNK_BYTES);
    protected final ByteBuffer crlfChunk = ByteBuffer.wrap(CRLF_BYTES);
    protected final ByteBuffer endChunk = ByteBuffer.wrap(END_CHUNK_BYTES);
    private Response response;

    public ChunkedOutputFilter() {
        this.chunkHeader.put(8, (byte)13);
        this.chunkHeader.put(9, (byte)10);
    }

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        int result = chunk.remaining();
        if (result <= 0) {
            return 0;
        }
        int pos = this.calculateChunkHeader(result);
        this.chunkHeader.position(pos).limit(10);
        this.buffer.doWrite(this.chunkHeader);
        this.buffer.doWrite(chunk);
        this.chunkHeader.position(8).limit(10);
        this.buffer.doWrite(this.chunkHeader);
        return result;
    }

    private int calculateChunkHeader(int len) {
        int pos = 8;
        for (int current = len; current > 0; current /= 16) {
            int digit = current % 16;
            this.chunkHeader.put(--pos, HexUtils.getHex((int)digit));
        }
        return pos;
    }

    @Override
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public void setBuffer(HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void flush() throws IOException {
        this.buffer.flush();
    }

    @Override
    public void end() throws IOException {
        Supplier<Map<String, String>> trailerFieldsSupplier = this.response.getTrailerFields();
        Map<String, String> trailerFields = null;
        if (trailerFieldsSupplier != null) {
            trailerFields = trailerFieldsSupplier.get();
        }
        if (trailerFields == null) {
            this.buffer.doWrite(this.endChunk);
            this.endChunk.position(0).limit(this.endChunk.capacity());
        } else {
            this.buffer.doWrite(this.lastChunk);
            this.lastChunk.position(0).limit(this.lastChunk.capacity());
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            try (OutputStreamWriter osw = new OutputStreamWriter((OutputStream)baos, StandardCharsets.ISO_8859_1);){
                for (Map.Entry<String, String> trailerField : trailerFields.entrySet()) {
                    if (disallowedTrailerFieldNames.contains(trailerField.getKey().toLowerCase(Locale.ENGLISH))) continue;
                    osw.write(trailerField.getKey());
                    osw.write(58);
                    osw.write(32);
                    osw.write(trailerField.getValue());
                    osw.write("\r\n");
                }
            }
            this.buffer.doWrite(ByteBuffer.wrap(baos.toByteArray()));
            this.buffer.doWrite(this.crlfChunk);
            this.crlfChunk.position(0).limit(this.crlfChunk.capacity());
        }
        this.buffer.end();
    }

    @Override
    public void recycle() {
        this.response = null;
    }

    static {
        disallowedTrailerFieldNames.add("age");
        disallowedTrailerFieldNames.add("cache-control");
        disallowedTrailerFieldNames.add("content-length");
        disallowedTrailerFieldNames.add("content-encoding");
        disallowedTrailerFieldNames.add("content-range");
        disallowedTrailerFieldNames.add("content-type");
        disallowedTrailerFieldNames.add("date");
        disallowedTrailerFieldNames.add("expires");
        disallowedTrailerFieldNames.add("location");
        disallowedTrailerFieldNames.add("retry-after");
        disallowedTrailerFieldNames.add("trailer");
        disallowedTrailerFieldNames.add("transfer-encoding");
        disallowedTrailerFieldNames.add("vary");
        disallowedTrailerFieldNames.add("warning");
    }
}

