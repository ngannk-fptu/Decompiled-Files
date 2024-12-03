/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.tool.schema.spi.DelayedDropAction;
import org.hibernate.tool.schema.spi.DelayedDropRegistry;
import org.hibernate.tool.schema.spi.SchemaManagementException;

public class DelayedDropRegistryNotAvailableImpl
implements DelayedDropRegistry {
    public static final DelayedDropRegistryNotAvailableImpl INSTANCE = new DelayedDropRegistryNotAvailableImpl();

    @Override
    public void registerOnCloseAction(DelayedDropAction action) {
        throw new SchemaManagementException("DelayedDropRegistry is not available in this context.  'create-drop' action is not valid");
    }
}

