/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.oracle.webservices.api.databinding;

import com.oracle.webservices.api.databinding.DatabindingModeFeature;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id="", bean=DatabindingModeFeature.class)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface DatabindingMode {
    public String value();
}

