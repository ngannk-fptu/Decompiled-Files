/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.cursor.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.jdbc.cursor.internal.StandardRefCursorSupport;
import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class RefCursorSupportInitiator
implements StandardServiceInitiator<RefCursorSupport> {
    public static final RefCursorSupportInitiator INSTANCE = new RefCursorSupportInitiator();

    @Override
    public RefCursorSupport initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new StandardRefCursorSupport();
    }

    @Override
    public Class<RefCursorSupport> getServiceInitiated() {
        return RefCursorSupport.class;
    }
}

