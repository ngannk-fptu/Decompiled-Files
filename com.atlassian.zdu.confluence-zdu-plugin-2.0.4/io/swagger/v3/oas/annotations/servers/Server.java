/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.servers;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.servers.Servers;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Servers.class)
@Inherited
public @interface Server {
    public String url() default "";

    public String description() default "";

    public ServerVariable[] variables() default {};

    public Extension[] extensions() default {};
}

