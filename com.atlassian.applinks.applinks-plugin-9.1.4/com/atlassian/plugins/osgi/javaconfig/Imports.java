/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cglib.core.ReflectUtils
 *  org.springframework.cglib.proxy.MethodProxy
 *  org.springframework.cglib.reflect.FastClass
 */
package com.atlassian.plugins.osgi.javaconfig;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;

final class Imports {
    private FastClass fastClass;
    private MethodProxy methodProxy;
    private ReflectUtils reflectUtils;

    private Imports() {
        throw new UnsupportedOperationException("This class is not for instantiation");
    }
}

