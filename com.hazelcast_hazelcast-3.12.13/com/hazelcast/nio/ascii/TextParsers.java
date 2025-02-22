/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PrivateApi
public class TextParsers {
    private final Map<String, CommandParser> parsers;
    private final Set<String> commandPrefixes;

    public TextParsers(Map<String, CommandParser> parsers) {
        this.parsers = new HashMap<String, CommandParser>(parsers);
        HashSet<String> prefixes = new HashSet<String>();
        for (String command : parsers.keySet()) {
            prefixes.add(command.substring(0, 3));
        }
        this.commandPrefixes = prefixes;
    }

    public CommandParser getParser(String command) {
        return command != null ? this.parsers.get(command) : null;
    }

    public boolean isCommandPrefix(String prefix) {
        return prefix != null ? this.commandPrefixes.contains(prefix) : false;
    }
}

