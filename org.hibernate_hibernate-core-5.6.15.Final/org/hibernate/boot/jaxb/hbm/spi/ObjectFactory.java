/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyValueMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAuxiliaryDatabaseObjectType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmClassRenameType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCollectionIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeIndexType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDialectScopeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityDiscriminatorType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterAliasMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterParameterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmGeneratorSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdentifierGeneratorDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexManyToAnyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexManyToManyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListIndexType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToAnyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapKeyBasicType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapKeyCompositeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapKeyManyToManyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMultiTenancyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryCollectionLoadReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryJoinReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryPropertyReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryScalarReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNestedCompositeElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmParentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPrimitiveArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPropertiesType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmQueryParamType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmResultSetMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSimpleIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTimestampAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnionSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmVersionAttributeType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _JaxbHbmFilterTypeAliases_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "aliases");
    private static final QName _JaxbHbmNamedNativeQueryTypeQueryParam_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "query-param");
    private static final QName _JaxbHbmNamedNativeQueryTypeReturnScalar_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "return-scalar");
    private static final QName _JaxbHbmNamedNativeQueryTypeReturn_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "return");
    private static final QName _JaxbHbmNamedNativeQueryTypeReturnJoin_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "return-join");
    private static final QName _JaxbHbmNamedNativeQueryTypeLoadCollection_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "load-collection");
    private static final QName _JaxbHbmNamedNativeQueryTypeSynchronize_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "synchronize");
    private static final QName _JaxbHbmFilterDefinitionTypeFilterParam_QNAME = new QName("http://www.hibernate.org/xsd/orm/hbm", "filter-param");

    public JaxbHbmNativeQueryPropertyReturnType createJaxbHbmNativeQueryPropertyReturnType() {
        return new JaxbHbmNativeQueryPropertyReturnType();
    }

    public JaxbHbmNativeQueryReturnType createJaxbHbmNativeQueryReturnType() {
        return new JaxbHbmNativeQueryReturnType();
    }

    public JaxbHbmAuxiliaryDatabaseObjectType createJaxbHbmAuxiliaryDatabaseObjectType() {
        return new JaxbHbmAuxiliaryDatabaseObjectType();
    }

    public JaxbHbmFetchProfileType createJaxbHbmFetchProfileType() {
        return new JaxbHbmFetchProfileType();
    }

    public JaxbHbmHibernateMapping createJaxbHbmHibernateMapping() {
        return new JaxbHbmHibernateMapping();
    }

    public JaxbHbmToolingHintType createJaxbHbmToolingHintType() {
        return new JaxbHbmToolingHintType();
    }

    public JaxbHbmIdentifierGeneratorDefinitionType createJaxbHbmIdentifierGeneratorDefinitionType() {
        return new JaxbHbmIdentifierGeneratorDefinitionType();
    }

    public JaxbHbmTypeDefinitionType createJaxbHbmTypeDefinitionType() {
        return new JaxbHbmTypeDefinitionType();
    }

    public JaxbHbmFilterDefinitionType createJaxbHbmFilterDefinitionType() {
        return new JaxbHbmFilterDefinitionType();
    }

    public JaxbHbmClassRenameType createJaxbHbmClassRenameType() {
        return new JaxbHbmClassRenameType();
    }

    public JaxbHbmRootEntityType createJaxbHbmRootEntityType() {
        return new JaxbHbmRootEntityType();
    }

    public JaxbHbmDiscriminatorSubclassEntityType createJaxbHbmDiscriminatorSubclassEntityType() {
        return new JaxbHbmDiscriminatorSubclassEntityType();
    }

    public JaxbHbmJoinedSubclassEntityType createJaxbHbmJoinedSubclassEntityType() {
        return new JaxbHbmJoinedSubclassEntityType();
    }

    public JaxbHbmUnionSubclassEntityType createJaxbHbmUnionSubclassEntityType() {
        return new JaxbHbmUnionSubclassEntityType();
    }

    public JaxbHbmResultSetMappingType createJaxbHbmResultSetMappingType() {
        return new JaxbHbmResultSetMappingType();
    }

    public JaxbHbmNamedQueryType createJaxbHbmNamedQueryType() {
        return new JaxbHbmNamedQueryType();
    }

    public JaxbHbmNamedNativeQueryType createJaxbHbmNamedNativeQueryType() {
        return new JaxbHbmNamedNativeQueryType();
    }

    public JaxbHbmFilterParameterType createJaxbHbmFilterParameterType() {
        return new JaxbHbmFilterParameterType();
    }

    public JaxbHbmDialectScopeType createJaxbHbmDialectScopeType() {
        return new JaxbHbmDialectScopeType();
    }

    public JaxbHbmQueryParamType createJaxbHbmQueryParamType() {
        return new JaxbHbmQueryParamType();
    }

    public JaxbHbmNativeQueryScalarReturnType createJaxbHbmNativeQueryScalarReturnType() {
        return new JaxbHbmNativeQueryScalarReturnType();
    }

    public JaxbHbmNativeQueryJoinReturnType createJaxbHbmNativeQueryJoinReturnType() {
        return new JaxbHbmNativeQueryJoinReturnType();
    }

    public JaxbHbmNativeQueryCollectionLoadReturnType createJaxbHbmNativeQueryCollectionLoadReturnType() {
        return new JaxbHbmNativeQueryCollectionLoadReturnType();
    }

    public JaxbHbmSimpleIdType createJaxbHbmSimpleIdType() {
        return new JaxbHbmSimpleIdType();
    }

    public JaxbHbmCompositeIdType createJaxbHbmCompositeIdType() {
        return new JaxbHbmCompositeIdType();
    }

    public JaxbHbmVersionAttributeType createJaxbHbmVersionAttributeType() {
        return new JaxbHbmVersionAttributeType();
    }

    public JaxbHbmTimestampAttributeType createJaxbHbmTimestampAttributeType() {
        return new JaxbHbmTimestampAttributeType();
    }

    public JaxbHbmNaturalIdType createJaxbHbmNaturalIdType() {
        return new JaxbHbmNaturalIdType();
    }

    public JaxbHbmIdBagCollectionType createJaxbHbmIdBagCollectionType() {
        return new JaxbHbmIdBagCollectionType();
    }

    public JaxbHbmCollectionIdType createJaxbHbmCollectionIdType() {
        return new JaxbHbmCollectionIdType();
    }

    public JaxbHbmArrayType createJaxbHbmArrayType() {
        return new JaxbHbmArrayType();
    }

    public JaxbHbmBagCollectionType createJaxbHbmBagCollectionType() {
        return new JaxbHbmBagCollectionType();
    }

    public JaxbHbmListType createJaxbHbmListType() {
        return new JaxbHbmListType();
    }

    public JaxbHbmMapType createJaxbHbmMapType() {
        return new JaxbHbmMapType();
    }

    public JaxbHbmSetType createJaxbHbmSetType() {
        return new JaxbHbmSetType();
    }

    public JaxbHbmListIndexType createJaxbHbmListIndexType() {
        return new JaxbHbmListIndexType();
    }

    public JaxbHbmOneToManyCollectionElementType createJaxbHbmOneToManyCollectionElementType() {
        return new JaxbHbmOneToManyCollectionElementType();
    }

    public JaxbHbmOneToOneType createJaxbHbmOneToOneType() {
        return new JaxbHbmOneToOneType();
    }

    public JaxbHbmManyToOneType createJaxbHbmManyToOneType() {
        return new JaxbHbmManyToOneType();
    }

    public JaxbHbmManyToManyCollectionElementType createJaxbHbmManyToManyCollectionElementType() {
        return new JaxbHbmManyToManyCollectionElementType();
    }

    public JaxbHbmMultiTenancyType createJaxbHbmMultiTenancyType() {
        return new JaxbHbmMultiTenancyType();
    }

    public JaxbHbmEntityDiscriminatorType createJaxbHbmEntityDiscriminatorType() {
        return new JaxbHbmEntityDiscriminatorType();
    }

    public JaxbHbmAnyAssociationType createJaxbHbmAnyAssociationType() {
        return new JaxbHbmAnyAssociationType();
    }

    public JaxbHbmAnyValueMappingType createJaxbHbmAnyValueMappingType() {
        return new JaxbHbmAnyValueMappingType();
    }

    public JaxbHbmCacheType createJaxbHbmCacheType() {
        return new JaxbHbmCacheType();
    }

    public JaxbHbmNaturalIdCacheType createJaxbHbmNaturalIdCacheType() {
        return new JaxbHbmNaturalIdCacheType();
    }

    public JaxbHbmColumnType createJaxbHbmColumnType() {
        return new JaxbHbmColumnType();
    }

    public JaxbHbmCompositeAttributeType createJaxbHbmCompositeAttributeType() {
        return new JaxbHbmCompositeAttributeType();
    }

    public JaxbHbmCompositeCollectionElementType createJaxbHbmCompositeCollectionElementType() {
        return new JaxbHbmCompositeCollectionElementType();
    }

    public JaxbHbmDynamicComponentType createJaxbHbmDynamicComponentType() {
        return new JaxbHbmDynamicComponentType();
    }

    public JaxbHbmBasicCollectionElementType createJaxbHbmBasicCollectionElementType() {
        return new JaxbHbmBasicCollectionElementType();
    }

    public JaxbHbmFilterType createJaxbHbmFilterType() {
        return new JaxbHbmFilterType();
    }

    public JaxbHbmFilterAliasMappingType createJaxbHbmFilterAliasMappingType() {
        return new JaxbHbmFilterAliasMappingType();
    }

    public JaxbHbmGeneratorSpecificationType createJaxbHbmGeneratorSpecificationType() {
        return new JaxbHbmGeneratorSpecificationType();
    }

    public JaxbHbmIndexType createJaxbHbmIndexType() {
        return new JaxbHbmIndexType();
    }

    public JaxbHbmSecondaryTableType createJaxbHbmSecondaryTableType() {
        return new JaxbHbmSecondaryTableType();
    }

    public JaxbHbmKeyType createJaxbHbmKeyType() {
        return new JaxbHbmKeyType();
    }

    public JaxbHbmCompositeKeyManyToOneType createJaxbHbmCompositeKeyManyToOneType() {
        return new JaxbHbmCompositeKeyManyToOneType();
    }

    public JaxbHbmCompositeKeyBasicAttributeType createJaxbHbmCompositeKeyBasicAttributeType() {
        return new JaxbHbmCompositeKeyBasicAttributeType();
    }

    public JaxbHbmLoaderType createJaxbHbmLoaderType() {
        return new JaxbHbmLoaderType();
    }

    public JaxbHbmManyToAnyCollectionElementType createJaxbHbmManyToAnyCollectionElementType() {
        return new JaxbHbmManyToAnyCollectionElementType();
    }

    public JaxbHbmMapKeyBasicType createJaxbHbmMapKeyBasicType() {
        return new JaxbHbmMapKeyBasicType();
    }

    public JaxbHbmMapKeyCompositeType createJaxbHbmMapKeyCompositeType() {
        return new JaxbHbmMapKeyCompositeType();
    }

    public JaxbHbmMapKeyManyToManyType createJaxbHbmMapKeyManyToManyType() {
        return new JaxbHbmMapKeyManyToManyType();
    }

    public JaxbHbmCompositeIndexType createJaxbHbmCompositeIndexType() {
        return new JaxbHbmCompositeIndexType();
    }

    public JaxbHbmIndexManyToManyType createJaxbHbmIndexManyToManyType() {
        return new JaxbHbmIndexManyToManyType();
    }

    public JaxbHbmIndexManyToAnyType createJaxbHbmIndexManyToAnyType() {
        return new JaxbHbmIndexManyToAnyType();
    }

    public JaxbHbmNestedCompositeElementType createJaxbHbmNestedCompositeElementType() {
        return new JaxbHbmNestedCompositeElementType();
    }

    public JaxbHbmParentType createJaxbHbmParentType() {
        return new JaxbHbmParentType();
    }

    public JaxbHbmPrimitiveArrayType createJaxbHbmPrimitiveArrayType() {
        return new JaxbHbmPrimitiveArrayType();
    }

    public JaxbHbmPropertiesType createJaxbHbmPropertiesType() {
        return new JaxbHbmPropertiesType();
    }

    public JaxbHbmBasicAttributeType createJaxbHbmBasicAttributeType() {
        return new JaxbHbmBasicAttributeType();
    }

    public JaxbHbmSynchronizeType createJaxbHbmSynchronizeType() {
        return new JaxbHbmSynchronizeType();
    }

    public JaxbHbmTuplizerType createJaxbHbmTuplizerType() {
        return new JaxbHbmTuplizerType();
    }

    public JaxbHbmTypeSpecificationType createJaxbHbmTypeSpecificationType() {
        return new JaxbHbmTypeSpecificationType();
    }

    public JaxbHbmConfigParameterType createJaxbHbmConfigParameterType() {
        return new JaxbHbmConfigParameterType();
    }

    public JaxbHbmCustomSqlDmlType createJaxbHbmCustomSqlDmlType() {
        return new JaxbHbmCustomSqlDmlType();
    }

    public JaxbHbmNativeQueryPropertyReturnType.JaxbHbmReturnColumn createJaxbHbmNativeQueryPropertyReturnTypeJaxbHbmReturnColumn() {
        return new JaxbHbmNativeQueryPropertyReturnType.JaxbHbmReturnColumn();
    }

    public JaxbHbmNativeQueryReturnType.JaxbHbmReturnDiscriminator createJaxbHbmNativeQueryReturnTypeJaxbHbmReturnDiscriminator() {
        return new JaxbHbmNativeQueryReturnType.JaxbHbmReturnDiscriminator();
    }

    public JaxbHbmAuxiliaryDatabaseObjectType.JaxbHbmDefinition createJaxbHbmAuxiliaryDatabaseObjectTypeJaxbHbmDefinition() {
        return new JaxbHbmAuxiliaryDatabaseObjectType.JaxbHbmDefinition();
    }

    public JaxbHbmFetchProfileType.JaxbHbmFetch createJaxbHbmFetchProfileTypeJaxbHbmFetch() {
        return new JaxbHbmFetchProfileType.JaxbHbmFetch();
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="aliases", scope=JaxbHbmFilterType.class)
    public JAXBElement<JaxbHbmFilterAliasMappingType> createJaxbHbmFilterTypeAliases(JaxbHbmFilterAliasMappingType value) {
        return new JAXBElement(_JaxbHbmFilterTypeAliases_QNAME, JaxbHbmFilterAliasMappingType.class, JaxbHbmFilterType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="query-param", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmQueryParamType> createJaxbHbmNamedNativeQueryTypeQueryParam(JaxbHbmQueryParamType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeQueryParam_QNAME, JaxbHbmQueryParamType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="return-scalar", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmNativeQueryScalarReturnType> createJaxbHbmNamedNativeQueryTypeReturnScalar(JaxbHbmNativeQueryScalarReturnType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeReturnScalar_QNAME, JaxbHbmNativeQueryScalarReturnType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="return", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmNativeQueryReturnType> createJaxbHbmNamedNativeQueryTypeReturn(JaxbHbmNativeQueryReturnType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeReturn_QNAME, JaxbHbmNativeQueryReturnType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="return-join", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmNativeQueryJoinReturnType> createJaxbHbmNamedNativeQueryTypeReturnJoin(JaxbHbmNativeQueryJoinReturnType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeReturnJoin_QNAME, JaxbHbmNativeQueryJoinReturnType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="load-collection", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmNativeQueryCollectionLoadReturnType> createJaxbHbmNamedNativeQueryTypeLoadCollection(JaxbHbmNativeQueryCollectionLoadReturnType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeLoadCollection_QNAME, JaxbHbmNativeQueryCollectionLoadReturnType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="synchronize", scope=JaxbHbmNamedNativeQueryType.class)
    public JAXBElement<JaxbHbmSynchronizeType> createJaxbHbmNamedNativeQueryTypeSynchronize(JaxbHbmSynchronizeType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeSynchronize_QNAME, JaxbHbmSynchronizeType.class, JaxbHbmNamedNativeQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="query-param", scope=JaxbHbmNamedQueryType.class)
    public JAXBElement<JaxbHbmQueryParamType> createJaxbHbmNamedQueryTypeQueryParam(JaxbHbmQueryParamType value) {
        return new JAXBElement(_JaxbHbmNamedNativeQueryTypeQueryParam_QNAME, JaxbHbmQueryParamType.class, JaxbHbmNamedQueryType.class, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.hibernate.org/xsd/orm/hbm", name="filter-param", scope=JaxbHbmFilterDefinitionType.class)
    public JAXBElement<JaxbHbmFilterParameterType> createJaxbHbmFilterDefinitionTypeFilterParam(JaxbHbmFilterParameterType value) {
        return new JAXBElement(_JaxbHbmFilterDefinitionTypeFilterParam_QNAME, JaxbHbmFilterParameterType.class, JaxbHbmFilterDefinitionType.class, (Object)value);
    }
}

