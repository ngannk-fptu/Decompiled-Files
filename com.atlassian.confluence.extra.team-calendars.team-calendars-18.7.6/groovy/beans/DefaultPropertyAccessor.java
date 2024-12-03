/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.DefaultPropertyReader;
import groovy.beans.DefaultPropertyWriter;
import groovy.beans.PropertyAccessor;

public class DefaultPropertyAccessor
implements PropertyAccessor {
    public static final PropertyAccessor INSTANCE = new DefaultPropertyAccessor();

    @Override
    public Object read(Object owner, String propertyName) {
        return DefaultPropertyReader.INSTANCE.read(owner, propertyName);
    }

    @Override
    public void write(Object owner, String propertyName, Object value) {
        DefaultPropertyWriter.INSTANCE.write(owner, propertyName, value);
    }
}

