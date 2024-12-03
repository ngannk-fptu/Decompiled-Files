/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerDefinition {
    public String host() default "";

    public String basePath() default "";

    public String[] consumes() default {""};

    public String[] produces() default {""};

    public Scheme[] schemes() default {Scheme.DEFAULT};

    public Tag[] tags() default {@Tag(name="")};

    public SecurityDefinition securityDefinition() default @SecurityDefinition;

    public Info info() default @Info(title="", version="");

    public ExternalDocs externalDocs() default @ExternalDocs(url="");

    public static enum Scheme {
        DEFAULT,
        HTTP,
        HTTPS,
        WS,
        WSS;

    }
}

