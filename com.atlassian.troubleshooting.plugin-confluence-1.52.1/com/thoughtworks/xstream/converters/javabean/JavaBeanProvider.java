/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.javabean;

public interface JavaBeanProvider {
    public Object newInstance(Class var1);

    public void visitSerializableProperties(Object var1, Visitor var2);

    public void writeProperty(Object var1, String var2, Object var3);

    public Class getPropertyType(Object var1, String var2);

    public boolean propertyDefinedInClass(String var1, Class var2);

    public boolean canInstantiate(Class var1);

    public static interface Visitor {
        public boolean shouldVisit(String var1, Class var2);

        public void visit(String var1, Class var2, Class var3, Object var4);
    }
}

