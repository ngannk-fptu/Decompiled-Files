/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.httpmethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface AllowedHttpMethod {
    public HttpMethod[] value() default {};
}

