/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Map;
import org.aspectj.weaver.BoundedReferenceTypeDelegate;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class BoundedReferenceType
extends ReferenceType {
    public static final int UNBOUND = 0;
    public static final int EXTENDS = 1;
    public static final int SUPER = 2;
    public int kind;
    private ResolvedType lowerBound;
    private ResolvedType upperBound;
    protected ReferenceType[] additionalInterfaceBounds = ReferenceType.EMPTY_ARRAY;

    public BoundedReferenceType(ReferenceType aBound, boolean isExtends, World world) {
        super((isExtends ? "+" : "-") + aBound.signature, aBound.signatureErasure, world);
        this.kind = isExtends ? 1 : 2;
        if (isExtends) {
            this.upperBound = aBound;
        } else {
            this.lowerBound = aBound;
            this.upperBound = world.resolve(UnresolvedType.OBJECT);
        }
        this.setDelegate(new BoundedReferenceTypeDelegate((ReferenceType)this.getUpperBound()));
    }

    public BoundedReferenceType(ReferenceType aBound, boolean isExtends, World world, ReferenceType[] additionalInterfaces) {
        this(aBound, isExtends, world);
        this.additionalInterfaceBounds = additionalInterfaces;
    }

    protected BoundedReferenceType(String signature, String erasedSignature, World world) {
        super(signature, erasedSignature, world);
        if (signature.equals("*")) {
            this.kind = 0;
            this.upperBound = world.resolve(UnresolvedType.OBJECT);
        } else {
            this.upperBound = world.resolve(BoundedReferenceType.forSignature(erasedSignature));
        }
        this.setDelegate(new BoundedReferenceTypeDelegate((ReferenceType)this.upperBound));
    }

    public BoundedReferenceType(World world) {
        super("*", "Ljava/lang/Object;", world);
        this.kind = 0;
        this.upperBound = world.resolve(UnresolvedType.OBJECT);
        this.setDelegate(new BoundedReferenceTypeDelegate((ReferenceType)this.upperBound));
    }

    public UnresolvedType getUpperBound() {
        return this.upperBound;
    }

    public UnresolvedType getLowerBound() {
        return this.lowerBound;
    }

    public ReferenceType[] getAdditionalBounds() {
        return this.additionalInterfaceBounds;
    }

    @Override
    public UnresolvedType parameterize(Map<String, UnresolvedType> typeBindings) {
        if (this.kind == 0) {
            return this;
        }
        ReferenceType[] parameterizedAdditionalInterfaces = new ReferenceType[this.additionalInterfaceBounds == null ? 0 : this.additionalInterfaceBounds.length];
        for (int i = 0; i < parameterizedAdditionalInterfaces.length; ++i) {
            parameterizedAdditionalInterfaces[i] = (ReferenceType)this.additionalInterfaceBounds[i].parameterize(typeBindings);
        }
        if (this.kind == 1) {
            UnresolvedType parameterizedUpperBound = this.getUpperBound().parameterize(typeBindings);
            if (!(parameterizedUpperBound instanceof ReferenceType)) {
                throw new IllegalStateException("DEBUG551732: Unexpected problem processing bounds. Parameterizing " + this.getUpperBound() + " produced " + parameterizedUpperBound + " (Type: " + parameterizedUpperBound == null ? "null" : parameterizedUpperBound.getClass().getName() + ") (typeBindings=" + typeBindings + ")");
            }
            return new BoundedReferenceType((ReferenceType)parameterizedUpperBound, true, this.world, parameterizedAdditionalInterfaces);
        }
        UnresolvedType parameterizedLowerBound = this.getLowerBound().parameterize(typeBindings);
        if (!(parameterizedLowerBound instanceof ReferenceType)) {
            throw new IllegalStateException("PR543023: Unexpectedly found a non reference type: " + parameterizedLowerBound.getClass().getName() + " with signature " + parameterizedLowerBound.getSignature());
        }
        return new BoundedReferenceType((ReferenceType)parameterizedLowerBound, false, this.world, parameterizedAdditionalInterfaces);
    }

    @Override
    public String getSignatureForAttribute() {
        StringBuilder ret = new StringBuilder();
        if (this.kind == 2) {
            ret.append("-");
            ret.append(this.lowerBound.getSignatureForAttribute());
            for (int i = 0; i < this.additionalInterfaceBounds.length; ++i) {
                ret.append(this.additionalInterfaceBounds[i].getSignatureForAttribute());
            }
        } else if (this.kind == 1) {
            ret.append("+");
            ret.append(this.upperBound.getSignatureForAttribute());
            for (int i = 0; i < this.additionalInterfaceBounds.length; ++i) {
                ret.append(this.additionalInterfaceBounds[i].getSignatureForAttribute());
            }
        } else if (this.kind == 0) {
            ret.append("*");
        }
        return ret.toString();
    }

    public boolean hasLowerBound() {
        return this.lowerBound != null;
    }

    public boolean isExtends() {
        return this.kind == 1;
    }

    public boolean isSuper() {
        return this.kind == 2;
    }

    public boolean isUnbound() {
        return this.kind == 0;
    }

    public boolean alwaysMatches(ResolvedType aCandidateType) {
        if (this.isExtends()) {
            return ((ReferenceType)this.getUpperBound()).isAssignableFrom(aCandidateType);
        }
        if (this.isSuper()) {
            return aCandidateType.isAssignableFrom((ReferenceType)this.getLowerBound());
        }
        return true;
    }

    public boolean canBeCoercedTo(ResolvedType aCandidateType) {
        if (this.alwaysMatches(aCandidateType)) {
            return true;
        }
        if (aCandidateType.isGenericWildcard()) {
            BoundedReferenceType boundedRT = (BoundedReferenceType)aCandidateType;
            ResolvedType myUpperBound = (ResolvedType)this.getUpperBound();
            ResolvedType myLowerBound = (ResolvedType)this.getLowerBound();
            if (this.isExtends()) {
                if (boundedRT.isExtends()) {
                    return myUpperBound.isAssignableFrom((ResolvedType)boundedRT.getUpperBound());
                }
                if (boundedRT.isSuper()) {
                    return myUpperBound == boundedRT.getLowerBound();
                }
                return true;
            }
            if (this.isSuper()) {
                if (boundedRT.isSuper()) {
                    return ((ResolvedType)boundedRT.getLowerBound()).isAssignableFrom(myLowerBound);
                }
                if (boundedRT.isExtends()) {
                    return myLowerBound == boundedRT.getUpperBound();
                }
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public String getSimpleName() {
        if (!this.isExtends() && !this.isSuper()) {
            return "?";
        }
        if (this.isExtends()) {
            return "? extends " + this.getUpperBound().getSimpleName();
        }
        return "? super " + this.getLowerBound().getSimpleName();
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        ResolvedType[] interfaces = super.getDeclaredInterfaces();
        if (this.additionalInterfaceBounds.length > 0) {
            ResolvedType[] allInterfaces = new ResolvedType[interfaces.length + this.additionalInterfaceBounds.length];
            System.arraycopy(interfaces, 0, allInterfaces, 0, interfaces.length);
            System.arraycopy(this.additionalInterfaceBounds, 0, allInterfaces, interfaces.length, this.additionalInterfaceBounds.length);
            return allInterfaces;
        }
        return interfaces;
    }

    @Override
    public boolean isGenericWildcard() {
        return true;
    }
}

