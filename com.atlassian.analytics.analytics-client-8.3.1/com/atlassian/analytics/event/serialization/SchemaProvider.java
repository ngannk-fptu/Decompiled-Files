/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.event.serialization;

import java.io.IOException;
import org.apache.avro.Schema;

public interface SchemaProvider {
    public Schema get(int var1) throws IOException;
}

