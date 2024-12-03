/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import org.osgi.framework.Bundle;

interface BundleClassLoaderFactory {
    public ClassLoader createClassLoader(Bundle var1);
}

