/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Authorization;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Api {
    public String value() default "";

    public String[] tags() default {""};

    @Deprecated
    public String description() default "";

    @Deprecated
    public String basePath() default "";

    @Deprecated
    public int position() default 0;

    public String produces() default "";

    public String consumes() default "";

    public String protocols() default "";

    public Authorization[] authorizations() default {@Authorization(value="")};

    public boolean hidden() default false;
}

