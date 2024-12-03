/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.internal.json.JsonReducedValueParser;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import java.io.IOException;

public abstract class NavigableJsonInputAdapter {
    public abstract void position(int var1);

    public abstract int position();

    public abstract void reset();

    public abstract boolean isAttributeName(JsonPathCursor var1);

    public abstract JsonValue parseValue(JsonReducedValueParser var1, int var2) throws IOException;

    public abstract JsonParser createParser(JsonFactory var1) throws IOException;
}

