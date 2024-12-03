/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.json;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.JsonWriter;
import java.io.IOException;

public class NonTerminalJsonValue
extends JsonValue {
    public static final NonTerminalJsonValue INSTANCE = new NonTerminalJsonValue();

    @Override
    void write(JsonWriter writer) throws IOException {
        throw new HazelcastException("This object should not be encoded");
    }
}

