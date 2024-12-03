/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.BasicLoader;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.ColumnEntityAliases;
import org.hibernate.loader.DefaultEntityAliases;
import org.hibernate.loader.GeneratedCollectionAliases;
import org.hibernate.loader.custom.CollectionFetchReturn;
import org.hibernate.loader.custom.CollectionReturn;
import org.hibernate.loader.custom.ColumnCollectionAliases;
import org.hibernate.loader.custom.ConstructorReturn;
import org.hibernate.loader.custom.EntityFetchReturn;
import org.hibernate.loader.custom.FetchReturn;
import org.hibernate.loader.custom.NonScalarReturn;
import org.hibernate.loader.custom.Return;
import org.hibernate.loader.custom.RootReturn;
import org.hibernate.loader.custom.ScalarReturn;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.SQLLoadableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.SQLLoadable;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class SQLQueryReturnProcessor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SQLQueryReturnProcessor.class);
    private static final NativeSQLQueryReturn[] NO_RETURNS = new NativeSQLQueryReturn[0];
    private NativeSQLQueryReturn[] queryReturns;
    private final Map alias2Return = new HashMap();
    private final Map alias2OwnerAlias = new HashMap();
    private final Map<String, EntityPersister> alias2Persister = new HashMap<String, EntityPersister>();
    private final Map alias2Suffix = new HashMap();
    private final Map<String, CollectionPersister> alias2CollectionPersister = new HashMap<String, CollectionPersister>();
    private final Map alias2CollectionSuffix = new HashMap();
    private final Map entityPropertyResultMaps = new HashMap();
    private final Map collectionPropertyResultMaps = new HashMap();
    private final SessionFactoryImplementor factory;
    private int entitySuffixSeed;
    private int collectionSuffixSeed;

    public SQLQueryReturnProcessor(NativeSQLQueryReturn[] queryReturns, SessionFactoryImplementor factory) {
        this.queryReturns = queryReturns == null ? NO_RETURNS : queryReturns;
        this.factory = factory;
    }

    private Map internalGetPropertyResultsMap(String alias) {
        NativeSQLQueryReturn rtn = (NativeSQLQueryReturn)this.alias2Return.get(alias);
        if (rtn instanceof NativeSQLQueryNonScalarReturn) {
            return ((NativeSQLQueryNonScalarReturn)rtn).getPropertyResultsMap();
        }
        return null;
    }

    private boolean hasPropertyResultMap(String alias) {
        Map propertyMaps = this.internalGetPropertyResultsMap(alias);
        return propertyMaps != null && !propertyMaps.isEmpty();
    }

    public ResultAliasContext process() {
        for (NativeSQLQueryReturn queryReturn : this.queryReturns) {
            if (!(queryReturn instanceof NativeSQLQueryNonScalarReturn)) continue;
            NativeSQLQueryNonScalarReturn rtn = (NativeSQLQueryNonScalarReturn)queryReturn;
            this.alias2Return.put(rtn.getAlias(), rtn);
            if (!(rtn instanceof NativeSQLQueryJoinReturn)) continue;
            NativeSQLQueryJoinReturn fetchReturn = (NativeSQLQueryJoinReturn)rtn;
            this.alias2OwnerAlias.put(fetchReturn.getAlias(), fetchReturn.getOwnerAlias());
        }
        for (NativeSQLQueryReturn queryReturn : this.queryReturns) {
            this.processReturn(queryReturn);
        }
        return new ResultAliasContext();
    }

    public void visitReturns(QueryReturnVisitor visitor) {
        for (NativeSQLQueryReturn queryReturn : this.queryReturns) {
            if (NativeSQLQueryScalarReturn.class.isInstance(queryReturn)) {
                visitor.visitScalarReturn((NativeSQLQueryScalarReturn)queryReturn);
                continue;
            }
            if (NativeSQLQueryRootReturn.class.isInstance(queryReturn)) {
                visitor.visitRootReturn((NativeSQLQueryRootReturn)queryReturn);
                continue;
            }
            if (NativeSQLQueryCollectionReturn.class.isInstance(queryReturn)) {
                visitor.visitCollectionReturn((NativeSQLQueryCollectionReturn)queryReturn);
                continue;
            }
            if (NativeSQLQueryJoinReturn.class.isInstance(queryReturn)) {
                visitor.visitFetch((NativeSQLQueryJoinReturn)queryReturn);
                continue;
            }
            if (NativeSQLQueryConstructorReturn.class.isInstance(queryReturn)) {
                visitor.visitDynamicInstantiation((NativeSQLQueryConstructorReturn)queryReturn);
                continue;
            }
            throw new IllegalStateException("Unrecognized NativeSQLQueryReturn concrete type : " + queryReturn);
        }
    }

    public List<Return> generateCustomReturns(boolean queryHadAliases) {
        ArrayList<Return> customReturns = new ArrayList<Return>();
        HashMap<String, NonScalarReturn> customReturnsByAlias = new HashMap<String, NonScalarReturn>();
        for (NativeSQLQueryReturn queryReturn : this.queryReturns) {
            String alias;
            NativeSQLQueryReturn rtn;
            if (queryReturn instanceof NativeSQLQueryScalarReturn) {
                rtn = (NativeSQLQueryScalarReturn)queryReturn;
                customReturns.add(new ScalarReturn(((NativeSQLQueryScalarReturn)rtn).getType(), ((NativeSQLQueryScalarReturn)rtn).getColumnAlias()));
                continue;
            }
            if (queryReturn instanceof NativeSQLQueryRootReturn) {
                rtn = (NativeSQLQueryRootReturn)queryReturn;
                alias = ((NativeSQLQueryNonScalarReturn)rtn).getAlias();
                DefaultEntityAliases entityAliases = queryHadAliases || this.hasPropertyResultMap(alias) ? new DefaultEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias)) : new ColumnEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                RootReturn customReturn = new RootReturn(alias, ((NativeSQLQueryRootReturn)rtn).getReturnEntityName(), entityAliases, ((NativeSQLQueryNonScalarReturn)rtn).getLockMode());
                customReturns.add(customReturn);
                customReturnsByAlias.put(((NativeSQLQueryNonScalarReturn)rtn).getAlias(), customReturn);
                continue;
            }
            if (queryReturn instanceof NativeSQLQueryCollectionReturn) {
                CollectionAliases collectionAliases;
                rtn = (NativeSQLQueryCollectionReturn)queryReturn;
                alias = ((NativeSQLQueryNonScalarReturn)rtn).getAlias();
                SQLLoadableCollection persister = (SQLLoadableCollection)this.alias2CollectionPersister.get(alias);
                boolean isEntityElements = persister.getElementType().isEntityType();
                DefaultEntityAliases elementEntityAliases = null;
                if (queryHadAliases || this.hasPropertyResultMap(alias)) {
                    collectionAliases = new GeneratedCollectionAliases((Map)this.collectionPropertyResultMaps.get(alias), (SQLLoadableCollection)this.alias2CollectionPersister.get(alias), (String)this.alias2CollectionSuffix.get(alias));
                    if (isEntityElements) {
                        elementEntityAliases = new DefaultEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                    }
                } else {
                    collectionAliases = new ColumnCollectionAliases((Map)this.collectionPropertyResultMaps.get(alias), (SQLLoadableCollection)this.alias2CollectionPersister.get(alias));
                    if (isEntityElements) {
                        elementEntityAliases = new ColumnEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                    }
                }
                CollectionReturn customReturn = new CollectionReturn(alias, ((NativeSQLQueryCollectionReturn)rtn).getOwnerEntityName(), ((NativeSQLQueryCollectionReturn)rtn).getOwnerProperty(), collectionAliases, elementEntityAliases, ((NativeSQLQueryNonScalarReturn)rtn).getLockMode());
                customReturns.add(customReturn);
                customReturnsByAlias.put(((NativeSQLQueryNonScalarReturn)rtn).getAlias(), customReturn);
                continue;
            }
            if (queryReturn instanceof NativeSQLQueryJoinReturn) {
                FetchReturn customReturn;
                rtn = (NativeSQLQueryJoinReturn)queryReturn;
                alias = ((NativeSQLQueryNonScalarReturn)rtn).getAlias();
                NonScalarReturn ownerCustomReturn = (NonScalarReturn)customReturnsByAlias.get(((NativeSQLQueryJoinReturn)rtn).getOwnerAlias());
                if (this.alias2CollectionPersister.containsKey(alias)) {
                    CollectionAliases collectionAliases;
                    SQLLoadableCollection persister = (SQLLoadableCollection)this.alias2CollectionPersister.get(alias);
                    boolean isEntityElements = persister.getElementType().isEntityType();
                    DefaultEntityAliases elementEntityAliases = null;
                    if (queryHadAliases || this.hasPropertyResultMap(alias)) {
                        collectionAliases = new GeneratedCollectionAliases((Map)this.collectionPropertyResultMaps.get(alias), persister, (String)this.alias2CollectionSuffix.get(alias));
                        if (isEntityElements) {
                            elementEntityAliases = new DefaultEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                        }
                    } else {
                        collectionAliases = new ColumnCollectionAliases((Map)this.collectionPropertyResultMaps.get(alias), persister);
                        if (isEntityElements) {
                            elementEntityAliases = new ColumnEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                        }
                    }
                    customReturn = new CollectionFetchReturn(alias, ownerCustomReturn, ((NativeSQLQueryJoinReturn)rtn).getOwnerProperty(), collectionAliases, elementEntityAliases, ((NativeSQLQueryNonScalarReturn)rtn).getLockMode());
                } else {
                    DefaultEntityAliases entityAliases = queryHadAliases || this.hasPropertyResultMap(alias) ? new DefaultEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias)) : new ColumnEntityAliases((Map)this.entityPropertyResultMaps.get(alias), (SQLLoadable)this.alias2Persister.get(alias), (String)this.alias2Suffix.get(alias));
                    customReturn = new EntityFetchReturn(alias, entityAliases, ownerCustomReturn, ((NativeSQLQueryJoinReturn)rtn).getOwnerProperty(), ((NativeSQLQueryNonScalarReturn)rtn).getLockMode());
                }
                customReturns.add(customReturn);
                customReturnsByAlias.put(alias, customReturn);
                continue;
            }
            if (NativeSQLQueryConstructorReturn.class.isInstance(queryReturn)) {
                NativeSQLQueryConstructorReturn constructorReturn = (NativeSQLQueryConstructorReturn)queryReturn;
                ScalarReturn[] scalars = new ScalarReturn[constructorReturn.getColumnReturns().length];
                int i = 0;
                for (NativeSQLQueryScalarReturn scalarReturn : constructorReturn.getColumnReturns()) {
                    scalars[i++] = new ScalarReturn(scalarReturn.getType(), scalarReturn.getColumnAlias());
                }
                customReturns.add(new ConstructorReturn(constructorReturn.getTargetClass(), scalars));
                continue;
            }
            throw new IllegalStateException("Unrecognized NativeSQLQueryReturn concrete type : " + queryReturn);
        }
        return customReturns;
    }

    public List<Return> generateCallableReturns() {
        final ArrayList<Return> customReturns = new ArrayList<Return>();
        this.visitReturns(new QueryReturnVisitor(){

            @Override
            public void visitScalarReturn(NativeSQLQueryScalarReturn rtn) {
                customReturns.add(new ScalarReturn(rtn.getType(), rtn.getColumnAlias()));
            }

            @Override
            public void visitRootReturn(NativeSQLQueryRootReturn rtn) {
                customReturns.add(new RootReturn(rtn.getAlias(), rtn.getReturnEntityName(), new ColumnEntityAliases((Map)SQLQueryReturnProcessor.this.entityPropertyResultMaps.get(rtn.getAlias()), (SQLLoadable)SQLQueryReturnProcessor.this.alias2Persister.get(rtn.getAlias()), (String)SQLQueryReturnProcessor.this.alias2Suffix.get(rtn.getAlias())), rtn.getLockMode()));
            }

            @Override
            public void visitCollectionReturn(NativeSQLQueryCollectionReturn rtn) {
                throw new UnsupportedOperationException("Collection returns not supported for stored procedure mapping");
            }

            @Override
            public void visitFetch(NativeSQLQueryJoinReturn rtn) {
                throw new UnsupportedOperationException("Collection returns not supported for stored procedure mapping");
            }

            @Override
            public void visitDynamicInstantiation(NativeSQLQueryConstructorReturn rtn) {
                ScalarReturn[] scalars = new ScalarReturn[rtn.getColumnReturns().length];
                int i = 0;
                for (NativeSQLQueryScalarReturn scalarReturn : rtn.getColumnReturns()) {
                    scalars[i++] = new ScalarReturn(scalarReturn.getType(), scalarReturn.getColumnAlias());
                }
                customReturns.add(new ConstructorReturn(rtn.getTargetClass(), scalars));
            }
        });
        return customReturns;
    }

    private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
        EntityPersister persister = this.factory.getEntityPersister(entityName);
        if (!(persister instanceof SQLLoadable)) {
            throw new MappingException("class persister is not SQLLoadable: " + entityName);
        }
        return (SQLLoadable)persister;
    }

    private String generateEntitySuffix() {
        return BasicLoader.generateSuffixes(this.entitySuffixSeed++, 1)[0];
    }

    private String generateCollectionSuffix() {
        return this.collectionSuffixSeed++ + "__";
    }

    private void processReturn(NativeSQLQueryReturn rtn) {
        if (rtn instanceof NativeSQLQueryScalarReturn) {
            this.processScalarReturn((NativeSQLQueryScalarReturn)rtn);
        } else if (rtn instanceof NativeSQLQueryRootReturn) {
            this.processRootReturn((NativeSQLQueryRootReturn)rtn);
        } else if (rtn instanceof NativeSQLQueryCollectionReturn) {
            this.processCollectionReturn((NativeSQLQueryCollectionReturn)rtn);
        } else if (NativeSQLQueryJoinReturn.class.isInstance(rtn)) {
            this.processJoinReturn((NativeSQLQueryJoinReturn)rtn);
        } else if (NativeSQLQueryConstructorReturn.class.isInstance(rtn)) {
            this.processConstructorReturn((NativeSQLQueryConstructorReturn)rtn);
        } else {
            throw new IllegalStateException("Unrecognized NativeSQLQueryReturn concrete type encountered : " + rtn);
        }
    }

    private void processConstructorReturn(NativeSQLQueryConstructorReturn rtn) {
    }

    private void processScalarReturn(NativeSQLQueryScalarReturn typeReturn) {
    }

    private void processRootReturn(NativeSQLQueryRootReturn rootReturn) {
        if (this.alias2Persister.containsKey(rootReturn.getAlias())) {
            return;
        }
        SQLLoadable persister = this.getSQLLoadable(rootReturn.getReturnEntityName());
        this.addPersister(rootReturn.getAlias(), rootReturn.getPropertyResultsMap(), persister);
    }

    private void addPersister(String alias, Map propertyResult, SQLLoadable persister) {
        this.alias2Persister.put(alias, persister);
        String suffix = this.generateEntitySuffix();
        LOG.tracev("Mapping alias [{0}] to entity-suffix [{1}]", alias, suffix);
        this.alias2Suffix.put(alias, suffix);
        this.entityPropertyResultMaps.put(alias, propertyResult);
    }

    private void addCollection(String role, String alias, Map propertyResults) {
        SQLLoadableCollection collectionPersister = (SQLLoadableCollection)this.factory.getCollectionPersister(role);
        this.alias2CollectionPersister.put(alias, collectionPersister);
        String suffix = this.generateCollectionSuffix();
        LOG.tracev("Mapping alias [{0}] to collection-suffix [{1}]", alias, suffix);
        this.alias2CollectionSuffix.put(alias, suffix);
        this.collectionPropertyResultMaps.put(alias, propertyResults);
        if (collectionPersister.isOneToMany() || collectionPersister.isManyToMany()) {
            SQLLoadable persister = (SQLLoadable)collectionPersister.getElementPersister();
            this.addPersister(alias, this.filter(propertyResults), persister);
        }
    }

    private Map filter(Map propertyResults) {
        HashMap result = new HashMap(propertyResults.size());
        String keyPrefix = "element.";
        for (Map.Entry element : propertyResults.entrySet()) {
            String path = (String)element.getKey();
            if (!path.startsWith(keyPrefix)) continue;
            result.put(path.substring(keyPrefix.length()), element.getValue());
        }
        return result;
    }

    private void processCollectionReturn(NativeSQLQueryCollectionReturn collectionReturn) {
        String role = collectionReturn.getOwnerEntityName() + '.' + collectionReturn.getOwnerProperty();
        this.addCollection(role, collectionReturn.getAlias(), collectionReturn.getPropertyResultsMap());
    }

    private void processJoinReturn(NativeSQLQueryJoinReturn fetchReturn) {
        SQLLoadable ownerPersister;
        Type returnType;
        String alias = fetchReturn.getAlias();
        if (this.alias2Persister.containsKey(alias) || this.alias2CollectionPersister.containsKey(alias)) {
            return;
        }
        String ownerAlias = fetchReturn.getOwnerAlias();
        if (!this.alias2Return.containsKey(ownerAlias)) {
            throw new HibernateException("Owner alias [" + ownerAlias + "] is unknown for alias [" + alias + "]");
        }
        if (!this.alias2Persister.containsKey(ownerAlias)) {
            NativeSQLQueryNonScalarReturn ownerReturn = (NativeSQLQueryNonScalarReturn)this.alias2Return.get(ownerAlias);
            this.processReturn(ownerReturn);
        }
        if ((returnType = (ownerPersister = (SQLLoadable)this.alias2Persister.get(ownerAlias)).getPropertyType(fetchReturn.getOwnerProperty())).isCollectionType()) {
            String role = ownerPersister.getEntityName() + '.' + fetchReturn.getOwnerProperty();
            this.addCollection(role, alias, fetchReturn.getPropertyResultsMap());
        } else if (returnType.isEntityType()) {
            EntityType eType = (EntityType)returnType;
            String returnEntityName = eType.getAssociatedEntityName();
            SQLLoadable persister = this.getSQLLoadable(returnEntityName);
            this.addPersister(alias, fetchReturn.getPropertyResultsMap(), persister);
        }
    }

    private static interface QueryReturnVisitor {
        public void visitScalarReturn(NativeSQLQueryScalarReturn var1);

        public void visitRootReturn(NativeSQLQueryRootReturn var1);

        public void visitCollectionReturn(NativeSQLQueryCollectionReturn var1);

        public void visitFetch(NativeSQLQueryJoinReturn var1);

        public void visitDynamicInstantiation(NativeSQLQueryConstructorReturn var1);
    }

    public class ResultAliasContext {
        public SQLLoadable getEntityPersister(String alias) {
            return (SQLLoadable)SQLQueryReturnProcessor.this.alias2Persister.get(alias);
        }

        public SQLLoadableCollection getCollectionPersister(String alias) {
            return (SQLLoadableCollection)SQLQueryReturnProcessor.this.alias2CollectionPersister.get(alias);
        }

        public String getEntitySuffix(String alias) {
            return (String)SQLQueryReturnProcessor.this.alias2Suffix.get(alias);
        }

        public String getCollectionSuffix(String alias) {
            return (String)SQLQueryReturnProcessor.this.alias2CollectionSuffix.get(alias);
        }

        public String getOwnerAlias(String alias) {
            return (String)SQLQueryReturnProcessor.this.alias2OwnerAlias.get(alias);
        }

        public Map getPropertyResultsMap(String alias) {
            return SQLQueryReturnProcessor.this.internalGetPropertyResultsMap(alias);
        }

        public String[] collectQuerySpaces() {
            HashSet<String> spaces = new HashSet<String>();
            this.collectQuerySpaces(spaces);
            return spaces.toArray(new String[spaces.size()]);
        }

        public void collectQuerySpaces(Collection<String> spaces) {
            for (Object persister : SQLQueryReturnProcessor.this.alias2Persister.values()) {
                Collections.addAll(spaces, (String[])persister.getQuerySpaces());
            }
            for (Object persister : SQLQueryReturnProcessor.this.alias2CollectionPersister.values()) {
                Type elementType = persister.getElementType();
                if (!elementType.isEntityType() || elementType.isAnyType()) continue;
                Joinable joinable = ((EntityType)elementType).getAssociatedJoinable(SQLQueryReturnProcessor.this.factory);
                Collections.addAll(spaces, (String[])((EntityPersister)((Object)joinable)).getQuerySpaces());
            }
        }
    }
}

