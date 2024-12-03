/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.links;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.servers.Server;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Link {
    public String name() default "";

    public String operationRef() default "";

    public String operationId() default "";

    public LinkParameter[] parameters() default {};

    public String description() default "";

    public String requestBody() default "";

    public Server server() default @Server;

    public Extension[] extensions() default {};

    public String ref() default "";
}

