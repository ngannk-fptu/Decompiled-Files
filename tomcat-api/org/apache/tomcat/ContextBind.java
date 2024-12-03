/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

public interface ContextBind {
    public ClassLoader bind(boolean var1, ClassLoader var2);

    public void unbind(boolean var1, ClassLoader var2);
}

