/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import org.eclipse.gemini.blueprint.context.support.internal.classloader.ChainedClassLoader;

interface InternalAopClassLoaderFactory {
    public ChainedClassLoader createClassLoader(ClassLoader var1);
}

