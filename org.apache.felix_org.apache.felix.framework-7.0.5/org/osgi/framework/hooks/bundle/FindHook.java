/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.bundle;

import java.util.Collection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@ConsumerType
public interface FindHook {
    public void find(BundleContext var1, Collection<Bundle> var2);
}

