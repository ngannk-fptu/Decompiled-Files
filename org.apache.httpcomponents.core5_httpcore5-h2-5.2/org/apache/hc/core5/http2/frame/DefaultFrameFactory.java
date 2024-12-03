/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.frame;

import java.nio.ByteBuffer;
import org.apache.hc.core5.http2.frame.FrameFactory;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.FrameType;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.util.Args;

public class DefaultFrameFactory
extends FrameFactory {
    public static final FrameFactory INSTANCE = new DefaultFrameFactory();

    @Override
    public RawFrame createHeaders(int streamId, ByteBuffer payload, boolean endHeaders, boolean endStream) {
        Args.positive((int)streamId, (String)"Stream id");
        int flags = (endHeaders ? FrameFlag.END_HEADERS.value : 0) | (endStream ? FrameFlag.END_STREAM.value : 0);
        return new RawFrame(FrameType.HEADERS.getValue(), flags, streamId, payload);
    }

    @Override
    public RawFrame createContinuation(int streamId, ByteBuffer payload, boolean endHeaders) {
        Args.positive((int)streamId, (String)"Stream id");
        int flags = endHeaders ? FrameFlag.END_HEADERS.value : 0;
        return new RawFrame(FrameType.CONTINUATION.getValue(), flags, streamId, payload);
    }

    @Override
    public RawFrame createPushPromise(int streamId, ByteBuffer payload, boolean endHeaders) {
        Args.positive((int)streamId, (String)"Stream id");
        int flags = endHeaders ? FrameFlag.END_HEADERS.value : 0;
        return new RawFrame(FrameType.PUSH_PROMISE.getValue(), flags, streamId, payload);
    }

    @Override
    public RawFrame createData(int streamId, ByteBuffer payload, boolean endStream) {
        Args.positive((int)streamId, (String)"Stream id");
        int flags = endStream ? FrameFlag.END_STREAM.value : 0;
        return new RawFrame(FrameType.DATA.getValue(), flags, streamId, payload);
    }
}

