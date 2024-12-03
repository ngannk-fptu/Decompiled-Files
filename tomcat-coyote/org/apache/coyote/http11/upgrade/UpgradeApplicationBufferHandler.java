/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11.upgrade;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

public class UpgradeApplicationBufferHandler
implements ApplicationBufferHandler {
    private ByteBuffer byteBuffer;

    @Override
    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override
    public void expand(int size) {
    }
}

