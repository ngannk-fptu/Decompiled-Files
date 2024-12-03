/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.search.query;

import java.util.Arrays;
import org.apache.commons.lang3.Validate;

public class QueryUtils {
    public static <U> Class<U> checkAssignableFrom(Class<U> givenType, Class<?> ... types) {
        Validate.notNull(givenType);
        Validate.notNull(types);
        for (Class<U> clazz : types) {
            if (clazz == null || !clazz.isAssignableFrom(givenType)) continue;
            return givenType;
        }
        throw new IllegalArgumentException("Given type (" + givenType.getName() + ") must be assignable from one of " + Arrays.toString(types));
    }
}

