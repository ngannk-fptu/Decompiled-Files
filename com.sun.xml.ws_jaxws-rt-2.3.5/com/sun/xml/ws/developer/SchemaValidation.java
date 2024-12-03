/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.developer.SchemaValidationFeature;
import com.sun.xml.ws.developer.ValidationErrorHandler;
import com.sun.xml.ws.server.DraconianValidationErrorHandler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/schema-validation", bean=SchemaValidationFeature.class)
public @interface SchemaValidation {
    public Class<? extends ValidationErrorHandler> handler() default DraconianValidationErrorHandler.class;

    public boolean inbound() default true;

    public boolean outbound() default true;
}

