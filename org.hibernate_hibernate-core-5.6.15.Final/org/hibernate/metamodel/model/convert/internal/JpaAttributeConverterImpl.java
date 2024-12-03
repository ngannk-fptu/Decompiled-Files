/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.metamodel.model.convert.internal;

import javax.persistence.AttributeConverter;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

public class JpaAttributeConverterImpl<O, R>
implements JpaAttributeConverter<O, R> {
    private final ManagedBean<AttributeConverter<O, R>> attributeConverterBean;
    private final JavaTypeDescriptor<AttributeConverter<O, R>> converterJavaTypeDescriptor;
    private final BasicJavaDescriptor<O> domainJavaTypeDescriptor;
    private final BasicJavaDescriptor<R> relationalJavaTypeDescriptor;

    public JpaAttributeConverterImpl(ManagedBean<AttributeConverter<O, R>> attributeConverterBean, JavaTypeDescriptor<AttributeConverter<O, R>> converterJavaTypeDescriptor, JavaTypeDescriptor<O> domainJavaTypeDescriptor, JavaTypeDescriptor<R> relationalJavaTypeDescriptor) {
        this.attributeConverterBean = attributeConverterBean;
        this.converterJavaTypeDescriptor = converterJavaTypeDescriptor;
        this.domainJavaTypeDescriptor = (BasicJavaDescriptor)domainJavaTypeDescriptor;
        this.relationalJavaTypeDescriptor = (BasicJavaDescriptor)relationalJavaTypeDescriptor;
    }

    @Override
    public ManagedBean<AttributeConverter<O, R>> getConverterBean() {
        return this.attributeConverterBean;
    }

    @Override
    public O toDomainValue(R relationalForm) {
        return (O)this.attributeConverterBean.getBeanInstance().convertToEntityAttribute(relationalForm);
    }

    @Override
    public R toRelationalValue(O domainForm) {
        return (R)this.attributeConverterBean.getBeanInstance().convertToDatabaseColumn(domainForm);
    }

    @Override
    public JavaTypeDescriptor<AttributeConverter<O, R>> getConverterJavaTypeDescriptor() {
        return this.converterJavaTypeDescriptor;
    }

    @Override
    public BasicJavaDescriptor<O> getDomainJavaTypeDescriptor() {
        return this.domainJavaTypeDescriptor;
    }

    @Override
    public BasicJavaDescriptor<R> getRelationalJavaTypeDescriptor() {
        return this.relationalJavaTypeDescriptor;
    }
}

