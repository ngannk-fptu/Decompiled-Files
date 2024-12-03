/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleListener;

@FunctionalInterface
@ConsumerType
public interface SynchronousBundleListener
extends BundleListener {
}

