/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.headers.Header;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Encoding {
    public String name() default "";

    public String contentType() default "";

    public String style() default "";

    public boolean explode() default false;

    public boolean allowReserved() default false;

    public Header[] headers() default {};

    public Extension[] extensions() default {};
}

