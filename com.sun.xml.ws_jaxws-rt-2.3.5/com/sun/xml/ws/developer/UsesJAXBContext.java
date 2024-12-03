/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.developer.JAXBContextFactory;
import com.sun.xml.ws.developer.UsesJAXBContextFeature;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/uses-jaxb-context", bean=UsesJAXBContextFeature.class)
public @interface UsesJAXBContext {
    public Class<? extends JAXBContextFactory> value();
}

