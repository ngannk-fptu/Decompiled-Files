/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.StaleObjectStateException;
import org.hibernate.WrongClassException;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.engine.internal.TwoPhaseLoad;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorHelper;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.jboss.logging.Logger;

public class EntityReferenceInitializerImpl
implements EntityReferenceInitializer {
    private static final Logger log = CoreLogging.logger(EntityReferenceInitializerImpl.class);
    private final EntityReference entityReference;
    private final EntityReferenceAliases entityReferenceAliases;
    private final boolean isReturn;

    public EntityReferenceInitializerImpl(EntityReference entityReference, EntityReferenceAliases entityReferenceAliases) {
        this(entityReference, entityReferenceAliases, false);
    }

    public EntityReferenceInitializerImpl(EntityReference entityReference, EntityReferenceAliases entityReferenceAliases, boolean isRoot) {
        this.entityReference = entityReference;
        this.entityReferenceAliases = entityReferenceAliases;
        this.isReturn = isRoot;
    }

    @Override
    public EntityReference getEntityReference() {
        return this.entityReference;
    }

    @Override
    public void hydrateIdentifier(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
        ResultSetProcessingContext.EntityReferenceProcessingState processingState = context.getProcessingState(this.entityReference);
        Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
        if (identifierHydratedForm == null) {
            identifierHydratedForm = this.readIdentifierHydratedState(resultSet, context);
            processingState.registerIdentifierHydratedForm(identifierHydratedForm);
        }
    }

    private Object readIdentifierHydratedState(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
        try {
            return this.entityReference.getEntityPersister().getIdentifierType().hydrate(resultSet, this.entityReferenceAliases.getColumnAliases().getSuffixedKeyAliases(), context.getSession(), null);
        }
        catch (Exception e) {
            throw new HibernateException("Encountered problem trying to hydrate identifier for entity [" + this.entityReference.getEntityPersister() + "]", e);
        }
    }

    @Override
    public void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContextImpl context) {
        ResultSetProcessingContext.EntityReferenceProcessingState processingState = context.getProcessingState(this.entityReference);
        EntityKey entityKey = processingState.getEntityKey();
        if (entityKey != null) {
            log.debugf("On call to EntityIdentifierReaderImpl#resolve, EntityKey was already known; should only happen on root returns with an optional identifier specified", new Object[0]);
            return;
        }
        Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
        if (identifierHydratedForm == null) {
            return;
        }
        Type identifierType = this.entityReference.getEntityPersister().getIdentifierType();
        Serializable resolvedId = (Serializable)identifierType.resolve(identifierHydratedForm, context.getSession(), null);
        if (resolvedId != null) {
            processingState.registerEntityKey(context.getSession().generateEntityKey(resolvedId, this.entityReference.getEntityPersister()));
        }
    }

    @Override
    public void hydrateEntityState(ResultSet resultSet, ResultSetProcessingContextImpl context) {
        EntityKey optionalEntityKey;
        ResultSetProcessingContext.EntityReferenceProcessingState processingState = context.getProcessingState(this.entityReference);
        if (processingState.isMissingIdentifier()) {
            this.handleMissingIdentifier(context);
            return;
        }
        EntityKey entityKey = processingState.getEntityKey();
        if (entityKey == null) {
            this.handleMissingIdentifier(context);
            return;
        }
        if (processingState.getEntityInstance() != null) {
            return;
        }
        Object existing = context.getSession().getEntityUsingInterceptor(entityKey);
        if (existing != null) {
            BytecodeLazyAttributeInterceptor interceptor;
            if (!this.entityReference.getEntityPersister().isInstance(existing)) {
                throw new WrongClassException("loaded object was of wrong class " + existing.getClass(), entityKey.getIdentifier(), this.entityReference.getEntityPersister().getEntityName());
            }
            this.checkVersion(resultSet, context, entityKey, existing);
            processingState.registerEntityInstance(existing);
            BytecodeEnhancementMetadata enhancementMetadata = this.entityReference.getEntityPersister().getEntityMetamodel().getBytecodeEnhancementMetadata();
            if (enhancementMetadata.isEnhancedForLazyLoading() && (interceptor = enhancementMetadata.extractLazyInterceptor(existing)) instanceof EnhancementAsProxyLazinessInterceptor) {
                LockMode requestedLockMode = context.resolveLockMode(this.entityReference);
                LockMode lockModeToAcquire = requestedLockMode == LockMode.NONE ? LockMode.READ : requestedLockMode;
                EnhancementAsProxyLazinessInterceptor enhancementAsProxyLazinessInterceptor = (EnhancementAsProxyLazinessInterceptor)interceptor;
                if (enhancementAsProxyLazinessInterceptor.isInitializing()) {
                    return;
                }
                enhancementAsProxyLazinessInterceptor.setInitializing();
                this.loadFromResultSet(resultSet, context, existing, this.getConcreteEntityTypeName(resultSet, context, entityKey), entityKey, lockModeToAcquire);
            }
            return;
        }
        Object entityInstance = null;
        if (this.isReturn && context.getQueryParameters().getOptionalObject() != null && (optionalEntityKey = ResultSetProcessorHelper.getOptionalObjectKey(context.getQueryParameters(), context.getSession())) != null && optionalEntityKey.equals(entityKey)) {
            entityInstance = context.getQueryParameters().getOptionalObject();
        }
        String concreteEntityTypeName = this.getConcreteEntityTypeName(resultSet, context, entityKey);
        if (entityInstance == null) {
            entityInstance = context.getSession().instantiate(concreteEntityTypeName, entityKey.getIdentifier());
        }
        processingState.registerEntityInstance(entityInstance);
        log.trace((Object)"hydrating entity state");
        LockMode requestedLockMode = context.resolveLockMode(this.entityReference);
        LockMode lockModeToAcquire = requestedLockMode == LockMode.NONE ? LockMode.READ : requestedLockMode;
        this.loadFromResultSet(resultSet, context, entityInstance, concreteEntityTypeName, entityKey, lockModeToAcquire);
    }

    private void handleMissingIdentifier(ResultSetProcessingContext context) {
        if (EntityFetch.class.isInstance(this.entityReference)) {
            EntityFetch fetch = (EntityFetch)this.entityReference;
            EntityType fetchedType = fetch.getFetchedType();
            if (!fetchedType.isOneToOne()) {
                return;
            }
            ResultSetProcessingContext.EntityReferenceProcessingState fetchOwnerState = context.getOwnerProcessingState(fetch);
            if (fetchOwnerState == null) {
                throw new IllegalStateException("Could not locate fetch owner state");
            }
            EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
            if (ownerEntityKey != null) {
                context.getSession().getPersistenceContextInternal().addNullProperty(ownerEntityKey, fetchedType.getPropertyName());
            }
        }
    }

    private void loadFromResultSet(ResultSet resultSet, ResultSetProcessingContext context, Object entityInstance, String concreteEntityTypeName, EntityKey entityKey, LockMode lockModeToAcquire) {
        String ukName;
        EntityType entityType;
        Object rowId;
        Object[] values;
        Serializable id = entityKey.getIdentifier();
        SharedSessionContractImplementor session = context.getSession();
        Loadable concreteEntityPersister = (Loadable)session.getFactory().getMetamodel().entityPersister(concreteEntityTypeName);
        if (log.isTraceEnabled()) {
            log.tracev("Initializing object from ResultSet: {0}", (Object)MessageHelper.infoString((EntityPersister)concreteEntityPersister, id, session.getFactory()));
        }
        TwoPhaseLoad.addUninitializedEntity(entityKey, entityInstance, concreteEntityPersister, lockModeToAcquire, session);
        EntityPersister rootEntityPersister = session.getFactory().getMetamodel().entityPersister(concreteEntityPersister.getRootEntityName());
        try {
            values = concreteEntityPersister.hydrate(resultSet, id, entityInstance, (Loadable)this.entityReference.getEntityPersister(), concreteEntityPersister == rootEntityPersister ? this.entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases() : this.entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases(concreteEntityPersister), context.getLoadPlan().areLazyAttributesForceFetched(), session);
            context.getProcessingState(this.entityReference).registerHydratedState(values);
        }
        catch (SQLException e) {
            throw session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().convert(e, "Could not read entity state from ResultSet : " + entityKey);
        }
        try {
            Object object = rowId = concreteEntityPersister.hasRowId() ? resultSet.getObject(this.entityReferenceAliases.getColumnAliases().getRowIdAlias()) : null;
            if (rowId != null && log.isTraceEnabled()) {
                log.tracev("extracted ROWID value: {0}", rowId);
            }
        }
        catch (SQLException e) {
            throw session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().convert(e, "Could not read entity row-id from ResultSet : " + entityKey);
        }
        EntityType entityType2 = entityType = EntityFetch.class.isInstance(this.entityReference) ? ((EntityFetch)this.entityReference).getFetchedType() : this.entityReference.getEntityPersister().getEntityMetamodel().getEntityType();
        if (entityType != null && (ukName = entityType.getRHSUniqueKeyPropertyName()) != null) {
            int index = ((UniqueKeyLoadable)concreteEntityPersister).getPropertyIndex(ukName);
            Type type = concreteEntityPersister.getPropertyTypes()[index];
            EntityUniqueKey euk = new EntityUniqueKey(this.entityReference.getEntityPersister().getEntityName(), ukName, type.semiResolve(values[index], session, entityInstance), type, concreteEntityPersister.getEntityMode(), session.getFactory());
            session.getPersistenceContextInternal().addEntity(euk, entityInstance);
        }
        TwoPhaseLoad.postHydrate(concreteEntityPersister, id, values, rowId, entityInstance, lockModeToAcquire, session);
        context.registerHydratedEntity(this.entityReference, entityKey, entityInstance);
    }

    private String getConcreteEntityTypeName(ResultSet resultSet, ResultSetProcessingContext context, EntityKey entityKey) {
        Object discriminatorValue;
        Loadable loadable = (Loadable)this.entityReference.getEntityPersister();
        if (!loadable.hasSubclasses()) {
            return this.entityReference.getEntityPersister().getEntityName();
        }
        try {
            discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(resultSet, this.entityReferenceAliases.getColumnAliases().getSuffixedDiscriminatorAlias(), context.getSession(), null);
        }
        catch (SQLException e) {
            throw context.getSession().getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().convert(e, "Could not read discriminator value from ResultSet");
        }
        String result = loadable.getSubclassForDiscriminatorValue(discriminatorValue);
        if (result == null) {
            throw new WrongClassException("Discriminator: " + discriminatorValue, entityKey.getIdentifier(), this.entityReference.getEntityPersister().getEntityName());
        }
        return result;
    }

    private void checkVersion(ResultSet resultSet, ResultSetProcessingContext context, EntityKey entityKey, Object existing) {
        LockMode requestedLockMode = context.resolveLockMode(this.entityReference);
        if (requestedLockMode != LockMode.NONE) {
            boolean isVersionCheckNeeded;
            SharedSessionContractImplementor session = context.getSession();
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            LockMode currentLockMode = persistenceContext.getEntry(existing).getLockMode();
            boolean bl = isVersionCheckNeeded = this.entityReference.getEntityPersister().isVersioned() && currentLockMode.lessThan(requestedLockMode);
            if (isVersionCheckNeeded) {
                this.checkVersion(session, resultSet, this.entityReference.getEntityPersister(), this.entityReferenceAliases.getColumnAliases(), entityKey, existing);
                persistenceContext.getEntry(existing).setLockMode(requestedLockMode);
            }
        }
    }

    private void checkVersion(SharedSessionContractImplementor session, ResultSet resultSet, EntityPersister persister, EntityAliases entityAliases, EntityKey entityKey, Object entityInstance) {
        Object version = session.getPersistenceContextInternal().getEntry(entityInstance).getVersion();
        if (version != null) {
            Object currentVersion;
            VersionType versionType = persister.getVersionType();
            try {
                currentVersion = versionType.nullSafeGet(resultSet, entityAliases.getSuffixedVersionAliases(), session, null);
            }
            catch (SQLException e) {
                throw session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().convert(e, "Could not read version value from result set");
            }
            if (!versionType.isEqual(version, currentVersion)) {
                StatisticsImplementor statistics = session.getFactory().getStatistics();
                if (statistics.isStatisticsEnabled()) {
                    statistics.optimisticFailure(persister.getEntityName());
                }
                throw new StaleObjectStateException(persister.getEntityName(), entityKey.getIdentifier());
            }
        }
    }

    @Override
    public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
    }
}

