/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldNamingStrategy2;
import com.google.gson.LowerCamelCaseSeparatorNamingPolicy;
import com.google.gson.ModifyFirstLetterNamingPolicy;
import com.google.gson.UpperCamelCaseSeparatorNamingPolicy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldNamingPolicy {
    UPPER_CAMEL_CASE(new ModifyFirstLetterNamingPolicy(ModifyFirstLetterNamingPolicy.LetterModifier.UPPER)),
    UPPER_CAMEL_CASE_WITH_SPACES(new UpperCamelCaseSeparatorNamingPolicy(" ")),
    LOWER_CASE_WITH_UNDERSCORES(new LowerCamelCaseSeparatorNamingPolicy("_")),
    LOWER_CASE_WITH_DASHES(new LowerCamelCaseSeparatorNamingPolicy("-"));

    private final FieldNamingStrategy2 namingPolicy;

    private FieldNamingPolicy(FieldNamingStrategy2 namingPolicy) {
        this.namingPolicy = namingPolicy;
    }

    FieldNamingStrategy2 getFieldNamingPolicy() {
        return this.namingPolicy;
    }
}

