/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityDiscriminatorType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmGeneratorSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMultiTenancyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPolymorphismEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.IdentifierSourceAggregatedCompositeImpl;
import org.hibernate.boot.model.source.internal.hbm.IdentifierSourceNonAggregatedCompositeImpl;
import org.hibernate.boot.model.source.internal.hbm.IdentifierSourceSimpleImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.SubclassEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.TimestampAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.VersionAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.DiscriminatorSource;
import org.hibernate.boot.model.source.spi.EntityHierarchySource;
import org.hibernate.boot.model.source.spi.EntityNamingSource;
import org.hibernate.boot.model.source.spi.IdentifierSource;
import org.hibernate.boot.model.source.spi.InheritanceType;
import org.hibernate.boot.model.source.spi.MultiTenancySource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.boot.model.source.spi.VersionAttributeSource;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.internal.util.StringHelper;

public class EntityHierarchySourceImpl
implements EntityHierarchySource {
    private final RootEntitySourceImpl rootEntitySource;
    private final IdentifierSource identifierSource;
    private final VersionAttributeSource versionAttributeSource;
    private final DiscriminatorSource discriminatorSource;
    private final MultiTenancySource multiTenancySource;
    private final Caching caching;
    private final Caching naturalIdCaching;
    private InheritanceType hierarchyInheritanceType = InheritanceType.NO_INHERITANCE;
    private Set<String> collectedEntityNames = new HashSet<String>();

    public EntityHierarchySourceImpl(RootEntitySourceImpl rootEntitySource) {
        this.rootEntitySource = rootEntitySource;
        this.rootEntitySource.injectHierarchy(this);
        this.identifierSource = EntityHierarchySourceImpl.interpretIdentifierSource(rootEntitySource);
        this.versionAttributeSource = EntityHierarchySourceImpl.interpretVersionSource(rootEntitySource);
        this.discriminatorSource = EntityHierarchySourceImpl.interpretDiscriminatorSource(rootEntitySource);
        this.multiTenancySource = EntityHierarchySourceImpl.interpretMultiTenancySource(rootEntitySource);
        this.caching = Helper.createCaching(this.entityElement().getCache());
        this.naturalIdCaching = Helper.createNaturalIdCaching(rootEntitySource.jaxbEntityMapping().getNaturalIdCache());
        this.collectedEntityNames.add(rootEntitySource.getEntityNamingSource().getEntityName());
    }

    private static IdentifierSource interpretIdentifierSource(RootEntitySourceImpl rootEntitySource) {
        if (rootEntitySource.jaxbEntityMapping().getId() == null && rootEntitySource.jaxbEntityMapping().getCompositeId() == null) {
            throw new MappingException(String.format(Locale.ENGLISH, "Entity [%s] did not define an identifier", rootEntitySource.getEntityNamingSource().getEntityName()), rootEntitySource.origin());
        }
        if (rootEntitySource.jaxbEntityMapping().getId() != null) {
            return new IdentifierSourceSimpleImpl(rootEntitySource);
        }
        if (StringHelper.isEmpty(rootEntitySource.jaxbEntityMapping().getCompositeId().getName())) {
            if (rootEntitySource.jaxbEntityMapping().getCompositeId().isMapped() && StringHelper.isEmpty(rootEntitySource.jaxbEntityMapping().getCompositeId().getClazz())) {
                throw new MappingException("mapped composite identifier must name component class to use.", rootEntitySource.origin());
            }
            return new IdentifierSourceNonAggregatedCompositeImpl(rootEntitySource);
        }
        if (rootEntitySource.jaxbEntityMapping().getCompositeId().isMapped()) {
            throw new MappingException("cannot combine mapped=\"true\" with specified name", rootEntitySource.origin());
        }
        return new IdentifierSourceAggregatedCompositeImpl(rootEntitySource);
    }

    private static VersionAttributeSource interpretVersionSource(RootEntitySourceImpl rootEntitySource) {
        JaxbHbmRootEntityType entityElement = rootEntitySource.jaxbEntityMapping();
        if (entityElement.getVersion() != null) {
            return new VersionAttributeSourceImpl(rootEntitySource.sourceMappingDocument(), rootEntitySource, entityElement.getVersion());
        }
        if (entityElement.getTimestamp() != null) {
            return new TimestampAttributeSourceImpl(rootEntitySource.sourceMappingDocument(), rootEntitySource, entityElement.getTimestamp());
        }
        return null;
    }

    private static DiscriminatorSource interpretDiscriminatorSource(final RootEntitySourceImpl rootEntitySource) {
        final JaxbHbmEntityDiscriminatorType jaxbDiscriminatorMapping = rootEntitySource.jaxbEntityMapping().getDiscriminator();
        if (jaxbDiscriminatorMapping == null) {
            return null;
        }
        final RelationalValueSource relationalValueSource = RelationalValueSourceHelper.buildValueSource(rootEntitySource.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){
            private List columnOrFormulas;

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.DISCRIMINATOR;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public SizeSource getSizeSource() {
                return Helper.interpretSizeSource(jaxbDiscriminatorMapping.getLength(), (Integer)null, null);
            }

            @Override
            public String getFormulaAttribute() {
                return jaxbDiscriminatorMapping.getFormulaAttribute();
            }

            @Override
            public String getColumnAttribute() {
                return jaxbDiscriminatorMapping.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                if (this.columnOrFormulas == null) {
                    if (jaxbDiscriminatorMapping.getColumn() != null) {
                        if (jaxbDiscriminatorMapping.getFormula() != null) {
                            throw new MappingException(String.format(Locale.ENGLISH, "discriminator mapping [%s] named both <column/> and <formula/>, but only one or other allowed", rootEntitySource.getEntityNamingSource().getEntityName()), rootEntitySource.sourceMappingDocument().getOrigin());
                        }
                        this.columnOrFormulas = Collections.singletonList(jaxbDiscriminatorMapping.getColumn());
                    } else {
                        this.columnOrFormulas = jaxbDiscriminatorMapping.getFormula() != null ? Collections.singletonList(jaxbDiscriminatorMapping.getFormula()) : Collections.emptyList();
                    }
                }
                return this.columnOrFormulas;
            }

            @Override
            public Boolean isNullable() {
                return !jaxbDiscriminatorMapping.isNotNull();
            }
        });
        return new DiscriminatorSource(){

            @Override
            public EntityNaming getEntityNaming() {
                return rootEntitySource.getEntityNamingSource();
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return rootEntitySource.metadataBuildingContext();
            }

            @Override
            public RelationalValueSource getDiscriminatorRelationalValueSource() {
                return relationalValueSource;
            }

            @Override
            public String getExplicitHibernateTypeName() {
                return jaxbDiscriminatorMapping.getType();
            }

            @Override
            public boolean isForced() {
                return jaxbDiscriminatorMapping.isForce();
            }

            @Override
            public boolean isInserted() {
                return jaxbDiscriminatorMapping.isInsert();
            }
        };
    }

    private static MultiTenancySource interpretMultiTenancySource(final RootEntitySourceImpl rootEntitySource) {
        final JaxbHbmMultiTenancyType jaxbMultiTenancy = rootEntitySource.jaxbEntityMapping().getMultiTenancy();
        if (jaxbMultiTenancy == null) {
            return null;
        }
        final RelationalValueSource relationalValueSource = RelationalValueSourceHelper.buildValueSource(rootEntitySource.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){
            private List columnOrFormulas;

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.MULTI_TENANCY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getFormulaAttribute() {
                return jaxbMultiTenancy.getFormulaAttribute();
            }

            @Override
            public String getColumnAttribute() {
                return jaxbMultiTenancy.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                if (this.columnOrFormulas == null) {
                    if (jaxbMultiTenancy.getColumn() != null) {
                        if (jaxbMultiTenancy.getFormula() != null) {
                            throw new MappingException(String.format(Locale.ENGLISH, "discriminator mapping [%s] named both <column/> and <formula/>, but only one or other allowed", rootEntitySource.getEntityNamingSource().getEntityName()), rootEntitySource.sourceMappingDocument().getOrigin());
                        }
                        this.columnOrFormulas = Collections.singletonList(jaxbMultiTenancy.getColumn());
                    } else {
                        this.columnOrFormulas = jaxbMultiTenancy.getFormula() != null ? Collections.singletonList(jaxbMultiTenancy.getColumn()) : Collections.emptyList();
                    }
                }
                return this.columnOrFormulas;
            }

            @Override
            public Boolean isNullable() {
                return false;
            }
        });
        return new MultiTenancySource(){

            @Override
            public RelationalValueSource getRelationalValueSource() {
                return relationalValueSource;
            }

            @Override
            public boolean isShared() {
                return jaxbMultiTenancy.isShared();
            }

            @Override
            public boolean bindAsParameter() {
                return jaxbMultiTenancy.isBindAsParam();
            }
        };
    }

    @Override
    public InheritanceType getHierarchyInheritanceType() {
        return this.hierarchyInheritanceType;
    }

    @Override
    public RootEntitySourceImpl getRoot() {
        return this.rootEntitySource;
    }

    public void processSubclass(SubclassEntitySourceImpl subclassEntitySource) {
        InheritanceType inheritanceType = Helper.interpretInheritanceType(subclassEntitySource.jaxbEntityMapping());
        if (this.hierarchyInheritanceType == InheritanceType.NO_INHERITANCE) {
            this.hierarchyInheritanceType = inheritanceType;
        } else if (this.hierarchyInheritanceType != inheritanceType) {
            throw new MappingException("Mixed inheritance strategies not supported", subclassEntitySource.getOrigin());
        }
        this.collectedEntityNames.add(subclassEntitySource.getEntityNamingSource().getEntityName());
    }

    protected JaxbHbmRootEntityType entityElement() {
        return this.rootEntitySource.jaxbEntityMapping();
    }

    @Override
    public IdentifierSource getIdentifierSource() {
        return this.identifierSource;
    }

    @Override
    public VersionAttributeSource getVersionAttributeSource() {
        return this.versionAttributeSource;
    }

    @Override
    public EntityMode getEntityMode() {
        return this.rootEntitySource.determineEntityMode();
    }

    @Override
    public boolean isMutable() {
        return this.entityElement().isMutable();
    }

    @Override
    public boolean isExplicitPolymorphism() {
        return JaxbHbmPolymorphismEnum.EXPLICIT == this.entityElement().getPolymorphism();
    }

    @Override
    public String getWhere() {
        return this.entityElement().getWhere();
    }

    @Override
    public String getRowId() {
        return this.entityElement().getRowid();
    }

    @Override
    public OptimisticLockStyle getOptimisticLockStyle() {
        return this.entityElement().getOptimisticLock();
    }

    @Override
    public Caching getCaching() {
        return this.caching;
    }

    @Override
    public Caching getNaturalIdCaching() {
        return this.naturalIdCaching;
    }

    @Override
    public DiscriminatorSource getDiscriminatorSource() {
        return this.discriminatorSource;
    }

    @Override
    public MultiTenancySource getMultiTenancySource() {
        return this.multiTenancySource;
    }

    static IdentifierGeneratorDefinition interpretGeneratorDefinition(MappingDocument mappingDocument, EntityNamingSource entityNaming, JaxbHbmGeneratorSpecificationType jaxbGeneratorMapping) {
        if (jaxbGeneratorMapping == null) {
            return null;
        }
        String generatorName = jaxbGeneratorMapping.getClazz();
        IdentifierGeneratorDefinition identifierGeneratorDefinition = mappingDocument.getMetadataCollector().getIdentifierGenerator(generatorName);
        if (identifierGeneratorDefinition == null) {
            identifierGeneratorDefinition = new IdentifierGeneratorDefinition(entityNaming.getEntityName() + '.' + generatorName, generatorName, Helper.extractParameters(jaxbGeneratorMapping.getConfigParameters()));
        }
        return identifierGeneratorDefinition;
    }

    public Set<String> getContainedEntityNames() {
        return this.collectedEntityNames;
    }
}

