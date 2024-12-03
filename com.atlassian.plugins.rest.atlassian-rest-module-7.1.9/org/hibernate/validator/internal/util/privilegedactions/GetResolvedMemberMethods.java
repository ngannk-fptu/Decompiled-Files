/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.security.PrivilegedAction;

public final class GetResolvedMemberMethods
implements PrivilegedAction<ResolvedMethod[]> {
    private final ResolvedTypeWithMembers type;

    public static GetResolvedMemberMethods action(ResolvedTypeWithMembers type) {
        return new GetResolvedMemberMethods(type);
    }

    private GetResolvedMemberMethods(ResolvedTypeWithMembers type) {
        this.type = type;
    }

    @Override
    public ResolvedMethod[] run() {
        return this.type.getMemberMethods();
    }
}

