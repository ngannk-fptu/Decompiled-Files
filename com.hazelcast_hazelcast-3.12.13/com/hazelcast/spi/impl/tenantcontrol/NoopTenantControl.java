/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.tenantcontrol;

import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import java.io.Closeable;

@SerializableByConvention
public final class NoopTenantControl
implements TenantControl {
    @Override
    public Closeable setTenant(boolean createRequestScope) {
        return NoopCloseable.INSTANCE;
    }

    @Override
    public void unregister() {
    }

    public boolean equals(Object obj) {
        return obj instanceof NoopTenantControl;
    }

    public int hashCode() {
        return 0;
    }

    private static final class NoopCloseable
    implements Closeable {
        static final NoopCloseable INSTANCE = new NoopCloseable();

        private NoopCloseable() {
        }

        @Override
        public void close() {
        }
    }
}

