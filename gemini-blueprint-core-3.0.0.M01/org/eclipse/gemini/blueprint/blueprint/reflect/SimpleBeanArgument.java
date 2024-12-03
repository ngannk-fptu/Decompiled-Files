/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.ValueFactory;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.Metadata;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

class SimpleBeanArgument
implements BeanArgument {
    private final int index;
    private final String typeName;
    private final Metadata value;
    private static final int UNSPECIFIED_INDEX = -1;

    public SimpleBeanArgument(int index, ConstructorArgumentValues.ValueHolder valueHolder) {
        this.index = index;
        this.typeName = valueHolder.getType();
        this.value = ValueFactory.buildValue(MetadataUtils.getValue(valueHolder));
    }

    public SimpleBeanArgument(ConstructorArgumentValues.ValueHolder valueHolder) {
        this(-1, valueHolder);
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Metadata getValue() {
        return this.value;
    }

    @Override
    public String getValueType() {
        return this.typeName;
    }
}

