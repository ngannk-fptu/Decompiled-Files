/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.MapKeyEnumerated
 *  org.jboss.logging.Logger
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import javax.persistence.Enumerated;
import javax.persistence.MapKeyEnumerated;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.metamodel.model.convert.internal.NamedEnumValueConverter;
import org.hibernate.metamodel.model.convert.internal.OrdinalEnumValueConverter;
import org.hibernate.metamodel.model.convert.spi.EnumValueConverter;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.LoggableUserType;
import org.jboss.logging.Logger;

public class EnumType<T extends Enum>
implements EnhancedUserType,
DynamicParameterizedType,
LoggableUserType,
TypeConfigurationAware,
Serializable {
    private static final Logger LOG = CoreLogging.logger(EnumType.class);
    public static final String ENUM = "enumClass";
    public static final String NAMED = "useNamed";
    public static final String TYPE = "type";
    private Class enumClass;
    private EnumValueConverter enumValueConverter;
    private TypeConfiguration typeConfiguration;

    @Override
    public void setParameterValues(Properties parameters) {
        DynamicParameterizedType.ParameterType reader = (DynamicParameterizedType.ParameterType)parameters.get("org.hibernate.type.ParameterType");
        if (reader != null) {
            boolean isOrdinal;
            this.enumClass = reader.getReturnedClass().asSubclass(Enum.class);
            javax.persistence.EnumType enumType = this.getEnumType(reader);
            if (enumType == null) {
                isOrdinal = true;
            } else if (javax.persistence.EnumType.ORDINAL.equals((Object)enumType)) {
                isOrdinal = true;
            } else if (javax.persistence.EnumType.STRING.equals((Object)enumType)) {
                isOrdinal = false;
            } else {
                throw new AssertionFailure("Unknown EnumType: " + enumType);
            }
            EnumJavaTypeDescriptor enumJavaDescriptor = (EnumJavaTypeDescriptor)this.typeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor(this.enumClass);
            this.enumValueConverter = isOrdinal ? new OrdinalEnumValueConverter(enumJavaDescriptor) : new NamedEnumValueConverter(enumJavaDescriptor);
        } else {
            String enumClassName = (String)parameters.get(ENUM);
            try {
                this.enumClass = ReflectHelper.classForName(enumClassName, this.getClass()).asSubclass(Enum.class);
            }
            catch (ClassNotFoundException exception) {
                throw new HibernateException("Enum class not found: " + enumClassName, exception);
            }
            this.enumValueConverter = this.interpretParameters(parameters);
        }
        LOG.debugf("Using %s-based conversion for Enum %s", (Object)(this.isOrdinal() ? "ORDINAL" : "NAMED"), (Object)this.enumClass.getName());
    }

    private javax.persistence.EnumType getEnumType(DynamicParameterizedType.ParameterType reader) {
        javax.persistence.EnumType enumType = null;
        if (reader.isPrimaryKey()) {
            MapKeyEnumerated enumAnn = this.getAnnotation(reader.getAnnotationsMethod(), MapKeyEnumerated.class);
            if (enumAnn != null) {
                enumType = enumAnn.value();
            }
        } else {
            Enumerated enumAnn = this.getAnnotation(reader.getAnnotationsMethod(), Enumerated.class);
            if (enumAnn != null) {
                enumType = enumAnn.value();
            }
        }
        return enumType;
    }

    private <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> anClass) {
        for (Annotation annotation : annotations) {
            if (!anClass.isInstance(annotation)) continue;
            return (A)annotation;
        }
        return null;
    }

    private EnumValueConverter interpretParameters(Properties parameters) {
        EnumJavaTypeDescriptor javaTypeDescriptor = (EnumJavaTypeDescriptor)this.typeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor(this.enumClass);
        if (parameters.containsKey(NAMED)) {
            boolean useNamed = ConfigurationHelper.getBoolean(NAMED, parameters);
            if (useNamed) {
                return new NamedEnumValueConverter(javaTypeDescriptor);
            }
            return new OrdinalEnumValueConverter(javaTypeDescriptor);
        }
        if (parameters.containsKey(TYPE)) {
            int type = Integer.decode((String)parameters.get(TYPE));
            if (this.isNumericType(type)) {
                return new OrdinalEnumValueConverter(javaTypeDescriptor);
            }
            if (this.isCharacterType(type)) {
                return new NamedEnumValueConverter(javaTypeDescriptor);
            }
            throw new HibernateException(String.format(Locale.ENGLISH, "Passed JDBC type code [%s] not recognized as numeric nor character", type));
        }
        return new OrdinalEnumValueConverter(javaTypeDescriptor);
    }

    private boolean isCharacterType(int jdbcTypeCode) {
        switch (jdbcTypeCode) {
            case -1: 
            case 1: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    private boolean isNumericType(int jdbcTypeCode) {
        switch (jdbcTypeCode) {
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] sqlTypes() {
        this.verifyConfigured();
        return new int[]{this.enumValueConverter.getJdbcTypeCode()};
    }

    @Override
    public Class<? extends Enum> returnedClass() {
        return this.enumClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        this.verifyConfigured();
        return this.enumValueConverter.readValue(rs, names[0], session);
    }

    private void verifyConfigured() {
        if (this.enumValueConverter == null) {
            throw new AssertionFailure("EnumType (" + this.enumClass.getName() + ") not properly, fully configured");
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.verifyConfigured();
        this.enumValueConverter.writeValue(st, (Enum)value, index, session);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.typeConfiguration;
    }

    @Override
    public void setTypeConfiguration(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = typeConfiguration;
    }

    @Override
    public String objectToSQLString(Object value) {
        this.verifyConfigured();
        return this.enumValueConverter.toSqlLiteral(value);
    }

    @Override
    public String toXMLString(Object value) {
        this.verifyConfigured();
        return this.enumValueConverter.getJavaDescriptor().unwrap((Enum)value, String.class, null);
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        this.verifyConfigured();
        return this.enumValueConverter.getJavaDescriptor().wrap(xmlValue, (WrapperOptions)null);
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        this.verifyConfigured();
        return this.enumValueConverter.getJavaDescriptor().toString((Enum)value);
    }

    public boolean isOrdinal() {
        this.verifyConfigured();
        return this.enumValueConverter instanceof OrdinalEnumValueConverter;
    }
}

