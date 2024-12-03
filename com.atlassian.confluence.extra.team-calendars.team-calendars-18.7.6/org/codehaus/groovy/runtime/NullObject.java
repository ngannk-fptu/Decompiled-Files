/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.Collections;
import java.util.Iterator;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class NullObject
extends GroovyObjectSupport {
    private static final NullObject INSTANCE = new NullObject();

    private NullObject() {
    }

    public static NullObject getNullObject() {
        return INSTANCE;
    }

    public Object clone() {
        throw new NullPointerException("Cannot invoke method clone() on null object");
    }

    @Override
    public Object getProperty(String property) {
        throw new NullPointerException("Cannot get property '" + property + "' on null object");
    }

    public <T> T with(Closure<T> closure) {
        return DefaultGroovyMethods.with(null, closure);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        throw new NullPointerException("Cannot set property '" + property + "' on null object");
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        throw new NullPointerException("Cannot invoke method " + name + "() on null object");
    }

    public boolean equals(Object to) {
        return to == null;
    }

    public Iterator iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    public Object plus(String s) {
        return this.getMetaClass().invokeMethod((Object)this, "toString", new Object[0]) + s;
    }

    public Object plus(Object o) {
        throw new NullPointerException("Cannot execute null+" + String.valueOf(o));
    }

    public boolean is(Object other) {
        return other == null;
    }

    public Object asType(Class c) {
        return null;
    }

    public boolean asBoolean() {
        return false;
    }

    public String toString() {
        return "null";
    }

    public int hashCode() {
        throw new NullPointerException("Cannot invoke method hashCode() on null object");
    }
}

