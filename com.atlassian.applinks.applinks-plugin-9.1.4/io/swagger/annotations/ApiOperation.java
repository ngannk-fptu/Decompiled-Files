/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Authorization;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.ResponseHeader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ApiOperation {
    public String value();

    public String notes() default "";

    public String[] tags() default {""};

    public Class<?> response() default Void.class;

    public String responseContainer() default "";

    public String responseReference() default "";

    public String httpMethod() default "";

    @Deprecated
    public int position() default 0;

    public String nickname() default "";

    public String produces() default "";

    public String consumes() default "";

    public String protocols() default "";

    public Authorization[] authorizations() default {@Authorization(value="")};

    public boolean hidden() default false;

    public ResponseHeader[] responseHeaders() default {@ResponseHeader(name="", response=Void.class)};

    public int code() default 200;

    public Extension[] extensions() default {@Extension(properties={@ExtensionProperty(name="", value="")})};

    public boolean ignoreJsonView() default false;
}

