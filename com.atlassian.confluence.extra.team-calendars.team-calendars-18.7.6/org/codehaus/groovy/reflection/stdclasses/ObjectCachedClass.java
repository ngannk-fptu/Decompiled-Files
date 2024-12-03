/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;

public class ObjectCachedClass
extends CachedClass {
    public ObjectCachedClass(ClassInfo classInfo) {
        super(Object.class, classInfo);
    }

    @Override
    public synchronized CachedClass getCachedSuperClass() {
        return null;
    }

    @Override
    public boolean isAssignableFrom(Class argument) {
        return true;
    }
}

