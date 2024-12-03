/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.responses;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(value=ApiResponses.class)
public @interface ApiResponse {
    public String description() default "";

    public String responseCode() default "default";

    public Header[] headers() default {};

    public Link[] links() default {};

    public Content[] content() default {};

    public Extension[] extensions() default {};

    public String ref() default "";
}

