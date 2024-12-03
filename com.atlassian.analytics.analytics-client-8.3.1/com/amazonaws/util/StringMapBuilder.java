/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.ImmutableMapParameter;

public class StringMapBuilder
extends ImmutableMapParameter.Builder<String, String> {
    public StringMapBuilder() {
    }

    public StringMapBuilder(String key, String value) {
        super.put(key, value);
    }
}

