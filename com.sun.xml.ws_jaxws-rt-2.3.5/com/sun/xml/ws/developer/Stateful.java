/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.api.server.InstanceResolverAnnotation;
import com.sun.xml.ws.developer.StatefulFeature;
import com.sun.xml.ws.server.StatefulInstanceResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/stateful", bean=StatefulFeature.class)
@InstanceResolverAnnotation(value=StatefulInstanceResolver.class)
public @interface Stateful {
}

