/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaProperty;
import groovy.lang.ReadOnlyPropertyException;
import java.lang.reflect.Array;

public class MetaArrayLengthProperty
extends MetaProperty {
    public MetaArrayLengthProperty() {
        super("length", Integer.TYPE);
    }

    @Override
    public Object getProperty(Object object) {
        return Array.getLength(object);
    }

    @Override
    public void setProperty(Object object, Object newValue) {
        throw new ReadOnlyPropertyException("length", object.getClass());
    }
}

