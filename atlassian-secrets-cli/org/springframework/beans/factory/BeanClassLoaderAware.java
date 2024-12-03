/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.Aware;

public interface BeanClassLoaderAware
extends Aware {
    public void setBeanClassLoader(ClassLoader var1);
}

