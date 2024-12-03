/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {
    @AliasFor(value="code")
    public HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

    @AliasFor(value="value")
    public HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

    public String reason() default "";
}

