/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import org.codehaus.jettison.mapped.TypeConverter;

public class SimpleConverter
implements TypeConverter {
    @Override
    public Object convertToJSONPrimitive(String text) {
        return text;
    }
}

