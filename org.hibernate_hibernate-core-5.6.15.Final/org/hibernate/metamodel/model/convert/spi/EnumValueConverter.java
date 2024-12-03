/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.convert.spi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.model.convert.spi.BasicValueConverter;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;

public interface EnumValueConverter<O extends Enum, R>
extends BasicValueConverter<O, R> {
    public EnumJavaTypeDescriptor<O> getJavaDescriptor();

    public int getJdbcTypeCode();

    public O readValue(ResultSet var1, String var2, SharedSessionContractImplementor var3) throws SQLException;

    public void writeValue(PreparedStatement var1, O var2, int var3, SharedSessionContractImplementor var4) throws SQLException;

    public String toSqlLiteral(Object var1);
}

