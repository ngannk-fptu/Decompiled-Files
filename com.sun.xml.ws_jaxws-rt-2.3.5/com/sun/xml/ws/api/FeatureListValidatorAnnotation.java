/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api;

import com.sun.xml.ws.api.FeatureListValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface FeatureListValidatorAnnotation {
    public Class<? extends FeatureListValidator> bean();
}

