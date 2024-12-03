/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry;

import java.util.Map;
import org.hibernate.service.Service;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public interface StandardServiceInitiator<R extends Service>
extends ServiceInitiator<R> {
    public R initiateService(Map var1, ServiceRegistryImplementor var2);
}

