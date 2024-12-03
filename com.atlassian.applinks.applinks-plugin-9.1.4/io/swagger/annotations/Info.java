/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.License;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Info {
    public String title();

    public String version();

    public String description() default "";

    public String termsOfService() default "";

    public Contact contact() default @Contact(name="");

    public License license() default @License(name="");

    public Extension[] extensions() default {@Extension(properties={@ExtensionProperty(name="", value="")})};
}

