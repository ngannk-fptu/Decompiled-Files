/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.Project;

public interface TypeAdapter {
    public void setProject(Project var1);

    public Project getProject();

    public void setProxy(Object var1);

    public Object getProxy();

    public void checkProxyClass(Class<?> var1);
}

