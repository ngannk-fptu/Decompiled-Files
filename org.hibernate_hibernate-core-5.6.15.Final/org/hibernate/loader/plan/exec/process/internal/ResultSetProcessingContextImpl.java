/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.exec.process.internal.HydratedEntityRegistration;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorHelper;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.EntityType;
import org.jboss.logging.Logger;

public class ResultSetProcessingContextImpl
implements ResultSetProcessingContext {
    private static final Logger LOG = CoreLogging.logger(ResultSetProcessingContextImpl.class);
    private final ResultSet resultSet;
    private final SharedSessionContractImplementor session;
    private final LoadPlan loadPlan;
    private final AliasResolutionContext aliasResolutionContext;
    private final boolean readOnly;
    private final boolean shouldUseOptionalEntityInformation;
    private final boolean shouldReturnProxies;
    private final QueryParameters queryParameters;
    private final NamedParameterContext namedParameterContext;
    private final boolean hadSubselectFetches;
    private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
    private Map<EntityReference, Set<EntityKey>> subselectLoadableEntityKeyMap;
    private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
    private int nRowsRead = 0;
    private Map<EntityReference, ResultSetProcessingContext.EntityReferenceProcessingState> identifierResolutionContextMap;

    public ResultSetProcessingContextImpl(ResultSet resultSet, SharedSessionContractImplementor session, LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext, boolean readOnly, boolean shouldUseOptionalEntityInformation, boolean shouldReturnProxies, QueryParameters queryParameters, NamedParameterContext namedParameterContext, boolean hadSubselectFetches) {
        this.resultSet = resultSet;
        this.session = session;
        this.loadPlan = loadPlan;
        this.aliasResolutionContext = aliasResolutionContext;
        this.readOnly = readOnly;
        this.shouldUseOptionalEntityInformation = shouldUseOptionalEntityInformation;
        this.shouldReturnProxies = shouldReturnProxies;
        this.queryParameters = queryParameters;
        this.namedParameterContext = namedParameterContext;
        this.hadSubselectFetches = hadSubselectFetches;
        if (shouldUseOptionalEntityInformation && queryParameters.getOptionalId() != null && loadPlan.getReturns().size() > 1) {
            throw new IllegalStateException("Cannot specify 'optional entity' values with multi-return load plans");
        }
    }

    @Override
    public SharedSessionContractImplementor getSession() {
        return this.session;
    }

    @Override
    public boolean shouldUseOptionalEntityInformation() {
        return this.shouldUseOptionalEntityInformation;
    }

    @Override
    public QueryParameters getQueryParameters() {
        return this.queryParameters;
    }

    @Override
    public boolean shouldReturnProxies() {
        return this.shouldReturnProxies;
    }

    @Override
    public LoadPlan getLoadPlan() {
        return this.loadPlan;
    }

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    @Override
    public LockMode resolveLockMode(EntityReference entityReference) {
        if (this.queryParameters.getLockOptions() != null && this.queryParameters.getLockOptions().getLockMode() != null) {
            return this.queryParameters.getLockOptions().getLockMode();
        }
        return LockMode.NONE;
    }

    @Override
    public ResultSetProcessingContext.EntityReferenceProcessingState getProcessingState(final EntityReference entityReference) {
        ResultSetProcessingContext.EntityReferenceProcessingState context;
        if (this.identifierResolutionContextMap == null) {
            this.identifierResolutionContextMap = new IdentityHashMap<EntityReference, ResultSetProcessingContext.EntityReferenceProcessingState>(5);
            context = null;
        } else {
            context = this.identifierResolutionContextMap.get(entityReference);
        }
        if (context == null) {
            context = new ResultSetProcessingContext.EntityReferenceProcessingState(){
                private boolean wasMissingIdentifier;
                private Object identifierHydratedForm;
                private EntityKey entityKey;
                private Object[] hydratedState;
                private Object entityInstance;

                @Override
                public EntityReference getEntityReference() {
                    return entityReference;
                }

                @Override
                public void registerMissingIdentifier() {
                    if (!EntityFetch.class.isInstance(entityReference)) {
                        throw new IllegalStateException("Missing return row identifier");
                    }
                    ResultSetProcessingContextImpl.this.registerNonExists((EntityFetch)entityReference);
                    this.wasMissingIdentifier = true;
                }

                @Override
                public boolean isMissingIdentifier() {
                    return this.wasMissingIdentifier;
                }

                @Override
                public void registerIdentifierHydratedForm(Object identifierHydratedForm) {
                    this.identifierHydratedForm = identifierHydratedForm;
                }

                @Override
                public Object getIdentifierHydratedForm() {
                    return this.identifierHydratedForm;
                }

                @Override
                public void registerEntityKey(EntityKey entityKey) {
                    this.entityKey = entityKey;
                }

                @Override
                public EntityKey getEntityKey() {
                    return this.entityKey;
                }

                @Override
                public void registerHydratedState(Object[] hydratedState) {
                    this.hydratedState = hydratedState;
                }

                @Override
                public Object[] getHydratedState() {
                    return this.hydratedState;
                }

                @Override
                public void registerEntityInstance(Object entityInstance) {
                    this.entityInstance = entityInstance;
                }

                @Override
                public Object getEntityInstance() {
                    return this.entityInstance;
                }
            };
            this.identifierResolutionContextMap.put(entityReference, context);
        }
        return context;
    }

    private void registerNonExists(EntityFetch fetch) {
        EntityType fetchedType = fetch.getFetchedType();
        if (!fetchedType.isOneToOne()) {
            return;
        }
        ResultSetProcessingContext.EntityReferenceProcessingState fetchOwnerState = this.getOwnerProcessingState(fetch);
        if (fetchOwnerState == null) {
            throw new IllegalStateException("Could not locate fetch owner state");
        }
        EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
        if (ownerEntityKey == null) {
            throw new IllegalStateException("Could not locate fetch owner EntityKey");
        }
        this.session.getPersistenceContextInternal().addNullProperty(ownerEntityKey, fetchedType.getPropertyName());
    }

    @Override
    public ResultSetProcessingContext.EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch) {
        return this.getProcessingState(fetch.getSource().resolveEntityReference());
    }

    @Override
    public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance) {
        if (this.currentRowHydratedEntityRegistrationList == null) {
            this.currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
        }
        this.currentRowHydratedEntityRegistrationList.add(new HydratedEntityRegistration(entityReference, entityKey, entityInstance));
    }

    void finishUpRow() {
        ++this.nRowsRead;
        if (this.currentRowHydratedEntityRegistrationList == null) {
            if (this.identifierResolutionContextMap != null) {
                this.identifierResolutionContextMap.clear();
            }
            return;
        }
        int sizeHint = this.currentRowHydratedEntityRegistrationList.size();
        if (this.hydratedEntityRegistrationList == null) {
            this.hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>(sizeHint);
        }
        this.hydratedEntityRegistrationList.addAll(this.currentRowHydratedEntityRegistrationList);
        if (this.hadSubselectFetches) {
            if (this.subselectLoadableEntityKeyMap == null) {
                this.subselectLoadableEntityKeyMap = new HashMap<EntityReference, Set<EntityKey>>();
            }
            for (HydratedEntityRegistration registration : this.currentRowHydratedEntityRegistrationList) {
                Set<EntityKey> entityKeys = this.subselectLoadableEntityKeyMap.get(registration.getEntityReference());
                if (entityKeys == null) {
                    entityKeys = new HashSet<EntityKey>();
                    this.subselectLoadableEntityKeyMap.put(registration.getEntityReference(), entityKeys);
                }
                entityKeys.add(registration.getKey());
            }
        }
        this.currentRowHydratedEntityRegistrationList.clear();
        this.identifierResolutionContextMap.clear();
    }

    public List<HydratedEntityRegistration> getHydratedEntityRegistrationList() {
        return this.hydratedEntityRegistrationList;
    }

    public void wrapUp() {
        this.createSubselects();
        if (this.hydratedEntityRegistrationList != null) {
            this.hydratedEntityRegistrationList = null;
        }
        if (this.subselectLoadableEntityKeyMap != null) {
            this.subselectLoadableEntityKeyMap = null;
        }
    }

    private void createSubselects() {
        if (this.subselectLoadableEntityKeyMap == null || this.nRowsRead <= 1) {
            LOG.tracef("Skipping create subselects because there are fewer than 2 results, so query by key is more efficient.", (Object)this.getClass().getName());
            return;
        }
        Map<String, int[]> namedParameterLocMap = ResultSetProcessorHelper.buildNamedParameterLocMap(this.queryParameters, this.namedParameterContext);
        String subselectQueryString = SubselectFetch.createSubselectFetchQueryFragment(this.queryParameters);
        for (Map.Entry<EntityReference, Set<EntityKey>> entry : this.subselectLoadableEntityKeyMap.entrySet()) {
            if (!entry.getKey().getEntityPersister().hasSubselectLoadableCollections()) continue;
            SubselectFetch subselectFetch = new SubselectFetch(subselectQueryString, this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(entry.getKey().getQuerySpaceUid()), (Loadable)entry.getKey().getEntityPersister(), this.queryParameters, entry.getValue(), namedParameterLocMap);
            PersistenceContext persistenceContext = this.session.getPersistenceContextInternal();
            for (EntityKey key : entry.getValue()) {
                persistenceContext.getBatchFetchQueue().addSubselect(key, subselectFetch);
            }
        }
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }
}

