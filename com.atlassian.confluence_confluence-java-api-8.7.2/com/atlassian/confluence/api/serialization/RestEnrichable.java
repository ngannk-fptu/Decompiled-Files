/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.serialization;

import com.atlassian.annotations.ExperimentalApi;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ExperimentalApi
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RestEnrichable {

    public static class Helper {
        public static boolean isAnnotationOnClass(Object obj) {
            return obj != null && obj.getClass().isAnnotationPresent(RestEnrichable.class);
        }
    }
}

