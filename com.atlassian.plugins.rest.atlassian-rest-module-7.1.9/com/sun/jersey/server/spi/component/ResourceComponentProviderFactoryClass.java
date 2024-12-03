/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceComponentProviderFactoryClass {
    public Class<? extends ResourceComponentProviderFactory> value();
}

