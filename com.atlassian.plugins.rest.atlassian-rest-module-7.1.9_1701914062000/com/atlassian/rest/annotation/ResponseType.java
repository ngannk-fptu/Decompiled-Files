/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface ResponseType {
    public int status() default 0;

    public StatusType statusType() default StatusType.SUCCESS;

    public Class<?> value();

    public Class<?>[] genericTypes() default {};

    public static enum StatusType {
        SUCCESS("2\\d\\d"),
        REDIRECTION("3\\d\\d"),
        CLIENT_ERROR("4\\d\\d"),
        SERVER_ERROR("5\\d\\d");

        private final String pattern;

        private StatusType(String pattern) {
            this.pattern = pattern;
        }

        public boolean matches(int status) {
            return String.valueOf(status).matches(this.pattern);
        }
    }
}

