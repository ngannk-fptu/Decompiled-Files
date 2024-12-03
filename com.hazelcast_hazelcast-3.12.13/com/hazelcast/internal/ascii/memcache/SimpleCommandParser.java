/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TypeAwareCommandParser;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;
import com.hazelcast.internal.ascii.memcache.SimpleCommand;
import com.hazelcast.internal.ascii.memcache.StatsCommand;
import com.hazelcast.internal.ascii.memcache.VersionCommand;
import com.hazelcast.nio.ascii.TextDecoder;

public class SimpleCommandParser
extends TypeAwareCommandParser {
    public SimpleCommandParser(TextCommandConstants.TextCommandType type) {
        super(type);
    }

    @Override
    public TextCommand parser(TextDecoder decoder, String cmd, int space) {
        if (this.type == TextCommandConstants.TextCommandType.QUIT) {
            return new SimpleCommand(this.type);
        }
        if (this.type == TextCommandConstants.TextCommandType.STATS) {
            return new StatsCommand();
        }
        if (this.type == TextCommandConstants.TextCommandType.VERSION) {
            return new VersionCommand(this.type);
        }
        return new ErrorCommand(TextCommandConstants.TextCommandType.UNKNOWN);
    }
}

