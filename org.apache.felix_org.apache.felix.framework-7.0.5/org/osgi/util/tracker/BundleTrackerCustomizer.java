/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.tracker;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

@ConsumerType
public interface BundleTrackerCustomizer<T> {
    public T addingBundle(Bundle var1, BundleEvent var2);

    public void modifiedBundle(Bundle var1, BundleEvent var2, T var3);

    public void removedBundle(Bundle var1, BundleEvent var2, T var3);
}

