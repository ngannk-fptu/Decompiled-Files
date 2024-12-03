/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.UUID;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class UUIDCharType
extends AbstractSingleColumnStandardBasicType<UUID>
implements LiteralType<UUID> {
    public static final UUIDCharType INSTANCE = new UUIDCharType();

    public UUIDCharType() {
        super(VarcharTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "uuid-char";
    }

    @Override
    public String objectToSQLString(UUID value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(value.toString(), dialect);
    }
}

