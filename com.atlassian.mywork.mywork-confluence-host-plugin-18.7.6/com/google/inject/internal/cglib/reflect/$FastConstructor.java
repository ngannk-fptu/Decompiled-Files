/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.reflect;

import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.cglib.reflect.$FastMember;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class $FastConstructor
extends $FastMember {
    $FastConstructor($FastClass fc, Constructor constructor) {
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

