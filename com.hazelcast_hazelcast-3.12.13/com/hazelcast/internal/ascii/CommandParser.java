/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.nio.ascii.TextDecoder;

public interface CommandParser {
    public TextCommand parser(TextDecoder var1, String var2, int var3);
}

