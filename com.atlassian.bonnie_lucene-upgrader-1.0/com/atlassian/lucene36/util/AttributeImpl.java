/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.Attribute;
import com.atlassian.lucene36.util.AttributeReflector;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.VirtualMethod;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AttributeImpl
implements Cloneable,
Serializable,
Attribute {
    @Deprecated
    private static final VirtualMethod<AttributeImpl> toStringMethod = new VirtualMethod<AttributeImpl>(AttributeImpl.class, "toString", new Class[0]);
    @Deprecated
    protected boolean enableBackwards = true;

    public abstract void clear();

    public String toString() {
        return this.reflectAsString(false);
    }

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

    @Deprecated
    private boolean assertExternalClass(Class<? extends AttributeImpl> clazz) {
        String name = clazz.getName();
        return !name.startsWith("com.atlassian.lucene36.") && !name.startsWith("org.apache.solr.") || name.equals("com.atlassian.lucene36.util.TestAttributeSource$TestAttributeImpl");
    }

    public void reflectWith(AttributeReflector reflector) {
        Class<?> clazz = this.getClass();
        LinkedList<WeakReference<Class<? extends Attribute>>> interfaces = AttributeSource.getAttributeInterfaces(clazz);
        if (interfaces.size() != 1) {
            throw new UnsupportedOperationException(clazz.getName() + " implements more than one Attribute interface, the default reflectWith() implementation cannot handle this.");
        }
        Class interf = (Class)interfaces.getFirst().get();
        if (this.enableBackwards && toStringMethod.isOverriddenAsOf(clazz)) {
            assert (this.assertExternalClass(clazz)) : "no Lucene/Solr classes should fallback to toString() parsing";
            for (String part : this.toString().split(",")) {
                int pos = part.indexOf(61);
                if (pos < 0) {
                    throw new UnsupportedOperationException("The backwards compatibility layer to support reflectWith() on old AtributeImpls expects the toString() implementation to return a correct format as specified for method reflectAsString(false)");
                }
                reflector.reflect(interf, part.substring(0, pos).trim(), part.substring(pos + 1));
            }
            return;
        }
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

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }
}

