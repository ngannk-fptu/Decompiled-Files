/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.spring.scanner.annotation.export;

import com.atlassian.plugin.spring.scanner.annotation.export.ServiceProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ExportAsService {
    public Class<?>[] value() default {};

    public ServiceProperty[] properties() default {};
}

