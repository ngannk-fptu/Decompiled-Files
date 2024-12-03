/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http2.Stream;

public class Http2OutputBuffer
implements HttpOutputBuffer {
    private final Response coyoteResponse;
    private HttpOutputBuffer next;

    public void addFilter(OutputFilter filter) {
        filter.setBuffer(this.next);
        this.next = filter;
    }

    public Http2OutputBuffer(Response coyoteResponse, Stream.StreamOutputBuffer streamOutputBuffer) {
        this.coyoteResponse = coyoteResponse;
        this.next = streamOutputBuffer;
    }

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (!this.coyoteResponse.isCommitted()) {
            this.coyoteResponse.sendHeaders();
        }
        return this.next.doWrite(chunk);
    }

    @Override
    public long getBytesWritten() {
        return this.next.getBytesWritten();
    }

    @Override
    public void end() throws IOException {
        this.next.end();
    }

    @Override
    public void flush() throws IOException {
        this.next.flush();
    }
}

