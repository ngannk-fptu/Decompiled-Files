/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.xml.bind.annotation.XmlRegistry;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAssociationOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbBasic;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCascadeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCollectionTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumnResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConstructorResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConverter;
import org.hibernate.boot.jaxb.mapping.spi.JaxbDiscriminatorColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbElementCollection;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddableAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbedded;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddedId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntity;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListener;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityMappings;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbFieldResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbGeneratedValue;
import org.hibernate.boot.jaxb.mapping.spi.JaxbId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIdClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIndex;
import org.hibernate.boot.jaxb.mapping.spi.JaxbInheritance;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbLob;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedAttributeNode;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedEntityGraph;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedNativeQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedStoredProcedureQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedSubgraph;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOrderColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitDefaults;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitMetadata;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrimaryKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbQueryHint;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSecondaryTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSqlResultSetMapping;
import org.hibernate.boot.jaxb.mapping.spi.JaxbStoredProcedureParameter;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTransient;
import org.hibernate.boot.jaxb.mapping.spi.JaxbUniqueConstraint;
import org.hibernate.boot.jaxb.mapping.spi.JaxbVersion;

@XmlRegistry
public class ObjectFactory {
    public JaxbEntityMappings createJaxbEntityMappings() {
        return new JaxbEntityMappings();
    }

    public JaxbPersistenceUnitMetadata createJaxbPersistenceUnitMetadata() {
        return new JaxbPersistenceUnitMetadata();
    }

    public JaxbSequenceGenerator createJaxbSequenceGenerator() {
        return new JaxbSequenceGenerator();
    }

    public JaxbTableGenerator createJaxbTableGenerator() {
        return new JaxbTableGenerator();
    }

    public JaxbNamedQuery createJaxbNamedQuery() {
        return new JaxbNamedQuery();
    }

    public JaxbNamedNativeQuery createJaxbNamedNativeQuery() {
        return new JaxbNamedNativeQuery();
    }

    public JaxbNamedStoredProcedureQuery createJaxbNamedStoredProcedureQuery() {
        return new JaxbNamedStoredProcedureQuery();
    }

    public JaxbSqlResultSetMapping createJaxbSqlResultSetMapping() {
        return new JaxbSqlResultSetMapping();
    }

    public JaxbMappedSuperclass createJaxbMappedSuperclass() {
        return new JaxbMappedSuperclass();
    }

    public JaxbEntity createJaxbEntity() {
        return new JaxbEntity();
    }

    public JaxbEmbeddable createJaxbEmbeddable() {
        return new JaxbEmbeddable();
    }

    public JaxbConverter createJaxbConverter() {
        return new JaxbConverter();
    }

    public JaxbEmptyType createJaxbEmptyType() {
        return new JaxbEmptyType();
    }

    public JaxbPersistenceUnitDefaults createJaxbPersistenceUnitDefaults() {
        return new JaxbPersistenceUnitDefaults();
    }

    public JaxbAssociationOverride createJaxbAssociationOverride() {
        return new JaxbAssociationOverride();
    }

    public JaxbAttributeOverride createJaxbAttributeOverride() {
        return new JaxbAttributeOverride();
    }

    public JaxbAttributes createJaxbAttributes() {
        return new JaxbAttributes();
    }

    public JaxbBasic createJaxbBasic() {
        return new JaxbBasic();
    }

    public JaxbCascadeType createJaxbCascadeType() {
        return new JaxbCascadeType();
    }

    public JaxbCollectionTable createJaxbCollectionTable() {
        return new JaxbCollectionTable();
    }

    public JaxbColumn createJaxbColumn() {
        return new JaxbColumn();
    }

    public JaxbColumnResult createJaxbColumnResult() {
        return new JaxbColumnResult();
    }

    public JaxbConstructorResult createJaxbConstructorResult() {
        return new JaxbConstructorResult();
    }

    public JaxbConvert createJaxbConvert() {
        return new JaxbConvert();
    }

    public JaxbDiscriminatorColumn createJaxbDiscriminatorColumn() {
        return new JaxbDiscriminatorColumn();
    }

    public JaxbElementCollection createJaxbElementCollection() {
        return new JaxbElementCollection();
    }

    public JaxbEmbeddableAttributes createJaxbEmbeddableAttributes() {
        return new JaxbEmbeddableAttributes();
    }

    public JaxbEmbedded createJaxbEmbedded() {
        return new JaxbEmbedded();
    }

    public JaxbEmbeddedId createJaxbEmbeddedId() {
        return new JaxbEmbeddedId();
    }

    public JaxbEntityListener createJaxbEntityListener() {
        return new JaxbEntityListener();
    }

    public JaxbEntityListeners createJaxbEntityListeners() {
        return new JaxbEntityListeners();
    }

    public JaxbEntityResult createJaxbEntityResult() {
        return new JaxbEntityResult();
    }

    public JaxbFieldResult createJaxbFieldResult() {
        return new JaxbFieldResult();
    }

    public JaxbForeignKey createJaxbForeignKey() {
        return new JaxbForeignKey();
    }

    public JaxbGeneratedValue createJaxbGeneratedValue() {
        return new JaxbGeneratedValue();
    }

    public JaxbId createJaxbId() {
        return new JaxbId();
    }

    public JaxbIdClass createJaxbIdClass() {
        return new JaxbIdClass();
    }

    public JaxbIndex createJaxbIndex() {
        return new JaxbIndex();
    }

    public JaxbInheritance createJaxbInheritance() {
        return new JaxbInheritance();
    }

    public JaxbJoinColumn createJaxbJoinColumn() {
        return new JaxbJoinColumn();
    }

    public JaxbJoinTable createJaxbJoinTable() {
        return new JaxbJoinTable();
    }

    public JaxbLob createJaxbLob() {
        return new JaxbLob();
    }

    public JaxbManyToMany createJaxbManyToMany() {
        return new JaxbManyToMany();
    }

    public JaxbManyToOne createJaxbManyToOne() {
        return new JaxbManyToOne();
    }

    public JaxbMapKey createJaxbMapKey() {
        return new JaxbMapKey();
    }

    public JaxbMapKeyClass createJaxbMapKeyClass() {
        return new JaxbMapKeyClass();
    }

    public JaxbMapKeyColumn createJaxbMapKeyColumn() {
        return new JaxbMapKeyColumn();
    }

    public JaxbMapKeyJoinColumn createJaxbMapKeyJoinColumn() {
        return new JaxbMapKeyJoinColumn();
    }

    public JaxbNamedAttributeNode createJaxbNamedAttributeNode() {
        return new JaxbNamedAttributeNode();
    }

    public JaxbNamedEntityGraph createJaxbNamedEntityGraph() {
        return new JaxbNamedEntityGraph();
    }

    public JaxbNamedSubgraph createJaxbNamedSubgraph() {
        return new JaxbNamedSubgraph();
    }

    public JaxbOneToMany createJaxbOneToMany() {
        return new JaxbOneToMany();
    }

    public JaxbOneToOne createJaxbOneToOne() {
        return new JaxbOneToOne();
    }

    public JaxbOrderColumn createJaxbOrderColumn() {
        return new JaxbOrderColumn();
    }

    public JaxbPostLoad createJaxbPostLoad() {
        return new JaxbPostLoad();
    }

    public JaxbPostPersist createJaxbPostPersist() {
        return new JaxbPostPersist();
    }

    public JaxbPostRemove createJaxbPostRemove() {
        return new JaxbPostRemove();
    }

    public JaxbPostUpdate createJaxbPostUpdate() {
        return new JaxbPostUpdate();
    }

    public JaxbPrePersist createJaxbPrePersist() {
        return new JaxbPrePersist();
    }

    public JaxbPreRemove createJaxbPreRemove() {
        return new JaxbPreRemove();
    }

    public JaxbPreUpdate createJaxbPreUpdate() {
        return new JaxbPreUpdate();
    }

    public JaxbPrimaryKeyJoinColumn createJaxbPrimaryKeyJoinColumn() {
        return new JaxbPrimaryKeyJoinColumn();
    }

    public JaxbQueryHint createJaxbQueryHint() {
        return new JaxbQueryHint();
    }

    public JaxbSecondaryTable createJaxbSecondaryTable() {
        return new JaxbSecondaryTable();
    }

    public JaxbStoredProcedureParameter createJaxbStoredProcedureParameter() {
        return new JaxbStoredProcedureParameter();
    }

    public JaxbTable createJaxbTable() {
        return new JaxbTable();
    }

    public JaxbTransient createJaxbTransient() {
        return new JaxbTransient();
    }

    public JaxbUniqueConstraint createJaxbUniqueConstraint() {
        return new JaxbUniqueConstraint();
    }

    public JaxbVersion createJaxbVersion() {
        return new JaxbVersion();
    }
}

