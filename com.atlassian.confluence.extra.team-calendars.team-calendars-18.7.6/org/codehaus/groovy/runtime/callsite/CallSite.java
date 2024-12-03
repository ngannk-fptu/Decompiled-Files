/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public interface CallSite {
    public CallSiteArray getArray();

    public int getIndex();

    public String getName();

    public Object getProperty(Object var1) throws Throwable;

    public Object callGetPropertySafe(Object var1) throws Throwable;

    public Object callGetProperty(Object var1) throws Throwable;

    public Object callGroovyObjectGetProperty(Object var1) throws Throwable;

    public Object callGroovyObjectGetPropertySafe(Object var1) throws Throwable;

    public Object call(Object var1, Object[] var2) throws Throwable;

    public Object call(Object var1) throws Throwable;

    public Object call(Object var1, Object var2) throws Throwable;

    public Object call(Object var1, Object var2, Object var3) throws Throwable;

    public Object call(Object var1, Object var2, Object var3, Object var4) throws Throwable;

    public Object call(Object var1, Object var2, Object var3, Object var4, Object var5) throws Throwable;

    public Object callSafe(Object var1, Object[] var2) throws Throwable;

    public Object callSafe(Object var1) throws Throwable;

    public Object callSafe(Object var1, Object var2) throws Throwable;

    public Object callSafe(Object var1, Object var2, Object var3) throws Throwable;

    public Object callSafe(Object var1, Object var2, Object var3, Object var4) throws Throwable;

    public Object callSafe(Object var1, Object var2, Object var3, Object var4, Object var5) throws Throwable;

    public Object callCurrent(GroovyObject var1, Object[] var2) throws Throwable;

    public Object callCurrent(GroovyObject var1) throws Throwable;

    public Object callCurrent(GroovyObject var1, Object var2) throws Throwable;

    public Object callCurrent(GroovyObject var1, Object var2, Object var3) throws Throwable;

    public Object callCurrent(GroovyObject var1, Object var2, Object var3, Object var4) throws Throwable;

    public Object callCurrent(GroovyObject var1, Object var2, Object var3, Object var4, Object var5) throws Throwable;

    public Object callStatic(Class var1, Object[] var2) throws Throwable;

    public Object callStatic(Class var1) throws Throwable;

    public Object callStatic(Class var1, Object var2) throws Throwable;

    public Object callStatic(Class var1, Object var2, Object var3) throws Throwable;

    public Object callStatic(Class var1, Object var2, Object var3, Object var4) throws Throwable;

    public Object callStatic(Class var1, Object var2, Object var3, Object var4, Object var5) throws Throwable;

    public Object callConstructor(Object var1, Object[] var2) throws Throwable;

    public Object callConstructor(Object var1) throws Throwable;

    public Object callConstructor(Object var1, Object var2) throws Throwable;

    public Object callConstructor(Object var1, Object var2, Object var3) throws Throwable;

    public Object callConstructor(Object var1, Object var2, Object var3, Object var4) throws Throwable;

    public Object callConstructor(Object var1, Object var2, Object var3, Object var4, Object var5) throws Throwable;
}

