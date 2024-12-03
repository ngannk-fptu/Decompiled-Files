/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.process.spi;

import java.util.Collection;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.jaxb.spi.Binding;

public interface ManagedResources {
    public Collection<AttributeConverterInfo> getAttributeConverterDefinitions();

    public Collection<Class> getAnnotatedClassReferences();

    public Collection<String> getAnnotatedClassNames();

    public Collection<String> getAnnotatedPackageNames();

    public Collection<Binding> getXmlMappingBindings();
}

