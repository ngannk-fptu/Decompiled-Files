/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaClass;

public interface GroovyObject {
    public Object invokeMethod(String var1, Object var2);

    public Object getProperty(String var1);

    public void setProperty(String var1, Object var2);

    public MetaClass getMetaClass();

    public void setMetaClass(MetaClass var1);
}

