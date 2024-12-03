/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
public @interface MBeanInfo {
    public String value();
}

