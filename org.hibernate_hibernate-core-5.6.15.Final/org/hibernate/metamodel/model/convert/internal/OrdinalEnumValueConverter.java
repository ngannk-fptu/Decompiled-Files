/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.convert.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.model.convert.spi.EnumValueConverter;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
import org.hibernate.type.descriptor.java.IntegerTypeDescriptor;

public class OrdinalEnumValueConverter<E extends Enum>
implements EnumValueConverter<E, Integer>,
Serializable {
    private final EnumJavaTypeDescriptor<E> enumJavaDescriptor;
    private transient ValueExtractor<E> valueExtractor;
    private transient ValueBinder<Integer> valueBinder;

    public OrdinalEnumValueConverter(EnumJavaTypeDescriptor<E> enumJavaDescriptor) {
        this.enumJavaDescriptor = enumJavaDescriptor;
        this.valueExtractor = OrdinalEnumValueConverter.createValueExtractor(enumJavaDescriptor);
        this.valueBinder = OrdinalEnumValueConverter.createValueBinder();
    }

    @Override
    public E toDomainValue(Integer relationalForm) {
        return this.enumJavaDescriptor.fromOrdinal(relationalForm);
    }

    @Override
    public Integer toRelationalValue(E domainForm) {
        return this.enumJavaDescriptor.toOrdinal(domainForm);
    }

    @Override
    public int getJdbcTypeCode() {
        return 4;
    }

    @Override
    public EnumJavaTypeDescriptor<E> getJavaDescriptor() {
        return this.enumJavaDescriptor;
    }

    @Override
    public E readValue(ResultSet resultSet, String name, SharedSessionContractImplementor session) throws SQLException {
        return (E)((Enum)this.valueExtractor.extract(resultSet, name, (WrapperOptions)session));
    }

    @Override
    public void writeValue(PreparedStatement statement, E value, int position, SharedSessionContractImplementor session) throws SQLException {
        Integer jdbcValue = value == null ? null : this.toRelationalValue(value);
        this.valueBinder.bind(statement, jdbcValue, position, (WrapperOptions)session);
    }

    @Override
    public String toSqlLiteral(Object value) {
        return Integer.toString(((Enum)value).ordinal());
    }

    private static <T extends Enum> ValueExtractor<T> createValueExtractor(EnumJavaTypeDescriptor<T> enumJavaDescriptor) {
        return org.hibernate.type.descriptor.sql.IntegerTypeDescriptor.INSTANCE.getExtractor(enumJavaDescriptor);
    }

    private static ValueBinder<Integer> createValueBinder() {
        return org.hibernate.type.descriptor.sql.IntegerTypeDescriptor.INSTANCE.getBinder(IntegerTypeDescriptor.INSTANCE);
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.valueExtractor = OrdinalEnumValueConverter.createValueExtractor(this.enumJavaDescriptor);
        this.valueBinder = OrdinalEnumValueConverter.createValueBinder();
    }
}

