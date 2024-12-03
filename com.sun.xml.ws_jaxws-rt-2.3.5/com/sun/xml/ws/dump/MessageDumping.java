/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.dump;

import com.sun.xml.ws.dump.MessageDumpingFeature;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id="com.sun.xml.ws.messagedump.MessageDumpingFeature", bean=MessageDumpingFeature.class)
public @interface MessageDumping {
    public boolean enabled() default true;

    public String messageLoggingRoot() default "com.sun.xml.ws.messagedump";

    public String messageLoggingLevel() default "FINE";

    public boolean storeMessages() default false;
}

