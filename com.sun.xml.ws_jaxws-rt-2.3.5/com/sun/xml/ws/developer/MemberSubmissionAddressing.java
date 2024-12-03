/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="http://java.sun.com/xml/ns/jaxws/2004/08/addressing", bean=MemberSubmissionAddressingFeature.class)
public @interface MemberSubmissionAddressing {
    public boolean enabled() default true;

    public boolean required() default false;

    public Validation validation() default Validation.LAX;

    public static enum Validation {
        LAX,
        STRICT;

    }
}

