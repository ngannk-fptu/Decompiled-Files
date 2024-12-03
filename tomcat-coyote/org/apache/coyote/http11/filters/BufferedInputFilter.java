/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

public class BufferedInputFilter
implements InputFilter,
ApplicationBufferHandler {
    private static final StringManager sm = StringManager.getManager(BufferedInputFilter.class);
    private static final String ENCODING_NAME = "buffered";
    private static final ByteChunk ENCODING = new ByteChunk();
    private ByteChunk buffered;
    private ByteBuffer tempRead;
    private InputBuffer buffer;
    private boolean hasRead = false;
    private final int maxSwallowSize;

    public BufferedInputFilter(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public void setLimit(int limit) {
        if (this.buffered == null) {
            this.buffered = new ByteChunk();
            this.buffered.setLimit(limit);
        }
    }

    @Override
    public void setRequest(Request request) {
        try {
            if (this.buffered.getLimit() == 0) {
                long swallowed = 0L;
                int read = 0;
                while ((read = this.buffer.doRead(this)) >= 0) {
                    if (this.maxSwallowSize <= -1 || (swallowed += (long)read) <= (long)this.maxSwallowSize) continue;
                    throw new IOException(sm.getString("bufferedInputFilter.maxSwallowSize"));
                }
            } else {
                while (this.buffer.doRead(this) >= 0) {
                    this.buffered.append(this.tempRead);
                    this.tempRead = null;
                }
            }
        }
        catch (IOException | BufferOverflowException ioe) {
            throw new IllegalStateException(sm.getString("bufferedInputFilter.bodySize", new Object[]{ioe.getMessage()}));
        }
    }

    @Override
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.isFinished()) {
            return -1;
        }
        handler.setByteBuffer(ByteBuffer.wrap(this.buffered.getBuffer(), this.buffered.getStart(), this.buffered.getLength()));
        this.hasRead = true;
        return this.buffered.getLength();
    }

    @Override
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void recycle() {
        if (this.buffered != null) {
            if (this.buffered.getBuffer() != null && this.buffered.getBuffer().length > 65536) {
                this.buffered = null;
            } else {
                this.buffered.recycle();
            }
        }
        this.hasRead = false;
        this.buffer = null;
    }

    @Override
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override
    public long end() throws IOException {
        return 0L;
    }

    @Override
    public int available() {
        int available = this.buffered.getLength();
        if (available == 0) {
            return this.buffer.available();
        }
        return available;
    }

    @Override
    public boolean isFinished() {
        return this.hasRead || this.buffered.getLength() <= 0;
    }

    @Override
    public void setByteBuffer(ByteBuffer buffer) {
        this.tempRead = buffer;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return this.tempRead;
    }

    @Override
    public void expand(int size) {
    }

    static {
        ENCODING.setBytes(ENCODING_NAME.getBytes(StandardCharsets.ISO_8859_1), 0, ENCODING_NAME.length());
    }
}

