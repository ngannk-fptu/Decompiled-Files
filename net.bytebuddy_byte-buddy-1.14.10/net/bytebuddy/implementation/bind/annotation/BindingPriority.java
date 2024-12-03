/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface BindingPriority {
    public static final int DEFAULT = 1;

    public int value();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Resolver implements MethodDelegationBinder.AmbiguityResolver
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape VALUE;

        private static int resolve(@MaybeNull AnnotationDescription.Loadable<BindingPriority> bindingPriority) {
            return bindingPriority == null ? 1 : bindingPriority.getValue(VALUE).resolve(Integer.class);
        }

        @Override
        public MethodDelegationBinder.AmbiguityResolver.Resolution resolve(MethodDescription source, MethodDelegationBinder.MethodBinding left, MethodDelegationBinder.MethodBinding right) {
            int rightPriority;
            int leftPriority = Resolver.resolve(left.getTarget().getDeclaredAnnotations().ofType(BindingPriority.class));
            if (leftPriority == (rightPriority = Resolver.resolve(right.getTarget().getDeclaredAnnotations().ofType(BindingPriority.class)))) {
                return MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
            }
            if (leftPriority < rightPriority) {
                return MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
            }
            return MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
        }

        static {
            VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(BindingPriority.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
        }
    }
}

