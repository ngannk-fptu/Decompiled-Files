/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.locator;

import org.apache.axiom.locator.Loader;

final class DefaultLoader
extends Loader {
    private final ClassLoader classLoader;

    DefaultLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    Class load(String className) throws ClassNotFoundException {
        return this.classLoader.loadClass(className);
    }
}

