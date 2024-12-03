/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import java.util.EventListener;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.FrameworkEvent;

@FunctionalInterface
@ConsumerType
public interface FrameworkListener
extends EventListener {
    public void frameworkEvent(FrameworkEvent var1);
}

