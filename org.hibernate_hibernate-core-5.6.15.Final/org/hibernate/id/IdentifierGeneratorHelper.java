/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.ResultSetIdentifierConsumer;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;

public final class IdentifierGeneratorHelper {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(IdentifierGeneratorHelper.class);
    public static final Serializable SHORT_CIRCUIT_INDICATOR = new Serializable(){

        public String toString() {
            return "SHORT_CIRCUIT_INDICATOR";
        }
    };
    public static final Serializable POST_INSERT_INDICATOR = new Serializable(){

        public String toString() {
            return "POST_INSERT_INDICATOR";
        }
    };

    public static Serializable getGeneratedIdentity(ResultSet rs, String identifier, Type type, Dialect dialect) throws SQLException, HibernateException {
        if (!rs.next()) {
            throw new HibernateException("The database returned no natively generated identity value");
        }
        Serializable id = IdentifierGeneratorHelper.get(rs, identifier, type, dialect);
        LOG.debugf("Natively generated identity: %s", id);
        return id;
    }

    public static Serializable get(ResultSet rs, String identifier, Type type, Dialect dialect) throws SQLException, IdentifierGenerationException {
        CustomType customType;
        if (ResultSetIdentifierConsumer.class.isInstance(type)) {
            return ((ResultSetIdentifierConsumer)((Object)type)).consumeIdentifier(rs);
        }
        if (CustomType.class.isInstance(type) && ResultSetIdentifierConsumer.class.isInstance((customType = (CustomType)type).getUserType())) {
            return ((ResultSetIdentifierConsumer)((Object)customType.getUserType())).consumeIdentifier(rs);
        }
        ResultSetMetaData resultSetMetaData = null;
        int columnCount = 1;
        try {
            resultSetMetaData = rs.getMetaData();
            columnCount = resultSetMetaData.getColumnCount();
        }
        catch (Exception exception) {
            // empty catch block
        }
        Class clazz = type.getReturnedClass();
        if (columnCount == 1) {
            if (clazz == Long.class) {
                return Long.valueOf(rs.getLong(1));
            }
            if (clazz == Integer.class) {
                return Integer.valueOf(rs.getInt(1));
            }
            if (clazz == Short.class) {
                return Short.valueOf(rs.getShort(1));
            }
            if (clazz == String.class) {
                return rs.getString(1);
            }
            if (clazz == BigInteger.class) {
                return rs.getBigDecimal(1).setScale(0, 7).toBigInteger();
            }
            if (clazz == BigDecimal.class) {
                return rs.getBigDecimal(1).setScale(0, 7);
            }
            throw new IdentifierGenerationException("unrecognized id type : " + type.getName() + " -> " + clazz.getName());
        }
        try {
            return IdentifierGeneratorHelper.extractIdentifier(rs, identifier, type, clazz);
        }
        catch (SQLException e) {
            if (StringHelper.isQuoted(identifier, dialect)) {
                return IdentifierGeneratorHelper.extractIdentifier(rs, StringHelper.unquote(identifier, dialect), type, clazz);
            }
            throw e;
        }
    }

    private static Serializable extractIdentifier(ResultSet rs, String identifier, Type type, Class clazz) throws SQLException {
        if (clazz == Long.class) {
            return Long.valueOf(rs.getLong(identifier));
        }
        if (clazz == Integer.class) {
            return Integer.valueOf(rs.getInt(identifier));
        }
        if (clazz == Short.class) {
            return Short.valueOf(rs.getShort(identifier));
        }
        if (clazz == String.class) {
            return rs.getString(identifier);
        }
        if (clazz == BigInteger.class) {
            return rs.getBigDecimal(identifier).setScale(0, 7).toBigInteger();
        }
        if (clazz == BigDecimal.class) {
            return rs.getBigDecimal(identifier).setScale(0, 7);
        }
        throw new IdentifierGenerationException("unrecognized id type : " + type.getName() + " -> " + clazz.getName());
    }

    @Deprecated
    public static Number createNumber(long value, Class clazz) throws IdentifierGenerationException {
        if (clazz == Long.class) {
            return value;
        }
        if (clazz == Integer.class) {
            return (int)value;
        }
        if (clazz == Short.class) {
            return (short)value;
        }
        throw new IdentifierGenerationException("unrecognized id type : " + clazz.getName());
    }

    public static IntegralDataTypeHolder getIntegralDataTypeHolder(Class integralType) {
        if (integralType == Long.class || integralType == Integer.class || integralType == Short.class) {
            return new BasicHolder(integralType);
        }
        if (integralType == BigInteger.class) {
            return new BigIntegerHolder();
        }
        if (integralType == BigDecimal.class) {
            return new BigDecimalHolder();
        }
        throw new IdentifierGenerationException("Unknown integral data type for ids : " + integralType.getName());
    }

    public static long extractLong(IntegralDataTypeHolder holder) {
        if (holder.getClass() == BasicHolder.class) {
            ((BasicHolder)holder).checkInitialized();
            return ((BasicHolder)holder).value;
        }
        if (holder.getClass() == BigIntegerHolder.class) {
            ((BigIntegerHolder)holder).checkInitialized();
            return ((BigIntegerHolder)holder).value.longValue();
        }
        if (holder.getClass() == BigDecimalHolder.class) {
            ((BigDecimalHolder)holder).checkInitialized();
            return ((BigDecimalHolder)holder).value.longValue();
        }
        throw new IdentifierGenerationException("Unknown IntegralDataTypeHolder impl [" + holder + "]");
    }

    public static BigInteger extractBigInteger(IntegralDataTypeHolder holder) {
        if (holder.getClass() == BasicHolder.class) {
            ((BasicHolder)holder).checkInitialized();
            return BigInteger.valueOf(((BasicHolder)holder).value);
        }
        if (holder.getClass() == BigIntegerHolder.class) {
            ((BigIntegerHolder)holder).checkInitialized();
            return ((BigIntegerHolder)holder).value;
        }
        if (holder.getClass() == BigDecimalHolder.class) {
            ((BigDecimalHolder)holder).checkInitialized();
            return ((BigDecimalHolder)holder).value.toBigInteger();
        }
        throw new IdentifierGenerationException("Unknown IntegralDataTypeHolder impl [" + holder + "]");
    }

    public static BigDecimal extractBigDecimal(IntegralDataTypeHolder holder) {
        if (holder.getClass() == BasicHolder.class) {
            ((BasicHolder)holder).checkInitialized();
            return BigDecimal.valueOf(((BasicHolder)holder).value);
        }
        if (holder.getClass() == BigIntegerHolder.class) {
            ((BigIntegerHolder)holder).checkInitialized();
            return new BigDecimal(((BigIntegerHolder)holder).value);
        }
        if (holder.getClass() == BigDecimalHolder.class) {
            ((BigDecimalHolder)holder).checkInitialized();
            return ((BigDecimalHolder)holder).value;
        }
        throw new IdentifierGenerationException("Unknown IntegralDataTypeHolder impl [" + holder + "]");
    }

    private IdentifierGeneratorHelper() {
    }

    public static class BigDecimalHolder
    implements IntegralDataTypeHolder {
        private BigDecimal value;

        @Override
        public IntegralDataTypeHolder initialize(long value) {
            this.value = BigDecimal.valueOf(value);
            return this;
        }

        @Override
        public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
            BigDecimal rsValue = resultSet.getBigDecimal(1);
            if (resultSet.wasNull()) {
                return this.initialize(defaultValue);
            }
            this.value = rsValue.setScale(0, 7);
            return this;
        }

        @Override
        public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
            preparedStatement.setBigDecimal(position, this.value);
        }

        @Override
        public IntegralDataTypeHolder increment() {
            this.checkInitialized();
            this.value = this.value.add(BigDecimal.ONE);
            return this;
        }

        private void checkInitialized() {
            if (this.value == null) {
                throw new IdentifierGenerationException("integral holder was not initialized");
            }
        }

        @Override
        public IntegralDataTypeHolder add(long increment) {
            this.checkInitialized();
            this.value = this.value.add(BigDecimal.valueOf(increment));
            return this;
        }

        @Override
        public IntegralDataTypeHolder decrement() {
            this.checkInitialized();
            this.value = this.value.subtract(BigDecimal.ONE);
            return this;
        }

        @Override
        public IntegralDataTypeHolder subtract(long subtrahend) {
            this.checkInitialized();
            this.value = this.value.subtract(BigDecimal.valueOf(subtrahend));
            return this;
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
            this.checkInitialized();
            this.value = this.value.multiply(IdentifierGeneratorHelper.extractBigDecimal(factor));
            return this;
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(long factor) {
            this.checkInitialized();
            this.value = this.value.multiply(BigDecimal.valueOf(factor));
            return this;
        }

        @Override
        public boolean eq(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigDecimal(other)) == 0;
        }

        @Override
        public boolean eq(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigDecimal.valueOf(value)) == 0;
        }

        @Override
        public boolean lt(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigDecimal(other)) < 0;
        }

        @Override
        public boolean lt(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigDecimal.valueOf(value)) < 0;
        }

        @Override
        public boolean gt(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigDecimal(other)) > 0;
        }

        @Override
        public boolean gt(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigDecimal.valueOf(value)) > 0;
        }

        @Override
        public IntegralDataTypeHolder copy() {
            BigDecimalHolder copy = new BigDecimalHolder();
            copy.value = this.value;
            return copy;
        }

        @Override
        public Number makeValue() {
            this.checkInitialized();
            return this.value;
        }

        @Override
        public Number makeValueThenIncrement() {
            Number result = this.makeValue();
            this.value = this.value.add(BigDecimal.ONE);
            return result;
        }

        @Override
        public Number makeValueThenAdd(long addend) {
            Number result = this.makeValue();
            this.value = this.value.add(BigDecimal.valueOf(addend));
            return result;
        }

        public String toString() {
            return "BigDecimalHolder[" + this.value + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BigDecimalHolder that = (BigDecimalHolder)o;
            return this.value == null ? that.value == null : this.value.equals(that.value);
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    public static class BigIntegerHolder
    implements IntegralDataTypeHolder {
        private BigInteger value;

        @Override
        public IntegralDataTypeHolder initialize(long value) {
            this.value = BigInteger.valueOf(value);
            return this;
        }

        @Override
        public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
            BigDecimal rsValue = resultSet.getBigDecimal(1);
            if (resultSet.wasNull()) {
                return this.initialize(defaultValue);
            }
            this.value = rsValue.setScale(0, 7).toBigInteger();
            return this;
        }

        @Override
        public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
            preparedStatement.setBigDecimal(position, new BigDecimal(this.value));
        }

        @Override
        public IntegralDataTypeHolder increment() {
            this.checkInitialized();
            this.value = this.value.add(BigInteger.ONE);
            return this;
        }

        private void checkInitialized() {
            if (this.value == null) {
                throw new IdentifierGenerationException("integral holder was not initialized");
            }
        }

        @Override
        public IntegralDataTypeHolder add(long increment) {
            this.checkInitialized();
            this.value = this.value.add(BigInteger.valueOf(increment));
            return this;
        }

        @Override
        public IntegralDataTypeHolder decrement() {
            this.checkInitialized();
            this.value = this.value.subtract(BigInteger.ONE);
            return this;
        }

        @Override
        public IntegralDataTypeHolder subtract(long subtrahend) {
            this.checkInitialized();
            this.value = this.value.subtract(BigInteger.valueOf(subtrahend));
            return this;
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
            this.checkInitialized();
            this.value = this.value.multiply(IdentifierGeneratorHelper.extractBigInteger(factor));
            return this;
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(long factor) {
            this.checkInitialized();
            this.value = this.value.multiply(BigInteger.valueOf(factor));
            return this;
        }

        @Override
        public boolean eq(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigInteger(other)) == 0;
        }

        @Override
        public boolean eq(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigInteger.valueOf(value)) == 0;
        }

        @Override
        public boolean lt(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigInteger(other)) < 0;
        }

        @Override
        public boolean lt(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigInteger.valueOf(value)) < 0;
        }

        @Override
        public boolean gt(IntegralDataTypeHolder other) {
            this.checkInitialized();
            return this.value.compareTo(IdentifierGeneratorHelper.extractBigInteger(other)) > 0;
        }

        @Override
        public boolean gt(long value) {
            this.checkInitialized();
            return this.value.compareTo(BigInteger.valueOf(value)) > 0;
        }

        @Override
        public IntegralDataTypeHolder copy() {
            BigIntegerHolder copy = new BigIntegerHolder();
            copy.value = this.value;
            return copy;
        }

        @Override
        public Number makeValue() {
            this.checkInitialized();
            return this.value;
        }

        @Override
        public Number makeValueThenIncrement() {
            Number result = this.makeValue();
            this.value = this.value.add(BigInteger.ONE);
            return result;
        }

        @Override
        public Number makeValueThenAdd(long addend) {
            Number result = this.makeValue();
            this.value = this.value.add(BigInteger.valueOf(addend));
            return result;
        }

        public String toString() {
            return "BigIntegerHolder[" + this.value + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BigIntegerHolder that = (BigIntegerHolder)o;
            return this.value == null ? that.value == null : this.value.equals(that.value);
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    public static class BasicHolder
    implements IntegralDataTypeHolder {
        private final Class exactType;
        private long value = Long.MIN_VALUE;

        public BasicHolder(Class exactType) {
            this.exactType = exactType;
            if (exactType != Long.class && exactType != Integer.class && exactType != Short.class) {
                throw new IdentifierGenerationException("Invalid type for basic integral holder : " + exactType);
            }
        }

        public long getActualLongValue() {
            return this.value;
        }

        @Override
        public IntegralDataTypeHolder initialize(long value) {
            this.value = value;
            return this;
        }

        @Override
        public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
            long value = resultSet.getLong(1);
            if (resultSet.wasNull()) {
                value = defaultValue;
            }
            return this.initialize(value);
        }

        @Override
        public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
            LOG.tracef("binding parameter [%s] - [%s]", position, this.value);
            preparedStatement.setLong(position, this.value);
        }

        @Override
        public IntegralDataTypeHolder increment() {
            this.checkInitialized();
            ++this.value;
            return this;
        }

        private void checkInitialized() {
            if (this.value == Long.MIN_VALUE) {
                throw new IdentifierGenerationException("integral holder was not initialized");
            }
        }

        @Override
        public IntegralDataTypeHolder add(long addend) {
            this.checkInitialized();
            this.value += addend;
            return this;
        }

        @Override
        public IntegralDataTypeHolder decrement() {
            this.checkInitialized();
            --this.value;
            return this;
        }

        @Override
        public IntegralDataTypeHolder subtract(long subtrahend) {
            this.checkInitialized();
            this.value -= subtrahend;
            return this;
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
            return this.multiplyBy(IdentifierGeneratorHelper.extractLong(factor));
        }

        @Override
        public IntegralDataTypeHolder multiplyBy(long factor) {
            this.checkInitialized();
            this.value *= factor;
            return this;
        }

        @Override
        public boolean eq(IntegralDataTypeHolder other) {
            return this.eq(IdentifierGeneratorHelper.extractLong(other));
        }

        @Override
        public boolean eq(long value) {
            this.checkInitialized();
            return this.value == value;
        }

        @Override
        public boolean lt(IntegralDataTypeHolder other) {
            return this.lt(IdentifierGeneratorHelper.extractLong(other));
        }

        @Override
        public boolean lt(long value) {
            this.checkInitialized();
            return this.value < value;
        }

        @Override
        public boolean gt(IntegralDataTypeHolder other) {
            return this.gt(IdentifierGeneratorHelper.extractLong(other));
        }

        @Override
        public boolean gt(long value) {
            this.checkInitialized();
            return this.value > value;
        }

        @Override
        public IntegralDataTypeHolder copy() {
            BasicHolder copy = new BasicHolder(this.exactType);
            copy.value = this.value;
            return copy;
        }

        @Override
        public Number makeValue() {
            this.checkInitialized();
            if (this.exactType == Long.class) {
                return this.value;
            }
            if (this.exactType == Integer.class) {
                return (int)this.value;
            }
            return (short)this.value;
        }

        @Override
        public Number makeValueThenIncrement() {
            Number result = this.makeValue();
            ++this.value;
            return result;
        }

        @Override
        public Number makeValueThenAdd(long addend) {
            Number result = this.makeValue();
            this.value += addend;
            return result;
        }

        public String toString() {
            return "BasicHolder[" + this.exactType.getName() + "[" + this.value + "]]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BasicHolder that = (BasicHolder)o;
            return this.value == that.value;
        }

        public int hashCode() {
            return (int)(this.value ^ this.value >>> 32);
        }
    }
}

