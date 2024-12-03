/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.util;

import java.lang.reflect.Field;

public class FieldAccessibilityException
extends RuntimeException {
    public FieldAccessibilityException(Field field, Object target, Throwable cause) {
        super("Could not access '" + field.getName() + "' from '" + target + "'", cause);
    }
}

