/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;

public interface Jsonator<T> {
    public Json convert(T var1);
}

