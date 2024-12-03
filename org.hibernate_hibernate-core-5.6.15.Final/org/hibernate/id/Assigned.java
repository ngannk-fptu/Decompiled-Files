/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class Assigned
implements IdentifierGenerator {
    private String entityName;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Serializable id = session.getEntityPersister(this.entityName, obj).getIdentifier(obj, session);
        if (id == null) {
            throw new IdentifierGenerationException("ids for this class must be manually assigned before calling save(): " + this.entityName);
        }
        return id;
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.entityName = params.getProperty("entity_name");
        if (this.entityName == null) {
            throw new MappingException("no entity name");
        }
    }
}

