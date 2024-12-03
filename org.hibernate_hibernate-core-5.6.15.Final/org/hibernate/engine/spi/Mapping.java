/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.MappingException;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.type.Type;

public interface Mapping {
    @Deprecated
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory();

    public Type getIdentifierType(String var1) throws MappingException;

    public String getIdentifierPropertyName(String var1) throws MappingException;

    public Type getReferencedPropertyType(String var1, String var2) throws MappingException;
}

