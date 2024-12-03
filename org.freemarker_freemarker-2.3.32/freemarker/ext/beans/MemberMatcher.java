/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class MemberMatcher<M extends Member, S> {
    private final Map<S, Types> signaturesToUpperBoundTypes = new HashMap<S, Types>();

    MemberMatcher() {
    }

    protected abstract S toMemberSignature(M var1);

    protected abstract boolean matchInUpperBoundTypeSubtypes();

    void addMatching(Class<?> upperBoundType, M member) {
        Class<?> declaringClass = member.getDeclaringClass();
        if (!declaringClass.isAssignableFrom(upperBoundType)) {
            throw new IllegalArgumentException("Upper bound class " + upperBoundType.getName() + " is not the same type or a subtype of the declaring type of member " + member + ".");
        }
        S memberSignature = this.toMemberSignature(member);
        Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
        if (upperBoundTypes == null) {
            upperBoundTypes = new Types();
            this.signaturesToUpperBoundTypes.put(memberSignature, upperBoundTypes);
        }
        upperBoundTypes.set.add(upperBoundType);
        if (upperBoundType.isInterface()) {
            upperBoundTypes.containsInterfaces = true;
        }
    }

    boolean matches(Class<?> contextClass, M member) {
        S memberSignature = this.toMemberSignature(member);
        Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
        return upperBoundTypes != null && (this.matchInUpperBoundTypeSubtypes() ? MemberMatcher.containsTypeOrSuperType(upperBoundTypes, contextClass) : MemberMatcher.containsExactType(upperBoundTypes, contextClass));
    }

    private static boolean containsExactType(Types types, Class<?> c) {
        if (c == null) {
            return false;
        }
        return types.set.contains(c);
    }

    private static boolean containsTypeOrSuperType(Types types, Class<?> c) {
        if (c == null) {
            return false;
        }
        if (types.set.contains(c)) {
            return true;
        }
        if (MemberMatcher.containsTypeOrSuperType(types, c.getSuperclass())) {
            return true;
        }
        if (types.containsInterfaces) {
            for (Class<?> anInterface : c.getInterfaces()) {
                if (!MemberMatcher.containsTypeOrSuperType(types, anInterface)) continue;
                return true;
            }
        }
        return false;
    }

    private static class Types {
        private final Set<Class<?>> set = new HashSet();
        private boolean containsInterfaces;

        private Types() {
        }
    }
}

