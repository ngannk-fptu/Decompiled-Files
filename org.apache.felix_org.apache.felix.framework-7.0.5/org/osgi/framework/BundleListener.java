/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import java.util.EventListener;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleEvent;

@FunctionalInterface
@ConsumerType
public interface BundleListener
extends EventListener {
    public void bundleChanged(BundleEvent var1);
}

