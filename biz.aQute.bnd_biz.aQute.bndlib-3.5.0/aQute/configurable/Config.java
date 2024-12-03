/*
 * Decompiled with CFR 0.152.
 */
package aQute.configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Config {
    public static final String NULL = "<<NULL>>";

    public boolean required() default false;

    public String description() default "";

    public String deflt() default "<<NULL>>";

    public String id() default "<<NULL>>";
}

