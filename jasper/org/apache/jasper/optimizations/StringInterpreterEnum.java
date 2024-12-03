/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.optimizations;

import org.apache.jasper.compiler.StringInterpreterFactory;

public class StringInterpreterEnum
extends StringInterpreterFactory.DefaultStringInterpreter {
    @Override
    protected String coerceToOtherType(Class<?> c, String s, boolean isNamedAttribute) {
        if (c.isEnum() && !isNamedAttribute) {
            Object enumValue = Enum.valueOf(c, s);
            return c.getName() + "." + ((Enum)enumValue).name();
        }
        return null;
    }
}

