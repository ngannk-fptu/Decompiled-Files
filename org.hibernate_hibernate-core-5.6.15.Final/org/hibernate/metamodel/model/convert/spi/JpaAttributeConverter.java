/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.metamodel.model.convert.spi;

import javax.persistence.AttributeConverter;
import org.hibernate.metamodel.model.convert.spi.BasicValueConverter;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

public interface JpaAttributeConverter<O, R>
extends BasicValueConverter<O, R> {
    public JavaTypeDescriptor<AttributeConverter<O, R>> getConverterJavaTypeDescriptor();

    public ManagedBean<AttributeConverter<O, R>> getConverterBean();

    public BasicJavaDescriptor<O> getDomainJavaTypeDescriptor();

    public BasicJavaDescriptor<R> getRelationalJavaTypeDescriptor();
}

