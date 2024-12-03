/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.osgi.framework.BundleContext;

public interface TypeCompatibilityChecker {
    public boolean isTypeCompatible(BundleContext var1);
}

