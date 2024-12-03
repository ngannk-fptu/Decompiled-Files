/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.annotation.Persistent;

@Documented
@Inherited
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Persistent
public @interface TypeAlias {
    public String value();
}

