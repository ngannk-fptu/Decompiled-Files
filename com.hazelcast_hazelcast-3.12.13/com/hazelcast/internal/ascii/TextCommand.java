/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;
import java.nio.ByteBuffer;

public interface TextCommand
extends OutboundFrame {
    public TextCommandConstants.TextCommandType getType();

    public void init(TextDecoder var1, long var2);

    public TextDecoder getDecoder();

    public TextEncoder getEncoder();

    public long getRequestId();

    public boolean shouldReply();

    public boolean readFrom(ByteBuffer var1);

    public boolean writeTo(ByteBuffer var1);
}

