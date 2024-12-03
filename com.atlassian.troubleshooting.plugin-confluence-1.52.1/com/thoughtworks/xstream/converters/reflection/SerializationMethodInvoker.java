/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationMethodInvoker
implements Caching {
    SerializationMembers serializationMembers = new SerializationMembers();

    public Object callReadResolve(Object result) {
        return this.serializationMembers.callReadResolve(result);
    }

    public Object callWriteReplace(Object object) {
        return this.serializationMembers.callWriteReplace(object);
    }

    public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
        return this.serializationMembers.supportsReadObject(type, includeBaseClasses);
    }

    public void callReadObject(Class type, Object object, ObjectInputStream stream) {
        this.serializationMembers.callReadObject(type, object, stream);
    }

    public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
        return this.serializationMembers.supportsWriteObject(type, includeBaseClasses);
    }

    public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
        this.serializationMembers.callWriteObject(type, instance, stream);
    }

    public void flushCache() {
        this.serializationMembers.flushCache();
    }
}

