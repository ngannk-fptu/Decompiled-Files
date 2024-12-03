/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

public class IdentityInputFilter
implements InputFilter,
ApplicationBufferHandler {
    private static final StringManager sm = StringManager.getManager(IdentityInputFilter.class);
    protected static final String ENCODING_NAME = "identity";
    protected static final ByteChunk ENCODING = new ByteChunk();
    protected long contentLength = -1L;
    protected long remaining = 0L;
    protected InputBuffer buffer;
    protected ByteBuffer tempRead;
    private final int maxSwallowSize;

    public IdentityInputFilter(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    @Override
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        int result = -1;
        if (this.contentLength >= 0L) {
            if (this.remaining > 0L) {
                int nRead = this.buffer.doRead(handler);
                if ((long)nRead > this.remaining) {
                    handler.getByteBuffer().limit(handler.getByteBuffer().position() + (int)this.remaining);
                    result = (int)this.remaining;
                } else {
                    result = nRead;
                }
                if (nRead > 0) {
                    this.remaining -= (long)nRead;
                }
            } else {
                if (handler.getByteBuffer() != null) {
                    handler.getByteBuffer().position(0).limit(0);
                }
                result = -1;
            }
        }
        return result;
    }

    @Override
    public void setRequest(Request request) {
        this.remaining = this.contentLength = request.getContentLengthLong();
    }

    @Override
    public long end() throws IOException {
        boolean maxSwallowSizeExceeded = this.maxSwallowSize > -1 && this.remaining > (long)this.maxSwallowSize;
        long swallowed = 0L;
        while (this.remaining > 0L) {
            int nread = this.buffer.doRead(this);
            this.tempRead = null;
            if (nread > 0) {
                this.remaining -= (long)nread;
                if (!maxSwallowSizeExceeded || (swallowed += (long)nread) <= (long)this.maxSwallowSize) continue;
                throw new IOException(sm.getString("inputFilter.maxSwallow"));
            }
            this.remaining = 0L;
        }
        return -this.remaining;
    }

    @Override
    public int available() {
        return this.buffer.available();
    }

    @Override
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void recycle() {
        this.contentLength = -1L;
        this.remaining = 0L;
    }

    @Override
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override
    public boolean isFinished() {
        return this.contentLength > -1L && this.remaining <= 0L;
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

