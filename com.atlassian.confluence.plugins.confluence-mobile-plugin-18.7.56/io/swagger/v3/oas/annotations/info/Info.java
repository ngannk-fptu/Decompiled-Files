/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.info;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Info {
    public String title() default "";

    public String description() default "";

    public String termsOfService() default "";

    public Contact contact() default @Contact;

    public License license() default @License;

    public String version() default "";

    public Extension[] extensions() default {};

    @OpenAPI31
    public String summary() default "";
}

