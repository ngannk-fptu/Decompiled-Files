/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.TransientObjectException;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class ForeignGenerator
implements IdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ForeignGenerator.class);
    private String entityName;
    private String propertyName;

    public String getEntityName() {
        return this.entityName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getRole() {
        return this.getEntityName() + '.' + this.getPropertyName();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.propertyName = params.getProperty("property");
        this.entityName = params.getProperty("entity_name");
        if (this.propertyName == null) {
            throw new MappingException("param named \"property\" is required for foreign id generation strategy");
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sessionImplementor, Object object) {
        Serializable id;
        EntityPersister persister = sessionImplementor.getFactory().getMetamodel().entityPersister(this.entityName);
        Object associatedObject = persister.getPropertyValue(object, this.propertyName);
        if (associatedObject == null) {
            throw new IdentifierGenerationException("attempted to assign id from null one-to-one property [" + this.getRole() + "]");
        }
        Type propertyType = persister.getPropertyType(this.propertyName);
        EntityType foreignValueSourceType = propertyType.isEntityType() ? (EntityType)propertyType : (EntityType)persister.getPropertyType("_identifierMapper." + this.propertyName);
        try {
            id = ForeignKeys.getEntityIdentifierIfNotUnsaved(foreignValueSourceType.getAssociatedEntityName(), associatedObject, sessionImplementor);
        }
        catch (TransientObjectException toe) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("ForeignGenerator detected a transient entity [%s]", foreignValueSourceType.getAssociatedEntityName());
            }
            if (sessionImplementor instanceof Session) {
                id = ((Session)((Object)sessionImplementor)).save(foreignValueSourceType.getAssociatedEntityName(), associatedObject);
            }
            if (sessionImplementor instanceof StatelessSession) {
                id = ((StatelessSession)((Object)sessionImplementor)).insert(foreignValueSourceType.getAssociatedEntityName(), associatedObject);
            }
            throw new IdentifierGenerationException("sessionImplementor is neither Session nor StatelessSession");
        }
        if (sessionImplementor instanceof Session && ((Session)((Object)sessionImplementor)).contains(this.entityName, object)) {
            return IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR;
        }
        return id;
    }
}

