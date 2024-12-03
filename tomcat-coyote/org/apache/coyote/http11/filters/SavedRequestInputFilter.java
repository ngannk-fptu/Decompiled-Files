/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

public class SavedRequestInputFilter
implements InputFilter {
    protected ByteChunk input = null;

    public SavedRequestInputFilter(ByteChunk input) {
        this.input = input;
    }

    @Override
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.input.getOffset() >= this.input.getEnd()) {
            return -1;
        }
        ByteBuffer byteBuffer = handler.getByteBuffer();
        byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());
        this.input.subtract(byteBuffer);
        return byteBuffer.remaining();
    }

    @Override
    public void setRequest(Request request) {
        request.setContentLength(this.input.getLength());
    }

    @Override
    public void recycle() {
        this.input = null;
    }

    @Override
    public ByteChunk getEncodingName() {
        return null;
    }

    @Override
    public void setBuffer(InputBuffer buffer) {
    }

    @Override
    public int available() {
        return this.input.getLength();
    }

    @Override
    public long end() throws IOException {
        return 0L;
    }

    @Override
    public boolean isFinished() {
        return this.input.getOffset() >= this.input.getEnd();
    }
}

