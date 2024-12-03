/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.jackson.annotate.JacksonAnnotation;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@JacksonAnnotation
@Deprecated
public @interface JsonGetter {
    public String value() default "";
}

