/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.PrivilegedAccessMunger;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.UnresolvedType;

public class ExposeTypeMunger
extends PrivilegedAccessMunger {
    public ExposeTypeMunger(UnresolvedType typeToExpose) {
        super(new ResolvedMemberImpl(Member.STATIC_INITIALIZATION, typeToExpose, 0, UnresolvedType.VOID, "<clinit>", UnresolvedType.NONE), false);
    }

    @Override
    public String toString() {
        return "ExposeTypeMunger(" + this.getSignature().getDeclaringType().getName() + ")";
    }

    public String getExposedTypeSignature() {
        return this.getSignature().getDeclaringType().getSignature();
    }
}

