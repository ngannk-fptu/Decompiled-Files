/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.rest.v1.validation;

import com.atlassian.audit.rest.v1.validation.QueryParamValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Validator {
    public Class<? extends QueryParamValidator> value();
}

