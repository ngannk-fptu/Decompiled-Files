/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.KeyDeserializer
 */
package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
import java.io.IOException;
import java.io.Serializable;

abstract class JodaKeyDeserializer
extends KeyDeserializer
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final JacksonJodaPeriodFormat PERIOD_FORMAT = FormatConfig.DEFAULT_PERIOD_FORMAT;

    JodaKeyDeserializer() {
    }

    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key.length() == 0) {
            return null;
        }
        return this.deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String var1, DeserializationContext var2) throws IOException;
}

