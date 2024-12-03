/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jmx.spi;

import javax.management.ObjectName;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Manageable;

@Deprecated
public interface JmxService
extends Service {
    public void registerService(Manageable var1, Class<? extends Service> var2);

    public void registerMBean(ObjectName var1, Object var2);
}

