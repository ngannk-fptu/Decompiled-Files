/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.factory;

import java.util.Properties;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;

public interface IdentifierGeneratorFactory {
    public Dialect getDialect();

    @Deprecated
    public void setDialect(Dialect var1);

    public IdentifierGenerator createIdentifierGenerator(String var1, Type var2, Properties var3);

    public Class getIdentifierGeneratorClass(String var1);
}

