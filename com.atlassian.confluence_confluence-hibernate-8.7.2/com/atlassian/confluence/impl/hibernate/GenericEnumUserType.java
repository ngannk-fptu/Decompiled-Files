/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.type.BasicTypeRegistry
 *  org.hibernate.type.SingleColumnType
 *  org.hibernate.usertype.EnhancedUserType
 *  org.hibernate.usertype.ParameterizedType
 */
package com.atlassian.confluence.impl.hibernate;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.SingleColumnType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

public class GenericEnumUserType
implements EnhancedUserType,
ParameterizedType,
Serializable {
    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "getId";
    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "fromId";
    private static final Class[] NULL_CLASS_VARARG = null;
    private static final Object[] NULL_OBJECT_VARARG = null;
    private static final char SINGLE_QUOTE = '\'';
    private static final long serialVersionUID = 75631542462332793L;
    private transient Class<? extends Enum> enumClass;
    private transient Method identifierMethod;
    private transient int[] sqlTypes;
    private transient SingleColumnType<Object> type;
    private transient Method valueOfMethod;

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(this.enumClass, xmlValue);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object identifier = this.type.nullSafeGet(rs, names[0], session);
        if (identifier == null || rs.wasNull()) {
            return null;
        }
        try {
            return this.valueOfMethod.invoke(this.enumClass, identifier);
        }
        catch (Exception exception) {
            String msg = "Exception while invoking valueOfMethod [" + this.valueOfMethod.getName() + "] of Enum class [" + this.enumClass.getName() + "] with argument of type [" + identifier.getClass().getName() + "], value=[" + identifier + "]";
            throw new HibernateException(msg, (Throwable)exception);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, this.sqlTypes[0]);
        } else {
            try {
                Object identifier = this.identifierMethod.invoke(value, NULL_OBJECT_VARARG);
                this.type.set(st, identifier, index, session);
            }
            catch (Exception exception) {
                String msg = "Exception while invoking identifierMethod [" + this.identifierMethod.getName() + "] of Enum class [" + this.enumClass.getName() + "] with argument of type [" + value.getClass().getName() + "], value=[" + value + "]";
                throw new HibernateException(msg, (Throwable)exception);
            }
        }
    }

    public String objectToSQLString(Object value) {
        return "'" + ((Enum)value).name() + "'";
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return this.enumClass;
    }

    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        String identifierMethodName = parameters.getProperty("identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);
        String valueOfMethodName = parameters.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);
        this.initialize(enumClassName, identifierMethodName, valueOfMethodName);
    }

    public int[] sqlTypes() {
        return this.sqlTypes;
    }

    public String toXMLString(Object value) {
        return ((Enum)value).name();
    }

    private void initialize(String enumClassName, String identifierMethodName, String valueOfMethodName) {
        try {
            this.enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        }
        catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum class not found", (Throwable)exception);
        }
        try {
            this.identifierMethod = this.enumClass.getMethod(identifierMethodName, NULL_CLASS_VARARG);
        }
        catch (Exception exception) {
            throw new HibernateException("Failed to obtain identifier method", (Throwable)exception);
        }
        Class<?> identifierType = this.identifierMethod.getReturnType();
        try {
            this.valueOfMethod = this.enumClass.getMethod(valueOfMethodName, identifierType);
        }
        catch (Exception exception) {
            throw new HibernateException("Failed to obtain valueOf method", (Throwable)exception);
        }
        BasicTypeRegistry registry = new BasicTypeRegistry();
        this.type = (SingleColumnType)registry.getRegisteredType(identifierType.getName());
        if (this.type == null) {
            throw new HibernateException("Unsupported identifier type " + identifierType.getName());
        }
        this.sqlTypes = new int[]{this.type.sqlType()};
    }

    private void readObject(ObjectInputStream stream) {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot be deserialized directly");
    }

    private Object writeReplace() {
        return new SerializationProxy(this.enumClass.getName(), this.identifierMethod.getName(), this.valueOfMethod.getName());
    }

    private static class SerializationProxy
    implements Serializable {
        private final String enumClassName;
        private final String identifierMethodName;
        private final String valueOfMethodName;

        private SerializationProxy(String enumClassName, String identifierMethodName, String valueOfMethodName) {
            this.enumClassName = enumClassName;
            this.identifierMethodName = identifierMethodName;
            this.valueOfMethodName = valueOfMethodName;
        }

        private Object readResolve() {
            GenericEnumUserType type = new GenericEnumUserType();
            type.initialize(this.enumClassName, this.identifierMethodName, this.valueOfMethodName);
            return type;
        }
    }
}

