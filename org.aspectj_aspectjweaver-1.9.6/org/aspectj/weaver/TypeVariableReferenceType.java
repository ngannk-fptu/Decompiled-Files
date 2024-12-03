/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Map;
import org.aspectj.weaver.BoundedReferenceTypeDelegate;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class TypeVariableReferenceType
extends ReferenceType
implements TypeVariableReference {
    private TypeVariable typeVariable;

    public TypeVariableReferenceType(TypeVariable typeVariable, World world) {
        super(typeVariable.getGenericSignature(), typeVariable.getErasureSignature(), world);
        this.typeVariable = typeVariable;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TypeVariableReferenceType) {
            return this.typeVariable == ((TypeVariableReferenceType)other).typeVariable;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.typeVariable.hashCode();
    }

    @Override
    public ReferenceTypeDelegate getDelegate() {
        if (this.delegate == null) {
            ResolvedType resolvedFirstBound = this.typeVariable.getFirstBound().resolve(this.world);
            BoundedReferenceTypeDelegate brtd = null;
            if (resolvedFirstBound.isMissing()) {
                brtd = new BoundedReferenceTypeDelegate((ReferenceType)this.world.resolve(UnresolvedType.OBJECT));
                this.setDelegate(brtd);
                this.world.getLint().cantFindType.signal("Unable to find type for generic bound.  Missing type is " + resolvedFirstBound.getName(), this.getSourceLocation());
            } else {
                brtd = new BoundedReferenceTypeDelegate((ReferenceType)resolvedFirstBound);
                this.setDelegate(brtd);
            }
        }
        return this.delegate;
    }

    @Override
    public UnresolvedType parameterize(Map<String, UnresolvedType> typeBindings) {
        UnresolvedType ut = typeBindings.get(this.getName());
        if (ut != null) {
            return this.world.resolve(ut);
        }
        return this;
    }

    @Override
    public TypeVariable getTypeVariable() {
        return this.typeVariable;
    }

    @Override
    public boolean isTypeVariableReference() {
        return true;
    }

    @Override
    public String toString() {
        return this.typeVariable.getName();
    }

    @Override
    public boolean isGenericWildcard() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        ReferenceType upper = (ReferenceType)this.typeVariable.getUpperBound();
        if (upper.isAnnotation()) {
            return true;
        }
        World world = upper.getWorld();
        this.typeVariable.resolve(world);
        ResolvedType annotationType = ResolvedType.ANNOTATION.resolve(world);
        UnresolvedType[] ifBounds = this.typeVariable.getSuperInterfaces();
        for (int i = 0; i < ifBounds.length; ++i) {
            if (((ReferenceType)ifBounds[i]).isAnnotation()) {
                return true;
            }
            if (!ifBounds[i].equals(annotationType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getSignature() {
        StringBuffer sb = new StringBuffer();
        sb.append("T");
        sb.append(this.typeVariable.getName());
        sb.append(";");
        return sb.toString();
    }

    public String getTypeVariableName() {
        return this.typeVariable.getName();
    }

    public ReferenceType getUpperBound() {
        return (ReferenceType)this.typeVariable.resolve(this.world).getUpperBound();
    }

    @Override
    public ResolvedType resolve(World world) {
        this.typeVariable.resolve(world);
        return this;
    }

    public boolean isTypeVariableResolved() {
        return this.typeVariable.isResolved;
    }
}

