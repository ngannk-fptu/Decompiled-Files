/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.ExportableProducer;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public interface IdentifierGenerator
extends Configurable,
ExportableProducer {
    public static final String ENTITY_NAME = "entity_name";
    public static final String JPA_ENTITY_NAME = "jpa_entity_name";
    public static final String GENERATOR_NAME = "GENERATOR_NAME";

    @Override
    default public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
    }

    @Override
    default public void registerExportables(Database database) {
    }

    default public void initialize(SqlStringGenerationContext context) {
    }

    public Serializable generate(SharedSessionContractImplementor var1, Object var2) throws HibernateException;

    default public boolean supportsJdbcBatchInserts() {
        return true;
    }
}

