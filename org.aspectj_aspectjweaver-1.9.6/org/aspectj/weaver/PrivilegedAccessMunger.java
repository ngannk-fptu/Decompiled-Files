/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;

public class PrivilegedAccessMunger
extends ResolvedTypeMunger {
    public boolean shortSyntax = false;

    public PrivilegedAccessMunger(ResolvedMember member, boolean shortSyntax) {
        super(PrivilegedAccess, member);
        this.shortSyntax = shortSyntax;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("should not be serialized");
    }

    public ResolvedMember getMember() {
        return this.getSignature();
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member, ResolvedType aspectType) {
        if (this.getSignature().getKind() == Member.FIELD) {
            ResolvedMember ret = AjcMemberMaker.privilegedAccessMethodForFieldGet(aspectType, this.getSignature(), this.shortSyntax);
            if (ResolvedType.matches(ret, member)) {
                return this.getSignature();
            }
            ret = AjcMemberMaker.privilegedAccessMethodForFieldSet(aspectType, this.getSignature(), this.shortSyntax);
            if (ResolvedType.matches(ret, member)) {
                return this.getSignature();
            }
        } else {
            ResolvedMember ret = AjcMemberMaker.privilegedAccessMethodForMethod(aspectType, this.getSignature());
            if (ResolvedType.matches(ret, member)) {
                return this.getSignature();
            }
        }
        return null;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PrivilegedAccessMunger)) {
            return false;
        }
        PrivilegedAccessMunger o = (PrivilegedAccessMunger)other;
        return this.kind.equals(o.kind) && (o.signature == null ? this.signature == null : this.signature.equals(o.signature)) && (o.declaredSignature == null ? this.declaredSignature == null : this.declaredSignature.equals(o.declaredSignature)) && (o.typeVariableAliases == null ? this.typeVariableAliases == null : this.typeVariableAliases.equals(o.typeVariableAliases));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + (this.signature == null ? 0 : this.signature.hashCode());
        result = 37 * result + (this.declaredSignature == null ? 0 : this.declaredSignature.hashCode());
        result = 37 * result + (this.typeVariableAliases == null ? 0 : this.typeVariableAliases.hashCode());
        return result;
    }

    @Override
    public boolean existsToSupportShadowMunging() {
        return true;
    }
}

