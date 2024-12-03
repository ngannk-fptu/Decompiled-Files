/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.spi.TypeConfiguration;

public abstract class AbstractDelegatingMetadata
implements MetadataImplementor {
    private final MetadataImplementor delegate;

    public AbstractDelegatingMetadata(MetadataImplementor delegate) {
        this.delegate = delegate;
    }

    protected MetadataImplementor delegate() {
        return this.delegate;
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return this.delegate.getIdentifierGeneratorFactory();
    }

    @Override
    public Type getIdentifierType(String className) throws MappingException {
        return this.delegate.getIdentifierType(className);
    }

    @Override
    public String getIdentifierPropertyName(String className) throws MappingException {
        return this.delegate.getIdentifierPropertyName(className);
    }

    @Override
    public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
        return this.delegate.getReferencedPropertyType(className, propertyName);
    }

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder() {
        return this.delegate.getSessionFactoryBuilder();
    }

    @Override
    public SessionFactory buildSessionFactory() {
        return this.delegate.buildSessionFactory();
    }

    @Override
    public UUID getUUID() {
        return this.delegate.getUUID();
    }

    @Override
    public Database getDatabase() {
        return this.delegate.getDatabase();
    }

    @Override
    public Collection<PersistentClass> getEntityBindings() {
        return this.delegate.getEntityBindings();
    }

    @Override
    public PersistentClass getEntityBinding(String entityName) {
        return this.delegate.getEntityBinding(entityName);
    }

    @Override
    public Collection<org.hibernate.mapping.Collection> getCollectionBindings() {
        return this.delegate.getCollectionBindings();
    }

    @Override
    public org.hibernate.mapping.Collection getCollectionBinding(String role) {
        return this.delegate.getCollectionBinding(role);
    }

    @Override
    public Map<String, String> getImports() {
        return this.delegate.getImports();
    }

    @Override
    public NamedQueryDefinition getNamedQueryDefinition(String name) {
        return this.delegate.getNamedQueryDefinition(name);
    }

    @Override
    public Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
        return this.delegate.getNamedQueryDefinitions();
    }

    @Override
    public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
        return this.delegate.getNamedNativeQueryDefinition(name);
    }

    @Override
    public Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
        return this.delegate.getNamedNativeQueryDefinitions();
    }

    @Override
    public Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
        return this.delegate.getNamedProcedureCallDefinitions();
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.delegate.getResultSetMapping(name);
    }

    @Override
    public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
        return this.delegate.getResultSetMappingDefinitions();
    }

    @Override
    public TypeDefinition getTypeDefinition(String typeName) {
        return this.delegate.getTypeDefinition(typeName);
    }

    @Override
    public Map<String, FilterDefinition> getFilterDefinitions() {
        return this.delegate.getFilterDefinitions();
    }

    @Override
    public FilterDefinition getFilterDefinition(String name) {
        return this.delegate.getFilterDefinition(name);
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return this.delegate.getFetchProfile(name);
    }

    @Override
    public Collection<FetchProfile> getFetchProfiles() {
        return this.delegate.getFetchProfiles();
    }

    @Override
    public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
        return this.delegate.getNamedEntityGraph(name);
    }

    @Override
    public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
        return this.delegate.getNamedEntityGraphs();
    }

    @Override
    public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
        return this.delegate.getIdentifierGenerator(name);
    }

    @Override
    public Collection<Table> collectTableMappings() {
        return this.delegate.collectTableMappings();
    }

    @Override
    public Map<String, SQLFunction> getSqlFunctionMap() {
        return this.delegate.getSqlFunctionMap();
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.delegate.getMetadataBuildingOptions();
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.delegate.getTypeConfiguration();
    }

    @Override
    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.delegate.getTypeResolver();
    }

    @Override
    public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
        return this.delegate.buildNamedQueryRepository(sessionFactory);
    }

    @Override
    public void validate() throws MappingException {
        this.delegate.validate();
    }

    @Override
    public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
        return this.delegate.getMappedSuperclassMappingsCopy();
    }

    @Override
    public void initSessionFactory(SessionFactoryImplementor sessionFactory) {
        this.delegate.initSessionFactory(sessionFactory);
    }
}

