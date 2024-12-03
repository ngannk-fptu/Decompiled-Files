/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.server.impl.container.servlet.PerSessionFactory;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactoryClass;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@ResourceComponentProviderFactoryClass(value=PerSessionFactory.class)
public @interface PerSession {
}

