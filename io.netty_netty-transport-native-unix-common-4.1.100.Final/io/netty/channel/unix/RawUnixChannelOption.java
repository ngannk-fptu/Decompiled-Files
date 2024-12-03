/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.unix;

import io.netty.channel.unix.GenericUnixChannelOption;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

public final class RawUnixChannelOption
extends GenericUnixChannelOption<ByteBuffer> {
    private final int length;

    public RawUnixChannelOption(String name, int level, int optname, int length) {
        super(name, level, optname);
        this.length = ObjectUtil.checkPositive((int)length, (String)"length");
    }

    public int length() {
        return this.length;
    }

    public void validate(ByteBuffer value) {
        super.validate((Object)value);
        if (value.remaining() != this.length) {
            throw new IllegalArgumentException("Length of value does not match. Expected " + this.length + ", but got " + value.remaining());
        }
    }
}

