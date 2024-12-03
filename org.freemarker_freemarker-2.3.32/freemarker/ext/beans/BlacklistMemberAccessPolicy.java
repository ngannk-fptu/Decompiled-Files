/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import java.lang.reflect.Method;
import java.util.Collection;

public class BlacklistMemberAccessPolicy
extends MemberSelectorListMemberAccessPolicy {
    private final boolean toStringAlwaysExposed;

    public BlacklistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
        super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.BLACKLIST, null);
        boolean toStringBlacklistedAnywhere = false;
        for (MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector : memberSelectors) {
            Method method = memberSelector.getMethod();
            if (method == null || !method.getName().equals("toString") || method.getParameterTypes().length != 0) continue;
            toStringBlacklistedAnywhere = true;
            break;
        }
        this.toStringAlwaysExposed = !toStringBlacklistedAnywhere;
    }

    @Override
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }
}

