/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minidev.json.annotate.JsonSmartAnnotation;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@JsonSmartAnnotation
public @interface JsonIgnore {
    public boolean value() default true;
}

