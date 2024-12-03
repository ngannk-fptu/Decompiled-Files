/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator;

import org.eclipse.gemini.blueprint.extender.internal.activator.TypeCompatibilityChecker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class BlueprintTypeCompatibilityChecker
implements TypeCompatibilityChecker {
    private static final String CONTAINER_PKG_CLASS = "org.osgi.service.blueprint.container.BlueprintContainer";
    private static final String REFLECT_PKG_CLASS = "org.osgi.service.blueprint.reflect.ComponentMetadata";
    private final Class<?> containerPkgClass;
    private final Class<?> reflectPkgClass;

    public BlueprintTypeCompatibilityChecker(Bundle extenderBundle) {
        try {
            this.containerPkgClass = extenderBundle.loadClass(CONTAINER_PKG_CLASS);
            this.reflectPkgClass = extenderBundle.loadClass(REFLECT_PKG_CLASS);
        }
        catch (ClassNotFoundException cnf) {
            throw new IllegalStateException("Cannot load blueprint classes " + cnf);
        }
    }

    @Override
    public boolean isTypeCompatible(BundleContext targetContext) {
        Bundle bnd = targetContext.getBundle();
        return this.checkCompatibility(CONTAINER_PKG_CLASS, bnd, this.containerPkgClass) && this.checkCompatibility(REFLECT_PKG_CLASS, bnd, this.reflectPkgClass);
    }

    private boolean checkCompatibility(String of, Bundle in, Class<?> against) {
        try {
            Class found = in.loadClass(of);
            return against.equals(found);
        }
        catch (ClassNotFoundException cnf) {
            return true;
        }
    }
}

