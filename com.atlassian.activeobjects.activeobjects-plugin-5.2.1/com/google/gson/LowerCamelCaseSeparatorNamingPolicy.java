/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.CamelCaseSeparatorNamingPolicy;
import com.google.gson.CompositionFieldNamingPolicy;
import com.google.gson.LowerCaseNamingPolicy;

final class LowerCamelCaseSeparatorNamingPolicy
extends CompositionFieldNamingPolicy {
    public LowerCamelCaseSeparatorNamingPolicy(String separatorString) {
        super(new CamelCaseSeparatorNamingPolicy(separatorString), new LowerCaseNamingPolicy());
    }
}

