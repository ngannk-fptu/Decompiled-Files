/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.tracker;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.ServiceReference;

@ConsumerType
public interface ServiceTrackerCustomizer<S, T> {
    public T addingService(ServiceReference<S> var1);

    public void modifiedService(ServiceReference<S> var1, T var2);

    public void removedService(ServiceReference<S> var1, T var2);
}

