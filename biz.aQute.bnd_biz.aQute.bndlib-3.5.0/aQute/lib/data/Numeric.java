/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Numeric {
    public long min() default -9223372036854775808L;

    public long max() default 0x7FFFFFFFFFFFFFFFL;

    public String reason() default "";
}

