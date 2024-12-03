/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ExecutableMemberSignature;
import freemarker.ext.beans.MemberMatcher;
import java.lang.reflect.Constructor;

final class ConstructorMatcher
extends MemberMatcher<Constructor<?>, ExecutableMemberSignature> {
    ConstructorMatcher() {
    }

    @Override
    protected ExecutableMemberSignature toMemberSignature(Constructor<?> member) {
        return new ExecutableMemberSignature(member);
    }

    @Override
    protected boolean matchInUpperBoundTypeSubtypes() {
        return false;
    }
}

