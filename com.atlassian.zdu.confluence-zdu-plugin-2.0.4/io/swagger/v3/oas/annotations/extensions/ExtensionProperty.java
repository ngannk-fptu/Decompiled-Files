/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ExtensionProperty {
    public String name();

    public String value();

    public boolean parseValue() default false;
}

