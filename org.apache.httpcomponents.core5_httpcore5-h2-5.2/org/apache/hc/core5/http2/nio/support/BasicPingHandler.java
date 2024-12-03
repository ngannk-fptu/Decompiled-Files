/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.nio.support;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http2.nio.AsyncPingHandler;
import org.apache.hc.core5.util.Args;

public class BasicPingHandler
implements AsyncPingHandler {
    private static final byte[] PING_MESSAGE = new byte[]{42, 42, 112, 105, 110, 103, 42, 42};
    private final Callback<Boolean> callback;

    public BasicPingHandler(Callback<Boolean> callback) {
        this.callback = (Callback)Args.notNull(callback, (String)"Callback");
    }

    @Override
    public ByteBuffer getData() {
        return ByteBuffer.wrap(PING_MESSAGE);
    }

    @Override
    public void consumeResponse(ByteBuffer feedback) throws HttpException, IOException {
        boolean result = true;
        for (int i = 0; i < PING_MESSAGE.length; ++i) {
            if (feedback.hasRemaining() && PING_MESSAGE[i] == feedback.get()) continue;
            result = false;
            break;
        }
        this.callback.execute((Object)result);
    }

    @Override
    public void failed(Exception cause) {
        this.callback.execute((Object)Boolean.FALSE);
    }

    @Override
    public void cancel() {
        this.callback.execute((Object)Boolean.FALSE);
    }
}

