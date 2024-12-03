/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import java.util.EventListener;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.ServiceEvent;

@FunctionalInterface
@ConsumerType
public interface ServiceListener
extends EventListener {
    public void serviceChanged(ServiceEvent var1);
}

