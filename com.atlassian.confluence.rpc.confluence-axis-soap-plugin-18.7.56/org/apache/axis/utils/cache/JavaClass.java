/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Hashtable;
import org.apache.axis.utils.cache.JavaMethod;

public class JavaClass
implements Serializable {
    private static Hashtable classes = new Hashtable();
    private Hashtable methods = new Hashtable();
    private Class jc;

    public static synchronized JavaClass find(Class jc) {
        JavaClass result = (JavaClass)classes.get(jc);
        if (result == null) {
            result = new JavaClass(jc);
            classes.put(jc, result);
        }
        return result;
    }

    public JavaClass(Class jc) {
        this.jc = jc;
        classes.put(jc, this);
    }

    public Class getJavaClass() {
        return this.jc;
    }

    public Method[] getMethod(String name) {
        JavaMethod jm = (JavaMethod)this.methods.get(name);
        if (jm == null) {
            jm = new JavaMethod(this.jc, name);
            this.methods.put(name, jm);
        }
        return jm.getMethod();
    }
}

