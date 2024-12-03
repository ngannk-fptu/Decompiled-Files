/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.support.PropertySourceFactory
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.support.PropertySourceFactory;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value=PropertySources.class)
public @interface PropertySource {
    public String name() default "";

    public String[] value();

    public boolean ignoreResourceNotFound() default false;

    public String encoding() default "";

    public Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;
}

