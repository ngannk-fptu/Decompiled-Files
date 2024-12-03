/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class MethodMatchers {
    public static MethodMatcher union(MethodMatcher mm1, MethodMatcher mm2) {
        return new UnionMethodMatcher(mm1, mm2);
    }

    static MethodMatcher union(MethodMatcher mm1, ClassFilter cf1, MethodMatcher mm2, ClassFilter cf2) {
        return new ClassFilterAwareUnionMethodMatcher(mm1, cf1, mm2, cf2);
    }

    public static MethodMatcher intersection(MethodMatcher mm1, MethodMatcher mm2) {
        return new IntersectionMethodMatcher(mm1, mm2);
    }

    public static boolean matches(MethodMatcher mm, Method method, @Nullable Class<?> targetClass, boolean hasIntroductions) {
        Assert.notNull((Object)mm, "MethodMatcher must not be null");
        return mm instanceof IntroductionAwareMethodMatcher ? ((IntroductionAwareMethodMatcher)mm).matches(method, targetClass, hasIntroductions) : mm.matches(method, targetClass);
    }

    private static class IntersectionMethodMatcher
    implements IntroductionAwareMethodMatcher,
    Serializable {
        private final MethodMatcher mm1;
        private final MethodMatcher mm2;

        public IntersectionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            Assert.notNull((Object)mm1, "First MethodMatcher must not be null");
            Assert.notNull((Object)mm2, "Second MethodMatcher must not be null");
            this.mm1 = mm1;
            this.mm2 = mm2;
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass, boolean hasIntroductions) {
            return MethodMatchers.matches(this.mm1, method, targetClass, hasIntroductions) && MethodMatchers.matches(this.mm2, method, targetClass, hasIntroductions);
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass) {
            return this.mm1.matches(method, targetClass) && this.mm2.matches(method, targetClass);
        }

        @Override
        public boolean isRuntime() {
            return this.mm1.isRuntime() || this.mm2.isRuntime();
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass, Object ... args) {
            boolean aMatches = this.mm1.isRuntime() ? this.mm1.matches(method, targetClass, args) : this.mm1.matches(method, targetClass);
            boolean bMatches = this.mm2.isRuntime() ? this.mm2.matches(method, targetClass, args) : this.mm2.matches(method, targetClass);
            return aMatches && bMatches;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof IntersectionMethodMatcher)) {
                return false;
            }
            IntersectionMethodMatcher that = (IntersectionMethodMatcher)other;
            return this.mm1.equals(that.mm1) && this.mm2.equals(that.mm2);
        }

        public int hashCode() {
            return 37 * this.mm1.hashCode() + this.mm2.hashCode();
        }
    }

    private static class ClassFilterAwareUnionMethodMatcher
    extends UnionMethodMatcher {
        private final ClassFilter cf1;
        private final ClassFilter cf2;

        public ClassFilterAwareUnionMethodMatcher(MethodMatcher mm1, ClassFilter cf1, MethodMatcher mm2, ClassFilter cf2) {
            super(mm1, mm2);
            this.cf1 = cf1;
            this.cf2 = cf2;
        }

        @Override
        protected boolean matchesClass1(@Nullable Class<?> targetClass) {
            return targetClass != null && this.cf1.matches(targetClass);
        }

        @Override
        protected boolean matchesClass2(@Nullable Class<?> targetClass) {
            return targetClass != null && this.cf2.matches(targetClass);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!super.equals(other)) {
                return false;
            }
            ClassFilter otherCf1 = ClassFilter.TRUE;
            ClassFilter otherCf2 = ClassFilter.TRUE;
            if (other instanceof ClassFilterAwareUnionMethodMatcher) {
                ClassFilterAwareUnionMethodMatcher cfa = (ClassFilterAwareUnionMethodMatcher)other;
                otherCf1 = cfa.cf1;
                otherCf2 = cfa.cf2;
            }
            return this.cf1.equals(otherCf1) && this.cf2.equals(otherCf2);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class UnionMethodMatcher
    implements IntroductionAwareMethodMatcher,
    Serializable {
        private final MethodMatcher mm1;
        private final MethodMatcher mm2;

        public UnionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            Assert.notNull((Object)mm1, "First MethodMatcher must not be null");
            Assert.notNull((Object)mm2, "Second MethodMatcher must not be null");
            this.mm1 = mm1;
            this.mm2 = mm2;
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass, boolean hasIntroductions) {
            return this.matchesClass1(targetClass) && MethodMatchers.matches(this.mm1, method, targetClass, hasIntroductions) || this.matchesClass2(targetClass) && MethodMatchers.matches(this.mm2, method, targetClass, hasIntroductions);
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass) {
            return this.matchesClass1(targetClass) && this.mm1.matches(method, targetClass) || this.matchesClass2(targetClass) && this.mm2.matches(method, targetClass);
        }

        protected boolean matchesClass1(@Nullable Class<?> targetClass) {
            return true;
        }

        protected boolean matchesClass2(@Nullable Class<?> targetClass) {
            return true;
        }

        @Override
        public boolean isRuntime() {
            return this.mm1.isRuntime() || this.mm2.isRuntime();
        }

        @Override
        public boolean matches(Method method, @Nullable Class<?> targetClass, Object ... args) {
            return this.mm1.matches(method, targetClass, args) || this.mm2.matches(method, targetClass, args);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof UnionMethodMatcher)) {
                return false;
            }
            UnionMethodMatcher that = (UnionMethodMatcher)other;
            return this.mm1.equals(that.mm1) && this.mm2.equals(that.mm2);
        }

        public int hashCode() {
            return 37 * this.mm1.hashCode() + this.mm2.hashCode();
        }
    }
}

