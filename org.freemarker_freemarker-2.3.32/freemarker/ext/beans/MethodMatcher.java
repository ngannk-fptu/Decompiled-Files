/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ExecutableMemberSignature;
import freemarker.ext.beans.MemberMatcher;
import java.lang.reflect.Method;

final class MethodMatcher
extends MemberMatcher<Method, ExecutableMemberSignature> {
    MethodMatcher() {
    }

    @Override
    protected ExecutableMemberSignature toMemberSignature(Method member) {
        return new ExecutableMemberSignature(member);
    }

    @Override
    protected boolean matchInUpperBoundTypeSubtypes() {
        return true;
    }
}

