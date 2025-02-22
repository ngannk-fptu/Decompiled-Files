/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.memcache.DeleteCommand;
import com.hazelcast.nio.ascii.TextDecoder;
import java.util.StringTokenizer;

public class DeleteCommandParser
implements CommandParser {
    @Override
    public TextCommand parser(TextDecoder decoder, String cmd, int space) {
        StringTokenizer st = new StringTokenizer(cmd);
        st.nextToken();
        String key = null;
        int expiration = 0;
        boolean noReply = false;
        if (st.hasMoreTokens()) {
            key = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            expiration = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
            noReply = "noreply".equals(st.nextToken());
        }
        return new DeleteCommand(key, expiration, noReply);
    }
}

