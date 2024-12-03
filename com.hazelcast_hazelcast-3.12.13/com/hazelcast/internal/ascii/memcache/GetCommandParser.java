/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.memcache.BulkGetCommand;
import com.hazelcast.internal.ascii.memcache.GetCommand;
import com.hazelcast.nio.ascii.TextDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GetCommandParser
implements CommandParser {
    @Override
    public TextCommand parser(TextDecoder decoder, String cmd, int space) {
        String key = cmd.substring(space + 1);
        if (key.indexOf(32) == -1) {
            GetCommand r = new GetCommand(key);
            decoder.publishRequest(r);
        } else {
            StringTokenizer st = new StringTokenizer(key);
            ArrayList<String> keys = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                String singleKey = st.nextToken();
                keys.add(singleKey);
            }
            decoder.publishRequest(new BulkGetCommand(keys));
        }
        return null;
    }
}

