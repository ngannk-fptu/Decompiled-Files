/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TypeAwareCommandParser;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;
import com.hazelcast.internal.ascii.memcache.SetCommand;
import com.hazelcast.nio.ascii.TextDecoder;
import java.util.StringTokenizer;

public class SetCommandParser
extends TypeAwareCommandParser {
    public SetCommandParser(TextCommandConstants.TextCommandType type) {
        super(type);
    }

    @Override
    public TextCommand parser(TextDecoder decoder, String cmd, int space) {
        StringTokenizer st = new StringTokenizer(cmd);
        st.nextToken();
        boolean noReply = false;
        if (!st.hasMoreTokens()) {
            return new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT);
        }
        String key = st.nextToken();
        if (!st.hasMoreTokens()) {
            return new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT);
        }
        int flag = Integer.parseInt(st.nextToken());
        if (!st.hasMoreTokens()) {
            return new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT);
        }
        int expiration = Integer.parseInt(st.nextToken());
        if (!st.hasMoreTokens()) {
            return new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT);
        }
        int valueLen = Integer.parseInt(st.nextToken());
        if (st.hasMoreTokens()) {
            noReply = "noreply".equals(st.nextToken());
        }
        return new SetCommand(this.type, key, flag, expiration, valueLen, noReply);
    }
}

