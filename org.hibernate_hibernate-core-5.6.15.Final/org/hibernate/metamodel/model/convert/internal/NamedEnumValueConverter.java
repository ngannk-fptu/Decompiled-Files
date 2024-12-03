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
import java.util.Locale;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.model.convert.spi.EnumValueConverter;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class NamedEnumValueConverter<E extends Enum>
implements EnumValueConverter<E, String>,
Serializable {
    private final EnumJavaTypeDescriptor<E> enumJavaDescriptor;
    private transient ValueExtractor<E> valueExtractor;
    private transient ValueBinder<String> valueBinder;

    public NamedEnumValueConverter(EnumJavaTypeDescriptor<E> enumJavaDescriptor) {
        this.enumJavaDescriptor = enumJavaDescriptor;
        this.valueExtractor = NamedEnumValueConverter.createValueExtractor(enumJavaDescriptor);
        this.valueBinder = NamedEnumValueConverter.createValueBinder();
    }

    @Override
    public E toDomainValue(String relationalForm) {
        return this.enumJavaDescriptor.fromName(relationalForm);
    }

    @Override
    public String toRelationalValue(E domainForm) {
        return this.enumJavaDescriptor.toName(domainForm);
    }

    @Override
    public int getJdbcTypeCode() {
        return 12;
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
        String jdbcValue = value == null ? null : this.toRelationalValue(value);
        this.valueBinder.bind(statement, jdbcValue, position, (WrapperOptions)session);
    }

    @Override
    public String toSqlLiteral(Object value) {
        return String.format(Locale.ROOT, "'%s'", ((Enum)value).name());
    }

    private static <T extends Enum> ValueExtractor<T> createValueExtractor(EnumJavaTypeDescriptor<T> enumJavaDescriptor) {
        return VarcharTypeDescriptor.INSTANCE.getExtractor(enumJavaDescriptor);
    }

    private static ValueBinder<String> createValueBinder() {
        return VarcharTypeDescriptor.INSTANCE.getBinder(StringTypeDescriptor.INSTANCE);
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.valueExtractor = NamedEnumValueConverter.createValueExtractor(this.enumJavaDescriptor);
        this.valueBinder = NamedEnumValueConverter.createValueBinder();
    }
}

