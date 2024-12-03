/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Member;
import java.util.Map;
import ognl.MemberAccess;

public abstract class AbstractMemberAccess
implements MemberAccess {
    @Override
    public Object setup(Map context, Object target, Member member, String propertyName) {
        return null;
    }

    @Override
    public void restore(Map context, Object target, Member member, String propertyName, Object state) {
    }
}

