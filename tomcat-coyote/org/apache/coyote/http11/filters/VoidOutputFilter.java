/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;

public class VoidOutputFilter
implements OutputFilter {
    private HttpOutputBuffer buffer = null;

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        return chunk.remaining();
    }

    @Override
    public long getBytesWritten() {
        return 0L;
    }

    @Override
    public void setResponse(Response response) {
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
    public void recycle() {
        this.buffer = null;
    }

    @Override
    public void end() throws IOException {
        this.buffer.end();
    }
}

