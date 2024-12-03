/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.ResolvedType
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Converter
 */
package org.hibernate.boot.model.convert.internal;

import com.fasterxml.classmate.ResolvedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.hibernate.AnnotationException;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.convert.internal.AutoApplicableConverterDescriptorBypassedImpl;
import org.hibernate.boot.model.convert.internal.AutoApplicableConverterDescriptorStandardImpl;
import org.hibernate.boot.model.convert.spi.AutoApplicableConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext;
import org.hibernate.metamodel.model.convert.internal.JpaAttributeConverterImpl;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.resource.beans.spi.ManagedBean;

public abstract class AbstractConverterDescriptor
implements ConverterDescriptor {
    private final Class<? extends AttributeConverter> converterClass;
    private final ResolvedType domainType;
    private final ResolvedType jdbcType;
    private final AutoApplicableConverterDescriptor autoApplicableDescriptor;

    public AbstractConverterDescriptor(Class<? extends AttributeConverter> converterClass, Boolean forceAutoApply, ClassmateContext classmateContext) {
        this.converterClass = converterClass;
        ResolvedType converterType = classmateContext.getTypeResolver().resolve(converterClass, new Type[0]);
        List converterParamTypes = converterType.typeParametersFor(AttributeConverter.class);
        if (converterParamTypes == null) {
            throw new AnnotationException("Could not extract type parameter information from AttributeConverter implementation [" + converterClass.getName() + "]");
        }
        if (converterParamTypes.size() != 2) {
            throw new AnnotationException("Unexpected type parameter information for AttributeConverter implementation [" + converterClass.getName() + "]; expected 2 parameter types, but found " + converterParamTypes.size());
        }
        this.domainType = (ResolvedType)converterParamTypes.get(0);
        this.jdbcType = (ResolvedType)converterParamTypes.get(1);
        this.autoApplicableDescriptor = this.resolveAutoApplicableDescriptor(converterClass, forceAutoApply);
    }

    private AutoApplicableConverterDescriptor resolveAutoApplicableDescriptor(Class<? extends AttributeConverter> converterClass, Boolean forceAutoApply) {
        Converter annotation;
        boolean autoApply = forceAutoApply != null ? forceAutoApply : (annotation = converterClass.getAnnotation(Converter.class)) != null && annotation.autoApply();
        return autoApply ? new AutoApplicableConverterDescriptorStandardImpl(this) : AutoApplicableConverterDescriptorBypassedImpl.INSTANCE;
    }

    @Override
    public Class<? extends AttributeConverter> getAttributeConverterClass() {
        return this.converterClass;
    }

    @Override
    public ResolvedType getDomainValueResolvedType() {
        return this.domainType;
    }

    @Override
    public ResolvedType getRelationalValueResolvedType() {
        return this.jdbcType;
    }

    @Override
    public AutoApplicableConverterDescriptor getAutoApplyDescriptor() {
        return this.autoApplicableDescriptor;
    }

    @Override
    public JpaAttributeConverter createJpaAttributeConverter(JpaAttributeConverterCreationContext context) {
        return new JpaAttributeConverterImpl(this.createManagedBean(context), context.getJavaTypeDescriptorRegistry().getDescriptor(this.getAttributeConverterClass()), context.getJavaTypeDescriptorRegistry().getDescriptor(this.getDomainValueResolvedType().getErasedType()), context.getJavaTypeDescriptorRegistry().getDescriptor(this.getRelationalValueResolvedType().getErasedType()));
    }

    protected abstract ManagedBean<? extends AttributeConverter> createManagedBean(JpaAttributeConverterCreationContext var1);
}

