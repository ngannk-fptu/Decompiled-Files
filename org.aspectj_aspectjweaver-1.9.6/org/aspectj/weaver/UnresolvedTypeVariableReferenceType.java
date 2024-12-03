/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableDeclaringElement;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class UnresolvedTypeVariableReferenceType
extends UnresolvedType
implements TypeVariableReference {
    private TypeVariable typeVariable;

    public UnresolvedTypeVariableReferenceType() {
        super("Ljava/lang/Object;");
    }

    public UnresolvedTypeVariableReferenceType(TypeVariable aTypeVariable) {
        super("T" + aTypeVariable.getName() + ";", aTypeVariable.getFirstBound().getErasureSignature());
        this.typeVariable = aTypeVariable;
    }

    public void setTypeVariable(TypeVariable aTypeVariable) {
        this.signature = "T" + aTypeVariable.getName() + ";";
        this.signatureErasure = aTypeVariable.getFirstBound().getErasureSignature();
        this.typeVariable = aTypeVariable;
        this.typeKind = UnresolvedType.TypeKind.TYPE_VARIABLE;
    }

    @Override
    public ResolvedType resolve(World world) {
        TypeVariableDeclaringElement typeVariableScope = world.getTypeVariableLookupScope();
        TypeVariable resolvedTypeVariable = null;
        TypeVariableReferenceType tvrt = null;
        if (typeVariableScope == null) {
            resolvedTypeVariable = this.typeVariable.resolve(world);
            tvrt = new TypeVariableReferenceType(resolvedTypeVariable, world);
        } else {
            boolean foundOK = false;
            resolvedTypeVariable = typeVariableScope.getTypeVariableNamed(this.typeVariable.getName());
            if (resolvedTypeVariable == null) {
                resolvedTypeVariable = this.typeVariable.resolve(world);
            } else {
                foundOK = true;
            }
            tvrt = new TypeVariableReferenceType(resolvedTypeVariable, world);
        }
        return tvrt;
    }

    @Override
    public boolean isTypeVariableReference() {
        return true;
    }

    @Override
    public TypeVariable getTypeVariable() {
        return this.typeVariable;
    }

    @Override
    public String toString() {
        if (this.typeVariable == null) {
            return "<type variable not set!>";
        }
        return "T" + this.typeVariable.getName() + ";";
    }

    @Override
    public String toDebugString() {
        return this.typeVariable.getName();
    }

    @Override
    public String getErasureSignature() {
        return this.typeVariable.getFirstBound().getSignature();
    }
}

