/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.Lob
 *  javax.persistence.MapKeyEnumerated
 *  javax.persistence.MapKeyTemporal
 *  javax.persistence.Temporal
 *  javax.persistence.TemporalType
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyTemporal;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.type.CharacterArrayClobType;
import org.hibernate.type.CharacterArrayNClobType;
import org.hibernate.type.CharacterNCharType;
import org.hibernate.type.EnumType;
import org.hibernate.type.PrimitiveCharacterArrayClobType;
import org.hibernate.type.PrimitiveCharacterArrayNClobType;
import org.hibernate.type.SerializableToBlobType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.WrappedMaterializedBlobType;
import org.hibernate.usertype.DynamicParameterizedType;
import org.jboss.logging.Logger;

public class SimpleValueBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SimpleValueBinder.class.getName());
    private MetadataBuildingContext buildingContext;
    private String propertyName;
    private String returnedClassName;
    private Ejb3Column[] columns;
    private String persistentClassName;
    private String explicitType = "";
    private String defaultType = "";
    private Properties typeParameters = new Properties();
    private boolean isNationalized;
    private boolean isLob;
    private Table table;
    private SimpleValue simpleValue;
    private boolean isVersion;
    private String timeStampVersionType;
    private boolean key;
    private String referencedEntityName;
    private XProperty xproperty;
    private AccessType accessType;
    private ConverterDescriptor attributeConverterDescriptor;

    public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName;
    }

    public boolean isVersion() {
        return this.isVersion;
    }

    public void setVersion(boolean isVersion) {
        this.isVersion = isVersion;
        if (isVersion && this.simpleValue != null) {
            this.simpleValue.makeVersion();
        }
    }

    public void setTimestampVersionType(String versionType) {
        this.timeStampVersionType = versionType;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setReturnedClassName(String returnedClassName) {
        this.returnedClassName = returnedClassName;
        if (this.defaultType.length() == 0) {
            this.defaultType = returnedClassName;
        }
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setColumns(Ejb3Column[] columns) {
        this.columns = columns;
    }

    public void setPersistentClassName(String persistentClassName) {
        this.persistentClassName = persistentClassName;
    }

    public void setType(XProperty property, XClass returnedClass, String declaringClassName, ConverterDescriptor attributeConverterDescriptor) {
        if (returnedClass == null) {
            return;
        }
        XClass returnedClassOrElement = returnedClass;
        boolean isArray = false;
        if (property.isArray()) {
            returnedClassOrElement = property.getElementClass();
            isArray = true;
        }
        this.xproperty = property;
        Properties typeParameters = this.typeParameters;
        typeParameters.clear();
        String type = "";
        if (this.getDialect().supportsNationalizedTypes()) {
            this.isNationalized = property.isAnnotationPresent(Nationalized.class) || this.buildingContext.getBuildingOptions().useNationalizedCharacterData();
        }
        Type annType = null;
        if (!this.key && property.isAnnotationPresent(Type.class) || this.key && property.isAnnotationPresent(MapKeyType.class)) {
            if (this.key) {
                MapKeyType ann = (MapKeyType)property.getAnnotation(MapKeyType.class);
                annType = ann.value();
            } else {
                annType = (Type)property.getAnnotation(Type.class);
            }
        }
        if (annType != null) {
            this.setExplicitType(annType);
            type = this.explicitType;
        } else if (!this.key && property.isAnnotationPresent(Temporal.class) || this.key && property.isAnnotationPresent(MapKeyTemporal.class)) {
            boolean isDate;
            if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Date.class)) {
                isDate = true;
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Calendar.class)) {
                isDate = false;
            } else {
                throw new AnnotationException("@Temporal should only be set on a java.util.Date or java.util.Calendar property: " + StringHelper.qualify(this.persistentClassName, this.propertyName));
            }
            TemporalType temporalType = this.getTemporalType(property);
            switch (temporalType) {
                case DATE: {
                    type = isDate ? "date" : "calendar_date";
                    break;
                }
                case TIME: {
                    type = "time";
                    if (isDate) break;
                    throw new NotYetImplementedException("Calendar cannot persist TIME only" + StringHelper.qualify(this.persistentClassName, this.propertyName));
                }
                case TIMESTAMP: {
                    type = isDate ? "timestamp" : "calendar";
                    break;
                }
                default: {
                    throw new AssertionFailure("Unknown temporal type: " + temporalType);
                }
            }
            this.explicitType = type;
        } else if (!this.key && property.isAnnotationPresent(Lob.class)) {
            this.isLob = true;
            if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Clob.class)) {
                type = this.isNationalized ? StandardBasicTypes.NCLOB.getName() : StandardBasicTypes.CLOB.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, NClob.class)) {
                type = StandardBasicTypes.NCLOB.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Blob.class)) {
                type = "blob";
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, String.class)) {
                type = this.isNationalized ? StandardBasicTypes.MATERIALIZED_NCLOB.getName() : StandardBasicTypes.MATERIALIZED_CLOB.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Character.class) && isArray) {
                type = this.isNationalized ? CharacterArrayNClobType.class.getName() : CharacterArrayClobType.class.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Character.TYPE) && isArray) {
                type = this.isNationalized ? PrimitiveCharacterArrayNClobType.class.getName() : PrimitiveCharacterArrayClobType.class.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Byte.class) && isArray) {
                type = WrappedMaterializedBlobType.class.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Byte.TYPE) && isArray) {
                type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().toXClass(Serializable.class).isAssignableFrom(returnedClassOrElement)) {
                type = SerializableToBlobType.class.getName();
                typeParameters.setProperty("classname", returnedClassOrElement.getName());
            } else {
                type = "blob";
            }
            this.defaultType = type;
        } else if (!this.key && property.isAnnotationPresent(Enumerated.class) || this.key && property.isAnnotationPresent(MapKeyEnumerated.class)) {
            Class attributeJavaType = this.buildingContext.getBootstrapContext().getReflectionManager().toClass(returnedClassOrElement);
            if (!Enum.class.isAssignableFrom(attributeJavaType)) {
                throw new AnnotationException(String.format("Attribute [%s.%s] was annotated as enumerated, but its java type is not an enum [%s]", declaringClassName, this.xproperty.getName(), attributeJavaType.getName()));
            }
            this.explicitType = type = EnumType.class.getName();
        } else if (this.isNationalized) {
            if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, String.class)) {
                this.explicitType = type = StringNVarcharType.INSTANCE.getName();
            } else if (this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Character.class) || this.buildingContext.getBootstrapContext().getReflectionManager().equals(returnedClassOrElement, Character.TYPE)) {
                type = isArray ? StringNVarcharType.INSTANCE.getName() : CharacterNCharType.INSTANCE.getName();
                this.explicitType = type;
            }
        }
        if (this.columns == null) {
            throw new AssertionFailure("SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType");
        }
        if ("".equals(type) && returnedClassOrElement.isEnum()) {
            type = EnumType.class.getName();
        }
        this.defaultType = BinderHelper.isEmptyAnnotationValue(type) ? this.returnedClassName : type;
        this.typeParameters = typeParameters;
        this.applyAttributeConverter(property, attributeConverterDescriptor);
    }

    private Dialect getDialect() {
        return this.buildingContext.getBuildingOptions().getServiceRegistry().getService(JdbcServices.class).getJdbcEnvironment().getDialect();
    }

    private void applyAttributeConverter(XProperty property, ConverterDescriptor attributeConverterDescriptor) {
        if (attributeConverterDescriptor == null) {
            return;
        }
        LOG.debugf("Starting applyAttributeConverter [%s:%s]", this.persistentClassName, property.getName());
        if (property.isAnnotationPresent(Id.class)) {
            LOG.debugf("Skipping AttributeConverter checks for Id attribute [%s]", property.getName());
            return;
        }
        if (this.isVersion) {
            LOG.debugf("Skipping AttributeConverter checks for version attribute [%s]", property.getName());
            return;
        }
        if (!this.key && property.isAnnotationPresent(Temporal.class)) {
            LOG.debugf("Skipping AttributeConverter checks for Temporal attribute [%s]", property.getName());
            return;
        }
        if (this.key && property.isAnnotationPresent(MapKeyTemporal.class)) {
            LOG.debugf("Skipping AttributeConverter checks for map-key annotated as MapKeyTemporal [%s]", property.getName());
            return;
        }
        if (!this.key && property.isAnnotationPresent(Enumerated.class)) {
            LOG.debugf("Skipping AttributeConverter checks for Enumerated attribute [%s]", property.getName());
            return;
        }
        if (this.key && property.isAnnotationPresent(MapKeyEnumerated.class)) {
            LOG.debugf("Skipping AttributeConverter checks for map-key annotated as MapKeyEnumerated [%s]", property.getName());
            return;
        }
        if (this.isAssociation()) {
            LOG.debugf("Skipping AttributeConverter checks for association attribute [%s]", property.getName());
            return;
        }
        this.attributeConverterDescriptor = attributeConverterDescriptor;
    }

    private boolean isAssociation() {
        return this.referencedEntityName != null;
    }

    private TemporalType getTemporalType(XProperty property) {
        if (this.key) {
            MapKeyTemporal ann = (MapKeyTemporal)property.getAnnotation(MapKeyTemporal.class);
            return ann.value();
        }
        Temporal ann = (Temporal)property.getAnnotation(Temporal.class);
        return ann.value();
    }

    public void setExplicitType(String explicitType) {
        this.explicitType = explicitType;
    }

    public void setExplicitType(Type typeAnn) {
        if (typeAnn != null) {
            this.explicitType = typeAnn.type();
            this.typeParameters.clear();
            for (Parameter param : typeAnn.parameters()) {
                this.typeParameters.setProperty(param.name(), param.value());
            }
        }
    }

    public void setBuildingContext(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
    }

    private void validate() {
        Ejb3Column.checkPropertyConsistency(this.columns, this.propertyName);
    }

    public SimpleValue make() {
        this.validate();
        LOG.debugf("building SimpleValue for %s", this.propertyName);
        if (this.table == null) {
            this.table = this.columns[0].getTable();
        }
        this.simpleValue = new SimpleValue(this.buildingContext, this.table);
        if (this.isVersion) {
            this.simpleValue.makeVersion();
        }
        if (this.isNationalized) {
            this.simpleValue.makeNationalized();
        }
        if (this.isLob) {
            this.simpleValue.makeLob();
        }
        this.linkWithValue();
        boolean isInSecondPass = this.buildingContext.getMetadataCollector().isInSecondPass();
        if (!isInSecondPass) {
            this.buildingContext.getMetadataCollector().addSecondPass(new SetSimpleValueTypeSecondPass(this));
        } else {
            this.fillSimpleValue();
        }
        return this.simpleValue;
    }

    public void linkWithValue() {
        if (this.columns[0].isNameDeferred() && !this.buildingContext.getMetadataCollector().isInSecondPass() && this.referencedEntityName != null) {
            this.buildingContext.getMetadataCollector().addSecondPass(new PkDrivenByDefaultMapsIdSecondPass(this.referencedEntityName, (Ejb3JoinColumn[])this.columns, this.simpleValue));
        } else {
            for (Ejb3Column column : this.columns) {
                column.linkWithValue(this.simpleValue);
            }
        }
    }

    public void fillSimpleValue() {
        Class typeClass;
        LOG.debugf("Starting fillSimpleValue for %s", this.propertyName);
        if (this.attributeConverterDescriptor != null) {
            if (!BinderHelper.isEmptyAnnotationValue(this.explicitType)) {
                throw new AnnotationException(String.format("AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];remove @Type or specify @Convert(disableConversion = true)", this.persistentClassName, this.propertyName));
            }
            LOG.debugf("Applying JPA AttributeConverter [%s] to [%s:%s]", this.attributeConverterDescriptor, this.persistentClassName, this.propertyName);
            this.simpleValue.setJpaAttributeConverterDescriptor(this.attributeConverterDescriptor);
        } else {
            TypeDefinition typeDef;
            String type;
            if (!BinderHelper.isEmptyAnnotationValue(this.explicitType)) {
                type = this.explicitType;
                typeDef = this.buildingContext.getMetadataCollector().getTypeDefinition(type);
            } else {
                TypeDefinition implicitTypeDef = this.buildingContext.getMetadataCollector().getTypeDefinition(this.returnedClassName);
                if (implicitTypeDef != null) {
                    typeDef = implicitTypeDef;
                    type = this.returnedClassName;
                } else {
                    typeDef = this.buildingContext.getMetadataCollector().getTypeDefinition(this.defaultType);
                    type = this.defaultType;
                }
            }
            if (typeDef != null) {
                type = typeDef.getTypeImplementorClass().getName();
                this.simpleValue.setTypeParameters(typeDef.getParametersAsProperties());
            }
            if (this.typeParameters != null && this.typeParameters.size() != 0) {
                this.simpleValue.setTypeParameters(this.typeParameters);
            }
            this.simpleValue.setTypeName(type);
        }
        if (this.persistentClassName != null || this.attributeConverterDescriptor != null) {
            try {
                this.simpleValue.setTypeUsingReflection(this.persistentClassName, this.propertyName);
            }
            catch (Exception e) {
                throw new MappingException(String.format(Locale.ROOT, "Unable to determine basic type mapping via reflection [%s -> %s]", this.persistentClassName, this.propertyName), e);
            }
        }
        if (!this.simpleValue.isTypeSpecified() && this.isVersion()) {
            this.simpleValue.setTypeName("integer");
        }
        if (this.timeStampVersionType != null) {
            this.simpleValue.setTypeName(this.timeStampVersionType);
        }
        if (this.simpleValue.getTypeName() != null && this.simpleValue.getTypeName().length() > 0 && this.simpleValue.getMetadata().getTypeResolver().basic(this.simpleValue.getTypeName()) == null && (typeClass = this.buildingContext.getBootstrapContext().getClassLoaderAccess().classForName(this.simpleValue.getTypeName())) != null && DynamicParameterizedType.class.isAssignableFrom(typeClass)) {
            Properties parameters = this.simpleValue.getTypeParameters();
            if (parameters == null) {
                parameters = new Properties();
            }
            parameters.put("org.hibernate.type.ParameterType.dynamic", Boolean.toString(true));
            parameters.put("org.hibernate.type.ParameterType.returnedClass", this.returnedClassName);
            parameters.put("org.hibernate.type.ParameterType.primaryKey", Boolean.toString(this.key));
            parameters.put("org.hibernate.type.ParameterType.entityClass", this.persistentClassName);
            parameters.put("org.hibernate.type.ParameterType.xproperty", this.xproperty);
            parameters.put("org.hibernate.type.ParameterType.propertyName", this.xproperty.getName());
            parameters.put("org.hibernate.type.ParameterType.accessType", this.accessType.getType());
            this.simpleValue.setTypeParameters(parameters);
        }
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public AccessType getAccessType() {
        return this.accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }
}

