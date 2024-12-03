/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Value;

public interface KeyValue
extends Value {
    @Deprecated
    public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory var1, Dialect var2, String var3, String var4, RootClass var5) throws MappingException;

    public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory var1, Dialect var2, RootClass var3) throws MappingException;

    public boolean isIdentityColumn(IdentifierGeneratorFactory var1, Dialect var2);

    public void createForeignKeyOfEntity(String var1);

    public boolean isCascadeDeleteEnabled();

    public String getNullValue();

    public boolean isUpdateable();
}

