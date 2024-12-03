/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;

@ProviderType
public interface ServiceObjects<S> {
    public S getService();

    public void ungetService(S var1);

    public ServiceReference<S> getServiceReference();
}

