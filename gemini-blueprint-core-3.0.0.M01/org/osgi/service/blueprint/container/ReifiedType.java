/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

public class ReifiedType {
    private static final ReifiedType OBJECT = new ReifiedType(Object.class);
    private final Class clazz;

    public ReifiedType(Class clazz) {
        this.clazz = clazz;
    }

    public Class getRawClass() {
        return this.clazz;
    }

    public ReifiedType getActualTypeArgument(int i) {
        return OBJECT;
    }

    public int size() {
        return 0;
    }
}

