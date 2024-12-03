/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import org.hibernate.annotations.common.reflection.ClassLoadingException;

@Deprecated
public interface ClassLoaderDelegate {
    public <T> Class<T> classForName(String var1) throws ClassLoadingException;
}

