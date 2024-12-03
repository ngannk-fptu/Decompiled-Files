/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.apache.axiom.locator;

import org.apache.axiom.locator.Loader;
import org.osgi.framework.Bundle;

final class OSGiLoader
extends Loader {
    private final Bundle bundle;

    OSGiLoader(Bundle bundle) {
        this.bundle = bundle;
    }

    Class load(String className) throws ClassNotFoundException {
        return this.bundle.loadClass(className);
    }
}

