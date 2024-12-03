/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.java.ao.RawEntity;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ManyToMany {
    public Class<? extends RawEntity<?>> value();

    public String reverse() default "";

    public String through() default "";

    public String where() default "";
}

