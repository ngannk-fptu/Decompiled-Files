/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.AbstractMultiValueGetter;
import com.hazelcast.query.impl.getters.Getter;
import java.lang.reflect.Field;

public class FieldGetter
extends AbstractMultiValueGetter {
    private final Field field;

    public FieldGetter(Getter parent, Field field, String modifierSuffix, Class resultType) {
        super(parent, modifierSuffix, field.getType(), resultType);
        this.field = field;
    }

    @Override
    protected Object extractFrom(Object object) throws IllegalAccessException {
        try {
            return this.field.get(object);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(FieldGetter.composeAttributeValueExtractionFailedMessage(this.field), e);
        }
    }

    @Override
    boolean isCacheable() {
        return true;
    }

    public String toString() {
        return "FieldGetter [parent=" + this.parent + ", field=" + this.field + ", modifier = " + this.getModifier() + "]";
    }
}

