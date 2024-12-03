/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.ResolvedType
 *  com.fasterxml.classmate.ResolvedTypeWithMembers
 *  com.fasterxml.classmate.members.ResolvedField
 *  com.fasterxml.classmate.members.ResolvedMember
 *  com.fasterxml.classmate.members.ResolvedMethod
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.boot.model.convert.internal;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.convert.spi.AutoApplicableConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.annotations.HCANNHelper;
import org.hibernate.internal.util.type.PrimitiveWrapperHelper;

public class AutoApplicableConverterDescriptorStandardImpl
implements AutoApplicableConverterDescriptor {
    private final ConverterDescriptor linkedConverterDescriptor;

    public AutoApplicableConverterDescriptorStandardImpl(ConverterDescriptor linkedConverterDescriptor) {
        this.linkedConverterDescriptor = linkedConverterDescriptor;
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForAttribute(XProperty xProperty, MetadataBuildingContext context) {
        ResolvedType attributeType = this.resolveAttributeType(xProperty, context);
        return this.typesMatch(this.linkedConverterDescriptor.getDomainValueResolvedType(), attributeType) ? this.linkedConverterDescriptor : null;
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForCollectionElement(XProperty xProperty, MetadataBuildingContext context) {
        ResolvedType elementType;
        ResolvedMember collectionMember = this.resolveMember(xProperty, context);
        if (Map.class.isAssignableFrom(collectionMember.getType().getErasedType())) {
            elementType = (ResolvedType)collectionMember.getType().typeParametersFor(Map.class).get(1);
        } else if (Collection.class.isAssignableFrom(collectionMember.getType().getErasedType())) {
            elementType = (ResolvedType)collectionMember.getType().typeParametersFor(Collection.class).get(0);
        } else {
            throw new HibernateException("Attribute was neither a Collection nor a Map : " + collectionMember.getType().getErasedType());
        }
        return this.typesMatch(this.linkedConverterDescriptor.getDomainValueResolvedType(), elementType) ? this.linkedConverterDescriptor : null;
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForMapKey(XProperty xProperty, MetadataBuildingContext context) {
        ResolvedMember collectionMember = this.resolveMember(xProperty, context);
        if (!Map.class.isAssignableFrom(collectionMember.getType().getErasedType())) {
            throw new HibernateException("Attribute was not a Map : " + collectionMember.getType().getErasedType());
        }
        ResolvedType keyType = (ResolvedType)collectionMember.getType().typeParametersFor(Map.class).get(0);
        return this.typesMatch(this.linkedConverterDescriptor.getDomainValueResolvedType(), keyType) ? this.linkedConverterDescriptor : null;
    }

    private ResolvedType resolveAttributeType(XProperty xProperty, MetadataBuildingContext context) {
        return this.resolveMember(xProperty, context).getType();
    }

    private ResolvedMember resolveMember(XProperty xProperty, MetadataBuildingContext buildingContext) {
        ClassmateContext classmateContext = buildingContext.getBootstrapContext().getClassmateContext();
        ReflectionManager reflectionManager = buildingContext.getBootstrapContext().getReflectionManager();
        ResolvedType declaringClassType = classmateContext.getTypeResolver().resolve((Type)reflectionManager.toClass(xProperty.getDeclaringClass()), new Type[0]);
        ResolvedTypeWithMembers declaringClassWithMembers = classmateContext.getMemberResolver().resolve(declaringClassType, null, null);
        Member member = AutoApplicableConverterDescriptorStandardImpl.toMember(xProperty);
        if (member instanceof Method) {
            for (ResolvedMethod resolvedMember : declaringClassWithMembers.getMemberMethods()) {
                if (!resolvedMember.getName().equals(member.getName())) continue;
                return resolvedMember;
            }
        } else if (member instanceof Field) {
            for (ResolvedField resolvedMember : declaringClassWithMembers.getMemberFields()) {
                if (!resolvedMember.getName().equals(member.getName())) continue;
                return resolvedMember;
            }
        } else {
            throw new HibernateException("Unexpected java.lang.reflect.Member type from org.hibernate.annotations.common.reflection.java.JavaXMember : " + member);
        }
        throw new HibernateException("Could not locate resolved type information for attribute [" + member.getName() + "] from Classmate");
    }

    private static Member toMember(XProperty xProperty) {
        try {
            return HCANNHelper.getUnderlyingMember(xProperty);
        }
        catch (Exception e) {
            throw new HibernateException("Could not resolve member signature from XProperty reference", e);
        }
    }

    private boolean typesMatch(ResolvedType converterDefinedType, ResolvedType checkType) {
        Class erasedCheckType = checkType.getErasedType();
        if (erasedCheckType.isPrimitive()) {
            erasedCheckType = PrimitiveWrapperHelper.getDescriptorByPrimitiveType(erasedCheckType).getWrapperClass();
        }
        if (!converterDefinedType.getErasedType().isAssignableFrom(erasedCheckType)) {
            return false;
        }
        if (converterDefinedType.getTypeParameters().isEmpty()) {
            return true;
        }
        if (checkType.getTypeParameters().isEmpty()) {
            return false;
        }
        if (converterDefinedType.getTypeParameters().size() != checkType.getTypeParameters().size()) {
            return false;
        }
        for (int i = 0; i < converterDefinedType.getTypeParameters().size(); ++i) {
            if (this.typesMatch((ResolvedType)converterDefinedType.getTypeParameters().get(i), (ResolvedType)checkType.getTypeParameters().get(i))) continue;
            return false;
        }
        return true;
    }
}

