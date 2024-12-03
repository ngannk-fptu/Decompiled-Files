/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.ResolvedType
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.boot.model.convert.spi;

import com.fasterxml.classmate.ResolvedType;
import javax.persistence.AttributeConverter;
import org.hibernate.boot.model.convert.spi.AutoApplicableConverterDescriptor;
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;

public interface ConverterDescriptor {
    public Class<? extends AttributeConverter> getAttributeConverterClass();

    public ResolvedType getDomainValueResolvedType();

    public ResolvedType getRelationalValueResolvedType();

    public AutoApplicableConverterDescriptor getAutoApplyDescriptor();

    public JpaAttributeConverter createJpaAttributeConverter(JpaAttributeConverterCreationContext var1);
}

