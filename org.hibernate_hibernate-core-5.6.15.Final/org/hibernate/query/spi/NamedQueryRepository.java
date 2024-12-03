/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.query.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Incubating;
import org.hibernate.MappingException;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.procedure.ProcedureCallMemento;
import org.jboss.logging.Logger;

@Incubating
public class NamedQueryRepository {
    private static final Logger log = Logger.getLogger(NamedQueryRepository.class);
    private final Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap;
    private volatile Map<String, NamedQueryDefinition> namedQueryDefinitionMap;
    private volatile Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap;
    private volatile Map<String, ProcedureCallMemento> procedureCallMementoMap;

    public NamedQueryRepository(Iterable<NamedQueryDefinition> namedQueryDefinitions, Iterable<NamedSQLQueryDefinition> namedSqlQueryDefinitions, Iterable<ResultSetMappingDefinition> namedSqlResultSetMappings, Map<String, ProcedureCallMemento> namedProcedureCalls) {
        HashMap<String, NamedQueryDefinition> namedQueryDefinitionMap = new HashMap<String, NamedQueryDefinition>();
        for (NamedQueryDefinition namedQueryDefinition : namedQueryDefinitions) {
            namedQueryDefinitionMap.put(namedQueryDefinition.getName(), namedQueryDefinition);
        }
        this.namedQueryDefinitionMap = CollectionHelper.toSmallMap(namedQueryDefinitionMap);
        HashMap<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap = new HashMap<String, NamedSQLQueryDefinition>();
        for (NamedSQLQueryDefinition namedSqlQueryDefinition : namedSqlQueryDefinitions) {
            namedSqlQueryDefinitionMap.put(namedSqlQueryDefinition.getName(), namedSqlQueryDefinition);
        }
        this.namedSqlQueryDefinitionMap = CollectionHelper.toSmallMap(namedSqlQueryDefinitionMap);
        HashMap<String, ResultSetMappingDefinition> hashMap = new HashMap<String, ResultSetMappingDefinition>();
        for (ResultSetMappingDefinition resultSetMappingDefinition : namedSqlResultSetMappings) {
            hashMap.put(resultSetMappingDefinition.getName(), resultSetMappingDefinition);
        }
        this.namedSqlResultSetMappingMap = CollectionHelper.toSmallMap(hashMap);
        this.procedureCallMementoMap = CollectionHelper.toSmallMap(namedProcedureCalls);
    }

    public NamedQueryRepository(Map<String, NamedQueryDefinition> namedQueryDefinitionMap, Map<String, NamedSQLQueryDefinition> namedSqlQueryDefinitionMap, Map<String, ResultSetMappingDefinition> namedSqlResultSetMappingMap, Map<String, ProcedureCallMemento> namedProcedureCallMap) {
        this.namedQueryDefinitionMap = CollectionHelper.toSmallMap(namedQueryDefinitionMap);
        this.namedSqlQueryDefinitionMap = CollectionHelper.toSmallMap(namedSqlQueryDefinitionMap);
        this.namedSqlResultSetMappingMap = CollectionHelper.toSmallMap(namedSqlResultSetMappingMap);
        this.procedureCallMementoMap = CollectionHelper.toSmallMap(namedProcedureCallMap);
    }

    public NamedQueryDefinition getNamedQueryDefinition(String queryName) {
        return this.namedQueryDefinitionMap.get(queryName);
    }

    public NamedSQLQueryDefinition getNamedSQLQueryDefinition(String queryName) {
        return this.namedSqlQueryDefinitionMap.get(queryName);
    }

    public ProcedureCallMemento getNamedProcedureCallMemento(String name) {
        return this.procedureCallMementoMap.get(name);
    }

    public ResultSetMappingDefinition getResultSetMappingDefinition(String mappingName) {
        return this.namedSqlResultSetMappingMap.get(mappingName);
    }

    private synchronized void removeNamedQueryDefinition(String name) {
        if (this.namedQueryDefinitionMap.containsKey(name)) {
            Map<String, NamedQueryDefinition> copy = CollectionHelper.makeCopy(this.namedQueryDefinitionMap);
            copy.remove(name);
            this.namedQueryDefinitionMap = CollectionHelper.toSmallMap(copy);
        }
    }

    public synchronized void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
        Map<String, NamedQueryDefinition> copy;
        NamedQueryDefinition previous;
        if (NamedSQLQueryDefinition.class.isInstance(definition)) {
            throw new IllegalArgumentException("NamedSQLQueryDefinition instance incorrectly passed to registerNamedQueryDefinition");
        }
        if (!name.equals(definition.getName())) {
            definition = definition.makeCopy(name);
        }
        if ((previous = (copy = CollectionHelper.makeCopy(this.namedQueryDefinitionMap)).put(name, definition)) != null) {
            log.debugf("registering named query definition [%s] overriding previously registered definition [%s]", (Object)name, (Object)previous);
        }
        this.namedQueryDefinitionMap = CollectionHelper.toSmallMap(copy);
        this.removeNamedSQLQueryDefinition(name);
    }

    private synchronized void removeNamedSQLQueryDefinition(String name) {
        if (this.namedSqlQueryDefinitionMap.containsKey(name)) {
            Map<String, NamedSQLQueryDefinition> copy = CollectionHelper.makeCopy(this.namedSqlQueryDefinitionMap);
            copy.remove(name);
            this.namedSqlQueryDefinitionMap = CollectionHelper.toSmallMap(copy);
        }
    }

    public synchronized void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
        Map<String, NamedSQLQueryDefinition> copy;
        NamedQueryDefinition previous;
        if (!name.equals(definition.getName())) {
            definition = definition.makeCopy(name);
        }
        if ((previous = (NamedQueryDefinition)(copy = CollectionHelper.makeCopy(this.namedSqlQueryDefinitionMap)).put(name, definition)) != null) {
            log.debugf("registering named SQL query definition [%s] overriding previously registered definition [%s]", (Object)name, (Object)previous);
        }
        this.namedSqlQueryDefinitionMap = CollectionHelper.toSmallMap(copy);
        this.removeNamedQueryDefinition(name);
    }

    public synchronized void registerNamedProcedureCallMemento(String name, ProcedureCallMemento memento) {
        Map<String, ProcedureCallMemento> copy = CollectionHelper.makeCopy(this.procedureCallMementoMap);
        ProcedureCallMemento previous = copy.put(name, memento);
        if (previous != null) {
            log.debugf("registering named procedure call definition [%s] overriding previously registered definition [%s]", (Object)name, (Object)previous);
        }
        this.procedureCallMementoMap = CollectionHelper.toSmallMap(copy);
    }

    public Map<String, HibernateException> checkNamedQueries(QueryPlanCache queryPlanCache) {
        HashMap<String, HibernateException> errors = new HashMap<String, HibernateException>();
        log.debugf("Checking %s named HQL queries", this.namedQueryDefinitionMap.size());
        for (NamedQueryDefinition namedQueryDefinition : this.namedQueryDefinitionMap.values()) {
            try {
                log.debugf("Checking named query: %s", (Object)namedQueryDefinition.getName());
                queryPlanCache.getHQLQueryPlan(namedQueryDefinition.getQueryString(), false, Collections.EMPTY_MAP);
            }
            catch (HibernateException e) {
                errors.put(namedQueryDefinition.getName(), e);
            }
        }
        log.debugf("Checking %s named SQL queries", this.namedSqlQueryDefinitionMap.size());
        for (NamedSQLQueryDefinition namedSQLQueryDefinition : this.namedSqlQueryDefinitionMap.values()) {
            try {
                NativeSQLQuerySpecification spec;
                log.debugf("Checking named SQL query: %s", (Object)namedSQLQueryDefinition.getName());
                if (namedSQLQueryDefinition.getResultSetRef() != null) {
                    ResultSetMappingDefinition definition = this.getResultSetMappingDefinition(namedSQLQueryDefinition.getResultSetRef());
                    if (definition == null) {
                        throw new MappingException("Unable to find resultset-ref definition: " + namedSQLQueryDefinition.getResultSetRef());
                    }
                    spec = new NativeSQLQuerySpecification(namedSQLQueryDefinition.getQueryString(), definition.getQueryReturns(), namedSQLQueryDefinition.getQuerySpaces());
                } else {
                    spec = new NativeSQLQuerySpecification(namedSQLQueryDefinition.getQueryString(), namedSQLQueryDefinition.getQueryReturns(), namedSQLQueryDefinition.getQuerySpaces());
                }
                queryPlanCache.getNativeSQLQueryPlan(spec);
            }
            catch (HibernateException e) {
                errors.put(namedSQLQueryDefinition.getName(), e);
            }
        }
        return errors;
    }
}

