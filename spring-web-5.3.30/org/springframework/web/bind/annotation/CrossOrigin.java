/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {
    @Deprecated
    public static final String[] DEFAULT_ORIGINS = new String[]{"*"};
    @Deprecated
    public static final String[] DEFAULT_ALLOWED_HEADERS = new String[]{"*"};
    @Deprecated
    public static final boolean DEFAULT_ALLOW_CREDENTIALS = false;
    @Deprecated
    public static final long DEFAULT_MAX_AGE = 1800L;

    @AliasFor(value="origins")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] origins() default {};

    public String[] originPatterns() default {};

    public String[] allowedHeaders() default {};

    public String[] exposedHeaders() default {};

    public RequestMethod[] methods() default {};

    public String allowCredentials() default "";

    public long maxAge() default -1L;
}

