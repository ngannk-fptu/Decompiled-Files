/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.RecursiveFieldNamingPolicy;
import com.google.gson.internal.$Gson$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class CamelCaseSeparatorNamingPolicy
extends RecursiveFieldNamingPolicy {
    private final String separatorString;

    public CamelCaseSeparatorNamingPolicy(String separatorString) {
        $Gson$Preconditions.checkNotNull(separatorString);
        $Gson$Preconditions.checkArgument(!"".equals(separatorString));
        this.separatorString = separatorString;
    }

    @Override
    protected String translateName(String target, Type fieldType, Collection<Annotation> annnotations) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < target.length(); ++i) {
            char character = target.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(this.separatorString);
            }
            translation.append(character);
        }
        return translation.toString();
    }
}

