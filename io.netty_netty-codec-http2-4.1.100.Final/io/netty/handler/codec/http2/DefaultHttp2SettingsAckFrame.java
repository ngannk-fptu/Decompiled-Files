/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2SettingsAckFrame;
import io.netty.util.internal.StringUtil;

final class DefaultHttp2SettingsAckFrame
implements Http2SettingsAckFrame {
    DefaultHttp2SettingsAckFrame() {
    }

    @Override
    public String name() {
        return "SETTINGS(ACK)";
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this);
    }
}

