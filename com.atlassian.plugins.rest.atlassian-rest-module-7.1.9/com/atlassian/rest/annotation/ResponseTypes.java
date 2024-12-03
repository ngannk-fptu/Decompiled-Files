/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.rest.annotation;

import com.atlassian.rest.annotation.ResponseType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface ResponseTypes {
    public ResponseType[] value();
}

