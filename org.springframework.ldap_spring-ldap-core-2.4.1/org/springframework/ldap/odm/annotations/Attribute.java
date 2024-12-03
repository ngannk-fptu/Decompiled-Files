/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Attribute {
    public String name() default "";

    public Type type() default Type.STRING;

    public String syntax() default "";

    public boolean readonly() default false;

    public static enum Type {
        STRING,
        BINARY;

    }
}

