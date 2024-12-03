/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

@ConsumerType
public interface PrototypeServiceFactory<S>
extends ServiceFactory<S> {
    @Override
    public S getService(Bundle var1, ServiceRegistration<S> var2);

    @Override
    public void ungetService(Bundle var1, ServiceRegistration<S> var2, S var3);
}

