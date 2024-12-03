/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface ConfInfo {
    public String elementName() default "";

    public String elementType() default "java.lang.String";

    public String collectionElementName() default "";

    public boolean dontSave() default false;

    public String type() default "";
}

