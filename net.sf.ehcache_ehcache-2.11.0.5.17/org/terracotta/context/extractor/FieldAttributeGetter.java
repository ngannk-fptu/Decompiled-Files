/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.lang.reflect.Field;
import org.terracotta.context.extractor.AttributeGetter;

abstract class FieldAttributeGetter<T>
implements AttributeGetter<T> {
    private final Field field;

    FieldAttributeGetter(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    abstract Object target();

    @Override
    public T get() {
        try {
            return (T)this.field.get(this.target());
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}

