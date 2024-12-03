/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

public interface Metadata
extends Mapping {
    public SessionFactoryBuilder getSessionFactoryBuilder();

    public SessionFactory buildSessionFactory();

    public UUID getUUID();

    public Database getDatabase();

    public Collection<PersistentClass> getEntityBindings();

    public PersistentClass getEntityBinding(String var1);

    public Collection<org.hibernate.mapping.Collection> getCollectionBindings();

    public org.hibernate.mapping.Collection getCollectionBinding(String var1);

    public Map<String, String> getImports();

    public NamedQueryDefinition getNamedQueryDefinition(String var1);

    public Collection<NamedQueryDefinition> getNamedQueryDefinitions();

    public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String var1);

    public Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();

    public Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions();

    public ResultSetMappingDefinition getResultSetMapping(String var1);

    public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions();

    public TypeDefinition getTypeDefinition(String var1);

    public Map<String, FilterDefinition> getFilterDefinitions();

    public FilterDefinition getFilterDefinition(String var1);

    public FetchProfile getFetchProfile(String var1);

    public Collection<FetchProfile> getFetchProfiles();

    public NamedEntityGraphDefinition getNamedEntityGraph(String var1);

    public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs();

    public IdentifierGeneratorDefinition getIdentifierGenerator(String var1);

    public Collection<Table> collectTableMappings();

    public Map<String, SQLFunction> getSqlFunctionMap();
}

