/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.UnresolvedType;

public class WildcardedUnresolvedType
extends UnresolvedType {
    public static final int UNBOUND = 0;
    public static final int EXTENDS = 1;
    public static final int SUPER = 2;
    public static final WildcardedUnresolvedType QUESTIONMARK = new WildcardedUnresolvedType("*", UnresolvedType.OBJECT, null);
    private int boundKind = 0;
    private UnresolvedType lowerBound;
    private UnresolvedType upperBound;

    public WildcardedUnresolvedType(String signature, UnresolvedType upperBound, UnresolvedType lowerBound) {
        super(signature, upperBound == null ? UnresolvedType.OBJECT.signature : upperBound.signatureErasure);
        this.typeKind = UnresolvedType.TypeKind.WILDCARD;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        if (signature.charAt(0) == '-') {
            this.boundKind = 2;
        }
        if (signature.charAt(0) == '+') {
            this.boundKind = 1;
        }
    }

    public UnresolvedType getUpperBound() {
        return this.upperBound;
    }

    public UnresolvedType getLowerBound() {
        return this.lowerBound;
    }

    public boolean isExtends() {
        return this.boundKind == 1;
    }

    public boolean isSuper() {
        return this.boundKind == 2;
    }

    public boolean isUnbound() {
        return this.boundKind == 0;
    }
}

