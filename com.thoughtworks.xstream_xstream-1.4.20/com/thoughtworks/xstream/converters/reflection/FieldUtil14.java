/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import java.lang.reflect.Field;

class FieldUtil14
implements FieldDictionary.FieldUtil {
    FieldUtil14() {
    }

    public boolean isSynthetic(Field field) {
        return false;
    }
}

