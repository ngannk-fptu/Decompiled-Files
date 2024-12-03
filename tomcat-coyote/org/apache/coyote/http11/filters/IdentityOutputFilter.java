/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;

public class IdentityOutputFilter
implements OutputFilter {
    protected long contentLength = -1L;
    protected long remaining = 0L;
    protected HttpOutputBuffer buffer;

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        int result = -1;
        if (this.contentLength >= 0L) {
            if (this.remaining > 0L) {
                result = chunk.remaining();
                if ((long)result > this.remaining) {
                    chunk.limit(chunk.position() + (int)this.remaining);
                    result = (int)this.remaining;
                    this.remaining = 0L;
                } else {
                    this.remaining -= (long)result;
                }
                this.buffer.doWrite(chunk);
            } else {
                chunk.position(0);
                chunk.limit(0);
                result = -1;
            }
        } else {
            result = chunk.remaining();
            this.buffer.doWrite(chunk);
            result -= chunk.remaining();
        }
        return result;
    }

    @Override
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override
    public void setResponse(Response response) {
        this.remaining = this.contentLength = response.getContentLengthLong();
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
        this.buffer.end();
    }

    @Override
    public void recycle() {
        this.contentLength = -1L;
        this.remaining = 0L;
    }
}

