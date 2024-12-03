/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.ApiImplicitParam;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ApiImplicitParams {
    public ApiImplicitParam[] value();
}

