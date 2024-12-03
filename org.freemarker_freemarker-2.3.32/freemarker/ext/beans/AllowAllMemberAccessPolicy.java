/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ClassMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class AllowAllMemberAccessPolicy
implements MemberAccessPolicy {
    public static final AllowAllMemberAccessPolicy INSTANCE = new AllowAllMemberAccessPolicy();
    public static final ClassMemberAccessPolicy CLASS_POLICY_INSTANCE = new ClassMemberAccessPolicy(){

        @Override
        public boolean isMethodExposed(Method method) {
            return true;
        }

        @Override
        public boolean isConstructorExposed(Constructor<?> constructor) {
            return true;
        }

        @Override
        public boolean isFieldExposed(Field field) {
            return true;
        }
    };

    private AllowAllMemberAccessPolicy() {
    }

    @Override
    public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
        return CLASS_POLICY_INSTANCE;
    }

    @Override
    public boolean isToStringAlwaysExposed() {
        return true;
    }
}

