/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Driver;
import java.util.Map;
import java.util.Properties;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

interface ConnectionCreatorFactory {
    public ConnectionCreator create(Driver var1, ServiceRegistryImplementor var2, String var3, Properties var4, Boolean var5, Integer var6, String var7, Map<Object, Object> var8);
}

