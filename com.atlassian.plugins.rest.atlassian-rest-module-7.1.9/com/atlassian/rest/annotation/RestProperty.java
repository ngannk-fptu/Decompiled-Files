/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
public @interface RestProperty {
    public Scope scope() default Scope.AUTO;

    public String pattern() default ".+";

    public String description() default "";

    public boolean required() default false;

    public static enum Scope {
        REQUEST,
        RESPONSE,
        BOTH,
        AUTO;

        private static final List<String> responseScopeByDefault;

        public boolean contains(Scope scope) {
            return this == BOTH || this == AUTO || this == scope;
        }

        public boolean includes(Scope fieldScope, String fieldName) {
            return (fieldScope = Scope.resolveAuto(fieldScope, fieldName)) == BOTH || fieldScope == this || this == BOTH || this == AUTO;
        }

        private static Scope resolveAuto(Scope scope, String fieldName) {
            if (scope == AUTO) {
                return responseScopeByDefault.contains(fieldName) ? RESPONSE : BOTH;
            }
            return scope;
        }

        static {
            responseScopeByDefault = Collections.unmodifiableList(Arrays.asList("self", "expand"));
        }
    }
}

