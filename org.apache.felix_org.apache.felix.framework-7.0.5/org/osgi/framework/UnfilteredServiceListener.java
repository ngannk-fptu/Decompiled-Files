/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.ServiceListener;

@FunctionalInterface
@ConsumerType
public interface UnfilteredServiceListener
extends ServiceListener {
}

