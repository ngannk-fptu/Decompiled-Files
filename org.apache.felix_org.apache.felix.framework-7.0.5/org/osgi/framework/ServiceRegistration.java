/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import java.util.Dictionary;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;

@ProviderType
public interface ServiceRegistration<S> {
    public ServiceReference<S> getReference();

    public void setProperties(Dictionary<String, ?> var1);

    public void unregister();
}

