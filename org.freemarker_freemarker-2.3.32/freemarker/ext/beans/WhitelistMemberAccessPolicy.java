/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import freemarker.ext.beans.TemplateAccessible;
import java.lang.reflect.Method;
import java.util.Collection;

public class WhitelistMemberAccessPolicy
extends MemberSelectorListMemberAccessPolicy {
    private static final Method TO_STRING_METHOD;
    private final boolean toStringAlwaysExposed = this.forClass(Object.class).isMethodExposed(TO_STRING_METHOD);

    public WhitelistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
        super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.WHITELIST, TemplateAccessible.class);
    }

    @Override
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }

    static {
        try {
            TO_STRING_METHOD = Object.class.getMethod("toString", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}

