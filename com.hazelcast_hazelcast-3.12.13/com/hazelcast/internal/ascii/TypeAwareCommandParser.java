/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommandConstants;

public abstract class TypeAwareCommandParser
implements CommandParser {
    protected final TextCommandConstants.TextCommandType type;

    protected TypeAwareCommandParser(TextCommandConstants.TextCommandType type) {
        this.type = type;
    }
}

