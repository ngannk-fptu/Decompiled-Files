/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.memcache.DeleteCommandParser;
import com.hazelcast.internal.ascii.memcache.GetCommandParser;
import com.hazelcast.internal.ascii.memcache.IncrementCommandParser;
import com.hazelcast.internal.ascii.memcache.SetCommandParser;
import com.hazelcast.internal.ascii.memcache.SimpleCommandParser;
import com.hazelcast.internal.ascii.memcache.TouchCommandParser;
import com.hazelcast.nio.ascii.AllowingTextProtocolFilter;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.ascii.TextParsers;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.HashMap;

@PrivateApi
public class MemcacheTextDecoder
extends TextDecoder {
    public static final TextParsers TEXT_PARSERS;

    public MemcacheTextDecoder(TcpIpConnection connection, TextEncoder encoder, boolean rootDecoder) {
        super(connection, encoder, AllowingTextProtocolFilter.INSTANCE, TEXT_PARSERS, rootDecoder);
    }

    static {
        HashMap<String, CommandParser> parsers = new HashMap<String, CommandParser>();
        parsers.put("get", new GetCommandParser());
        parsers.put("gets", new GetCommandParser());
        parsers.put("set", new SetCommandParser(TextCommandConstants.TextCommandType.SET));
        parsers.put("add", new SetCommandParser(TextCommandConstants.TextCommandType.ADD));
        parsers.put("replace", new SetCommandParser(TextCommandConstants.TextCommandType.REPLACE));
        parsers.put("append", new SetCommandParser(TextCommandConstants.TextCommandType.APPEND));
        parsers.put("prepend", new SetCommandParser(TextCommandConstants.TextCommandType.PREPEND));
        parsers.put("touch", new TouchCommandParser(TextCommandConstants.TextCommandType.TOUCH));
        parsers.put("incr", new IncrementCommandParser(TextCommandConstants.TextCommandType.INCREMENT));
        parsers.put("decr", new IncrementCommandParser(TextCommandConstants.TextCommandType.DECREMENT));
        parsers.put("delete", new DeleteCommandParser());
        parsers.put("quit", new SimpleCommandParser(TextCommandConstants.TextCommandType.QUIT));
        parsers.put("stats", new SimpleCommandParser(TextCommandConstants.TextCommandType.STATS));
        parsers.put("version", new SimpleCommandParser(TextCommandConstants.TextCommandType.VERSION));
        TEXT_PARSERS = new TextParsers(parsers);
    }
}

