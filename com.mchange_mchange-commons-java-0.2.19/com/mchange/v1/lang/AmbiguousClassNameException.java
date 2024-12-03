/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

public class AmbiguousClassNameException
extends Exception {
    AmbiguousClassNameException(String string, Class clazz, Class clazz2) {
        super(string + " could refer either to " + clazz.getName() + " or " + clazz2.getName());
    }
}

