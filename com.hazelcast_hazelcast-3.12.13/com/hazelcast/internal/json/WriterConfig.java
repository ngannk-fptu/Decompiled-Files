/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.json;

import com.hazelcast.internal.json.JsonWriter;
import java.io.Writer;

public abstract class WriterConfig {
    public static final WriterConfig MINIMAL = new WriterConfig(){

        @Override
        JsonWriter createWriter(Writer writer) {
            return new JsonWriter(writer);
        }
    };

    abstract JsonWriter createWriter(Writer var1);
}

