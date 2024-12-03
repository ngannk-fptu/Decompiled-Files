/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.spring.scanner.annotation.imports;

import com.atlassian.plugin.spring.scanner.ProductFilter;
import com.atlassian.plugin.spring.scanner.annotation.OnlyInProduct;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@ComponentImport
@OnlyInProduct(value=ProductFilter.REFAPP)
public @interface RefappImport {
    public String value() default "";
}

