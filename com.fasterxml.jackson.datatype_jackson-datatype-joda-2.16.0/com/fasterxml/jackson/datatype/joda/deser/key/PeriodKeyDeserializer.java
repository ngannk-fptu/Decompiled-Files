/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationContext
 */
package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.deser.key.JodaKeyDeserializer;
import java.io.IOException;

public class PeriodKeyDeserializer
extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        return PERIOD_FORMAT.parsePeriod(ctxt, key);
    }
}

