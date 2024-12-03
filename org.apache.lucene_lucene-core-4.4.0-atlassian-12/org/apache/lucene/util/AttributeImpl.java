/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeSource;

public abstract class AttributeImpl
implements Cloneable,
Attribute {
    public abstract void clear();

    public final String reflectAsString(final boolean prependAttClass) {
        final StringBuilder buffer = new StringBuilder();
        this.reflectWith(new AttributeReflector(){

            @Override
            public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                if (prependAttClass) {
                    buffer.append(attClass.getName()).append('#');
                }
                buffer.append(key).append('=').append(value == null ? "null" : value);
            }
        });
        return buffer.toString();
    }

    public void reflectWith(AttributeReflector reflector) {
        Class<?> clazz = this.getClass();
        LinkedList<WeakReference<Class<? extends Attribute>>> interfaces = AttributeSource.getAttributeInterfaces(clazz);
        if (interfaces.size() != 1) {
            throw new UnsupportedOperationException(clazz.getName() + " implements more than one Attribute interface, the default reflectWith() implementation cannot handle this.");
        }
        Class interf = (Class)interfaces.getFirst().get();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; ++i) {
                Field f = fields[i];
                if (Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                reflector.reflect(interf, f.getName(), f.get(this));
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void copyTo(AttributeImpl var1);

    public AttributeImpl clone() {
        AttributeImpl clone = null;
        try {
            clone = (AttributeImpl)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }
}

