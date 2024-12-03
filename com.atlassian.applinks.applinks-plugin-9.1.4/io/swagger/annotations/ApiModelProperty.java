/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ApiModelProperty {
    public String value() default "";

    public String name() default "";

    public String allowableValues() default "";

    public String access() default "";

    public String notes() default "";

    public String dataType() default "";

    public boolean required() default false;

    public int position() default 0;

    public boolean hidden() default false;

    public String example() default "";

    @Deprecated
    public boolean readOnly() default false;

    public AccessMode accessMode() default AccessMode.AUTO;

    public String reference() default "";

    public boolean allowEmptyValue() default false;

    public Extension[] extensions() default {@Extension(properties={@ExtensionProperty(name="", value="")})};

    public static enum AccessMode {
        AUTO,
        READ_ONLY,
        READ_WRITE;

    }
}

