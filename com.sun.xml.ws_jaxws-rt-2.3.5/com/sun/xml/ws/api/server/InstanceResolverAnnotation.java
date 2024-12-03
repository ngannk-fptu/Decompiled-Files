/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.server;

import com.sun.xml.ws.api.server.InstanceResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface InstanceResolverAnnotation {
    public Class<? extends InstanceResolver> value();
}

