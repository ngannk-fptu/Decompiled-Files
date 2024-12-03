/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TypeAwareCommandParser;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;
import com.hazelcast.internal.ascii.memcache.IncrementCommand;
import com.hazelcast.nio.ascii.TextDecoder;
import java.util.StringTokenizer;

public class IncrementCommandParser
extends TypeAwareCommandParser {
    public IncrementCommandParser(TextCommandConstants.TextCommandType type) {
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
        int value = Integer.parseInt(st.nextToken());
        if (st.hasMoreTokens()) {
            noReply = "noreply".equals(st.nextToken());
        }
        return new IncrementCommand(this.type, key, value, noReply);
    }
}

