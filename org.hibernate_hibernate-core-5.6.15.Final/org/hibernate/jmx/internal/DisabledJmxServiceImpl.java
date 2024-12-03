/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jmx.internal;

import javax.management.ObjectName;
import org.hibernate.jmx.spi.JmxService;
import org.hibernate.service.spi.Manageable;

public class DisabledJmxServiceImpl
implements JmxService {
    public static final DisabledJmxServiceImpl INSTANCE = new DisabledJmxServiceImpl();

    public void registerService(Manageable service, Class serviceRole) {
    }

    @Override
    public void registerMBean(ObjectName objectName, Object mBean) {
    }
}

