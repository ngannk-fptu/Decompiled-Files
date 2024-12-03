/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.metatype.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Option;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
public @interface AttributeDefinition {
    public String name() default "";

    public String description() default "";

    public AttributeType type() default AttributeType.STRING;

    public int cardinality() default 0;

    public String min() default "";

    public String max() default "";

    public String[] defaultValue() default {};

    public boolean required() default true;

    public Option[] options() default {};
}

