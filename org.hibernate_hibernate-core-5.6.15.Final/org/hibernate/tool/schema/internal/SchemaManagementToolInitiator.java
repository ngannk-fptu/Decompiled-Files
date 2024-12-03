/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementTool;

public class SchemaManagementToolInitiator
implements StandardServiceInitiator<SchemaManagementTool> {
    public static final SchemaManagementToolInitiator INSTANCE = new SchemaManagementToolInitiator();

    @Override
    public SchemaManagementTool initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object setting = configurationValues.get("hibernate.schema_management_tool");
        SchemaManagementTool tool = registry.getService(StrategySelector.class).resolveStrategy(SchemaManagementTool.class, setting);
        if (tool == null) {
            tool = new HibernateSchemaManagementTool();
        }
        return tool;
    }

    @Override
    public Class<SchemaManagementTool> getServiceInitiated() {
        return SchemaManagementTool.class;
    }
}

