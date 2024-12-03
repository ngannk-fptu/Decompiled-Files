/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.Module
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.SerializationFeature
 */
package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JodaMapper
extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public JodaMapper() {
        this.registerModule((Module)new JodaModule());
    }

    public boolean getWriteDatesAsTimestamps() {
        return this.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void setWriteDatesAsTimestamps(boolean state) {
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, state);
    }
}

