/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

import com.atlassian.plugins.rest.common.multipart.MultipartConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface MultipartConfigClass {
    public Class<? extends MultipartConfig> value();
}

