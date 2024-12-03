/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMember;

public class FastConstructor
extends FastMember {
    FastConstructor(FastClass fc, Constructor constructor) {
        super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
    }

    public Class[] getParameterTypes() {
        return ((Constructor)this.member).getParameterTypes();
    }

    public Class[] getExceptionTypes() {
        return ((Constructor)this.member).getExceptionTypes();
    }

    public Object newInstance() throws InvocationTargetException {
        return this.fc.newInstance(this.index, null);
    }

    public Object newInstance(Object[] args) throws InvocationTargetException {
        return this.fc.newInstance(this.index, args);
    }

    public Constructor getJavaConstructor() {
        return (Constructor)this.member;
    }
}

