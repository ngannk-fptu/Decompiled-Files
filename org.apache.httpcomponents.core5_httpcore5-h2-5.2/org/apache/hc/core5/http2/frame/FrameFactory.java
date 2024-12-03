/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.config.H2Setting;
import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.FrameType;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.util.Args;

public abstract class FrameFactory {
    public RawFrame createSettings(H2Setting ... settings) {
        ByteBuffer payload = ByteBuffer.allocate(settings.length * 12);
        for (H2Setting setting : settings) {
            payload.putShort((short)setting.getCode());
            payload.putInt(setting.getValue());
        }
        payload.flip();
        return new RawFrame(FrameType.SETTINGS.getValue(), 0, 0, payload);
    }

    public RawFrame createSettingsAck() {
        return new RawFrame(FrameType.SETTINGS.getValue(), FrameFlag.ACK.getValue(), 0, null);
    }

    public RawFrame createResetStream(int streamId, H2Error error) {
        Args.notNull((Object)((Object)error), (String)"Error");
        return this.createResetStream(streamId, error.getCode());
    }

    public RawFrame createResetStream(int streamId, int code) {
        Args.positive((int)streamId, (String)"Stream id");
        ByteBuffer payload = ByteBuffer.allocate(4);
        payload.putInt(code);
        payload.flip();
        return new RawFrame(FrameType.RST_STREAM.getValue(), 0, streamId, payload);
    }

    public RawFrame createPing(ByteBuffer opaqueData) {
        Args.notNull((Object)opaqueData, (String)"Opaque data");
        Args.check((opaqueData.remaining() == 8 ? 1 : 0) != 0, (String)"Opaque data length must be equal 8");
        return new RawFrame(FrameType.PING.getValue(), 0, 0, opaqueData);
    }

    public RawFrame createPingAck(ByteBuffer opaqueData) {
        Args.notNull((Object)opaqueData, (String)"Opaque data");
        Args.check((opaqueData.remaining() == 8 ? 1 : 0) != 0, (String)"Opaque data length must be equal 8");
        return new RawFrame(FrameType.PING.getValue(), FrameFlag.ACK.value, 0, opaqueData);
    }

    public RawFrame createGoAway(int lastStream, H2Error error, String message) {
        Args.notNegative((int)lastStream, (String)"Last stream id");
        byte[] debugData = message != null ? message.getBytes(StandardCharsets.US_ASCII) : null;
        ByteBuffer payload = ByteBuffer.allocate(8 + (debugData != null ? debugData.length : 0));
        payload.putInt(lastStream);
        payload.putInt(error.getCode());
        if (debugData != null) {
            payload.put(debugData);
        }
        payload.flip();
        return new RawFrame(FrameType.GOAWAY.getValue(), 0, 0, payload);
    }

    public abstract RawFrame createHeaders(int var1, ByteBuffer var2, boolean var3, boolean var4);

    public abstract RawFrame createContinuation(int var1, ByteBuffer var2, boolean var3);

    public abstract RawFrame createPushPromise(int var1, ByteBuffer var2, boolean var3);

    public abstract RawFrame createData(int var1, ByteBuffer var2, boolean var3);

    public RawFrame createWindowUpdate(int streamId, int increment) {
        Args.notNegative((int)streamId, (String)"Stream id");
        Args.positive((int)increment, (String)"Increment");
        ByteBuffer payload = ByteBuffer.allocate(4);
        payload.putInt(increment);
        payload.flip();
        return new RawFrame(FrameType.WINDOW_UPDATE.getValue(), 0, streamId, payload);
    }
}

