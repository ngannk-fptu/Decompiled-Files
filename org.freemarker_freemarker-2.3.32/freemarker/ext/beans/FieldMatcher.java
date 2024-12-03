/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.MemberMatcher;
import java.lang.reflect.Field;

final class FieldMatcher
extends MemberMatcher<Field, String> {
    FieldMatcher() {
    }

    @Override
    protected String toMemberSignature(Field member) {
        return member.getName();
    }

    @Override
    protected boolean matchInUpperBoundTypeSubtypes() {
        return true;
    }
}

