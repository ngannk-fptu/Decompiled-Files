/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.AccessPattern;

public interface NullValueProvider {
    public Object getNullValue(DeserializationContext var1) throws JsonMappingException;

    public AccessPattern getNullAccessPattern();
}

