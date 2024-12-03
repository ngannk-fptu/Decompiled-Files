/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer.servlet;

import com.sun.xml.ws.api.server.InstanceResolverAnnotation;
import com.sun.xml.ws.developer.servlet.HttpSessionScopeFeature;
import com.sun.xml.ws.server.servlet.HttpSessionInstanceResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/servlet/httpSessionScope", bean=HttpSessionScopeFeature.class)
@InstanceResolverAnnotation(value=HttpSessionInstanceResolver.class)
public @interface HttpSessionScope {
}

