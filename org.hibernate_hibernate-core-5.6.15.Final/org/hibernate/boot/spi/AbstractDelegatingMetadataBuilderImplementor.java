/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.SharedCacheMode
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.spi;

import javax.persistence.AttributeConverter;
import javax.persistence.SharedCacheMode;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.BasicType;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jboss.jandex.IndexView;

public abstract class AbstractDelegatingMetadataBuilderImplementor<T extends MetadataBuilderImplementor>
implements MetadataBuilderImplementor {
    private final MetadataBuilderImplementor delegate;

    @Deprecated
    public MetadataBuilderImplementor getDelegate() {
        return this.delegate;
    }

    protected MetadataBuilderImplementor delegate() {
        return this.delegate;
    }

    public AbstractDelegatingMetadataBuilderImplementor(MetadataBuilderImplementor delegate) {
        this.delegate = delegate;
    }

    protected abstract T getThis();

    @Override
    public MetadataBuilder applyImplicitSchemaName(String implicitSchemaName) {
        this.delegate.applyImplicitSchemaName(implicitSchemaName);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyImplicitCatalogName(String implicitCatalogName) {
        this.delegate.applyImplicitCatalogName(implicitCatalogName);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy) {
        this.delegate.applyImplicitNamingStrategy(namingStrategy);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy) {
        this.delegate.applyPhysicalNamingStrategy(namingStrategy);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applySharedCacheMode(SharedCacheMode cacheMode) {
        this.delegate.applySharedCacheMode(cacheMode);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAccessType(AccessType accessType) {
        this.delegate.applyAccessType(accessType);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyIndexView(IndexView jandexView) {
        this.delegate.applyIndexView(jandexView);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyScanOptions(ScanOptions scanOptions) {
        this.delegate.applyScanOptions(scanOptions);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment) {
        this.delegate.applyScanEnvironment(scanEnvironment);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyScanner(Scanner scanner) {
        this.delegate.applyScanner(scanner);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory) {
        this.delegate.applyArchiveDescriptorFactory(factory);
        return this.getThis();
    }

    @Override
    public MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enable) {
        this.delegate.enableNewIdentifierGeneratorSupport(enable);
        return this.getThis();
    }

    @Override
    public MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled) {
        this.delegate.enableExplicitDiscriminatorsForJoinedSubclassSupport(enabled);
        return this.getThis();
    }

    @Override
    public MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled) {
        this.delegate.enableImplicitDiscriminatorsForJoinedSubclassSupport(enabled);
        return this.getThis();
    }

    @Override
    public MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported) {
        this.delegate.enableImplicitForcingOfDiscriminatorsInSelect(supported);
        return this.getThis();
    }

    @Override
    public MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled) {
        this.delegate.enableGlobalNationalizedCharacterDataSupport(enabled);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyBasicType(BasicType type) {
        this.delegate.applyBasicType(type);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyBasicType(BasicType type, String ... keys) {
        this.delegate.applyBasicType(type, keys);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyBasicType(UserType type, String ... keys) {
        this.delegate.applyBasicType(type, keys);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyBasicType(CompositeUserType type, String ... keys) {
        this.delegate.applyBasicType(type, keys);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyTypes(TypeContributor typeContributor) {
        this.delegate.applyTypes(typeContributor);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
        this.delegate.applyCacheRegionDefinition(cacheRegionDefinition);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader) {
        this.delegate.applyTempClassLoader(tempClassLoader);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applySourceProcessOrdering(MetadataSourceType ... sourceTypes) {
        this.delegate.applySourceProcessOrdering(sourceTypes);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applySqlFunction(String functionName, SQLFunction function) {
        this.delegate.applySqlFunction(functionName, function);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
        this.delegate.applyAuxiliaryDatabaseObject(auxiliaryDatabaseObject);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition) {
        this.delegate.applyAttributeConverter(definition);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
        this.delegate.applyAttributeConverter(attributeConverterClass);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
        this.delegate.applyAttributeConverter(attributeConverterClass, autoApply);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter) {
        this.delegate.applyAttributeConverter(attributeConverter);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
        this.delegate.applyAttributeConverter(attributeConverter, autoApply);
        return this.getThis();
    }

    @Override
    public MetadataBuilder applyIdGenerationTypeInterpreter(IdGeneratorStrategyInterpreter interpreter) {
        this.delegate.applyIdGenerationTypeInterpreter(interpreter);
        return this.getThis();
    }

    public <M extends MetadataBuilder> M unwrap(Class<M> type) {
        return this.delegate.unwrap(type);
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.delegate.getMetadataBuildingOptions();
    }

    @Override
    public Metadata build() {
        return this.delegate.build();
    }
}

