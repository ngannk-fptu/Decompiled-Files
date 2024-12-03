/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class ArrayReferenceType
extends ReferenceType {
    private final ResolvedType componentType;

    public ArrayReferenceType(String sig, String erasureSig, World world, ResolvedType componentType) {
        super(sig, erasureSig, world);
        this.componentType = componentType;
    }

    @Override
    public final ResolvedMember[] getDeclaredFields() {
        return ResolvedMember.NONE;
    }

    @Override
    public final ResolvedMember[] getDeclaredMethods() {
        return ResolvedMember.NONE;
    }

    @Override
    public final ResolvedType[] getDeclaredInterfaces() {
        return new ResolvedType[]{this.world.getCoreType(CLONEABLE), this.world.getCoreType(SERIALIZABLE)};
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        return null;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return AnnotationAJ.EMPTY_ARRAY;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        return ResolvedType.NONE;
    }

    @Override
    public final ResolvedMember[] getDeclaredPointcuts() {
        return ResolvedMember.NONE;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        return false;
    }

    @Override
    public final ResolvedType getSuperclass() {
        return this.world.getCoreType(OBJECT);
    }

    @Override
    public final boolean isAssignableFrom(ResolvedType o) {
        if (!o.isArray()) {
            return false;
        }
        if (o.getComponentType().isPrimitiveType()) {
            return o.equals(this);
        }
        return this.getComponentType().resolve(this.world).isAssignableFrom(o.getComponentType().resolve(this.world));
    }

    @Override
    public boolean isAssignableFrom(ResolvedType o, boolean allowMissing) {
        return this.isAssignableFrom(o);
    }

    @Override
    public final boolean isCoerceableFrom(ResolvedType o) {
        if (o.equals(UnresolvedType.OBJECT) || o.equals(UnresolvedType.SERIALIZABLE) || o.equals(UnresolvedType.CLONEABLE)) {
            return true;
        }
        if (!o.isArray()) {
            return false;
        }
        if (o.getComponentType().isPrimitiveType()) {
            return o.equals(this);
        }
        return this.getComponentType().resolve(this.world).isCoerceableFrom(o.getComponentType().resolve(this.world));
    }

    @Override
    public final int getModifiers() {
        int mask = 7;
        return this.componentType.getModifiers() & mask | 0x10;
    }

    @Override
    public UnresolvedType getComponentType() {
        return this.componentType;
    }

    @Override
    public ResolvedType getResolvedComponentType() {
        return this.componentType;
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.getResolvedComponentType().getSourceContext();
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        if (this.typeVariables == null && this.componentType.getTypeVariables() != null) {
            this.typeVariables = this.componentType.getTypeVariables();
            for (int i = 0; i < this.typeVariables.length; ++i) {
                this.typeVariables[i].resolve(this.world);
            }
        }
        return this.typeVariables;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        return false;
    }

    @Override
    public boolean isAspect() {
        return false;
    }

    @Override
    public boolean isPrimitiveType() {
        return this.typeKind == UnresolvedType.TypeKind.PRIMITIVE;
    }

    @Override
    public boolean isSimpleType() {
        return this.typeKind == UnresolvedType.TypeKind.SIMPLE;
    }

    @Override
    public boolean isRawType() {
        return this.typeKind == UnresolvedType.TypeKind.RAW;
    }

    @Override
    public boolean isGenericType() {
        return this.typeKind == UnresolvedType.TypeKind.GENERIC;
    }

    @Override
    public boolean isParameterizedType() {
        return this.typeKind == UnresolvedType.TypeKind.PARAMETERIZED;
    }

    @Override
    public boolean isTypeVariableReference() {
        return this.typeKind == UnresolvedType.TypeKind.TYPE_VARIABLE;
    }

    @Override
    public boolean isGenericWildcard() {
        return this.typeKind == UnresolvedType.TypeKind.WILDCARD;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isNested() {
        return false;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isExposedToWeaver() {
        return false;
    }

    @Override
    public boolean canAnnotationTargetType() {
        return false;
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        return null;
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        return false;
    }

    @Override
    public boolean isPrimitiveArray() {
        if (this.componentType.isPrimitiveType()) {
            return true;
        }
        if (this.componentType.isArray()) {
            return this.componentType.isPrimitiveArray();
        }
        return false;
    }
}

