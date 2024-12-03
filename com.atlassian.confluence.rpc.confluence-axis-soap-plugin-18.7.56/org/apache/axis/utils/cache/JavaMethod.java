/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.cache;

import java.lang.reflect.Method;
import java.util.Vector;

public class JavaMethod {
    private Method[] methods = null;

    public JavaMethod(Class jc, String name) {
        Method[] methods = jc.getMethods();
        Vector<Method> workinglist = new Vector<Method>();
        for (int i = 0; i < methods.length; ++i) {
            if (!methods[i].getName().equals(name)) continue;
            workinglist.addElement(methods[i]);
        }
        if (workinglist.size() > 0) {
            this.methods = new Method[workinglist.size()];
            workinglist.copyInto(this.methods);
        }
    }

    public Method[] getMethod() {
        return this.methods;
    }
}

