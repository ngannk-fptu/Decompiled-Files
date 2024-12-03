/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.jta.platform.spi;

import java.util.Map;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.service.Service;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public interface JtaPlatformResolver
extends Service {
    public JtaPlatform resolveJtaPlatform(Map var1, ServiceRegistryImplementor var2);
}

