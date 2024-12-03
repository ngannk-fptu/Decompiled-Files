/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;
import com.hazelcast.internal.ascii.rest.HttpGetCommand;
import com.hazelcast.nio.ascii.TextDecoder;
import java.util.StringTokenizer;

public class HttpGetCommandParser
implements CommandParser {
    @Override
    public TextCommand parser(TextDecoder decoder, String cmd, int space) {
        StringTokenizer st = new StringTokenizer(cmd);
        st.nextToken();
        if (!st.hasMoreTokens()) {
            return new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT);
        }
        String uri = st.nextToken();
        return new HttpGetCommand(uri);
    }
}

