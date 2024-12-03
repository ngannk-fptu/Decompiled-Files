/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id="http://jax-ws.dev.java.net/features/mime", bean=StreamingAttachmentFeature.class)
public @interface StreamingAttachment {
    public String dir() default "";

    public boolean parseEagerly() default false;

    public long memoryThreshold() default 0x100000L;
}

