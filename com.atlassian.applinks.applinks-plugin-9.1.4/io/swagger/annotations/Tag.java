/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.ExternalDocs;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Tag {
    public String name();

    public String description() default "";

    public ExternalDocs externalDocs() default @ExternalDocs(url="");

    public Extension[] extensions() default {@Extension(properties={@ExtensionProperty(name="", value="")})};
}

