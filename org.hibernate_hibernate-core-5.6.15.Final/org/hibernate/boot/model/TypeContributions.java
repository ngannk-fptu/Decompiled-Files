/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public interface TypeContributions {
    public TypeConfiguration getTypeConfiguration();

    public void contributeJavaTypeDescriptor(JavaTypeDescriptor var1);

    public void contributeSqlTypeDescriptor(SqlTypeDescriptor var1);

    public void contributeType(BasicType var1);

    @Deprecated
    public void contributeType(BasicType var1, String ... var2);

    @Deprecated
    public void contributeType(UserType var1, String ... var2);

    @Deprecated
    public void contributeType(CompositeUserType var1, String ... var2);
}

