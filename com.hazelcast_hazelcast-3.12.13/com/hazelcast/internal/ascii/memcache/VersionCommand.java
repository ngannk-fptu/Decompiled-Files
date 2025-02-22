/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class VersionCommand
extends AbstractTextCommand {
    private static final byte[] VERSION = StringUtil.stringToBytes("VERSION Hazelcast\r\n");

    protected VersionCommand(TextCommandConstants.TextCommandType type) {
        super(type);
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        dst.put(VERSION);
        return true;
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }
}

