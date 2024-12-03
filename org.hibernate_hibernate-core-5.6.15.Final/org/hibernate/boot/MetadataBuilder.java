/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.SharedCacheMode
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot;

import javax.persistence.AttributeConverter;
import javax.persistence.SharedCacheMode;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.BasicType;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jboss.jandex.IndexView;

public interface MetadataBuilder {
    public MetadataBuilder applyImplicitCatalogName(String var1);

    public MetadataBuilder applyImplicitSchemaName(String var1);

    public MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy var1);

    public MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy var1);

    public MetadataBuilder applySharedCacheMode(SharedCacheMode var1);

    public MetadataBuilder applyAccessType(AccessType var1);

    public MetadataBuilder applyIndexView(IndexView var1);

    public MetadataBuilder applyScanOptions(ScanOptions var1);

    public MetadataBuilder applyScanEnvironment(ScanEnvironment var1);

    public MetadataBuilder applyScanner(Scanner var1);

    public MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory var1);

    public MetadataBuilder enableNewIdentifierGeneratorSupport(boolean var1);

    public MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean var1);

    public MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean var1);

    public MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean var1);

    public MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean var1);

    public MetadataBuilder applyBasicType(BasicType var1);

    public MetadataBuilder applyBasicType(BasicType var1, String ... var2);

    public MetadataBuilder applyBasicType(UserType var1, String ... var2);

    public MetadataBuilder applyBasicType(CompositeUserType var1, String ... var2);

    public MetadataBuilder applyTypes(TypeContributor var1);

    public MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition var1);

    public MetadataBuilder applyTempClassLoader(ClassLoader var1);

    public MetadataBuilder applySourceProcessOrdering(MetadataSourceType ... var1);

    public MetadataBuilder applySqlFunction(String var1, SQLFunction var2);

    public MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject var1);

    @Deprecated
    public MetadataBuilder applyAttributeConverter(AttributeConverterDefinition var1);

    public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> var1);

    public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> var1, boolean var2);

    public MetadataBuilder applyAttributeConverter(AttributeConverter var1);

    public MetadataBuilder applyAttributeConverter(AttributeConverter var1, boolean var2);

    public MetadataBuilder applyIdGenerationTypeInterpreter(IdGeneratorStrategyInterpreter var1);

    public <T extends MetadataBuilder> T unwrap(Class<T> var1);

    public Metadata build();
}

