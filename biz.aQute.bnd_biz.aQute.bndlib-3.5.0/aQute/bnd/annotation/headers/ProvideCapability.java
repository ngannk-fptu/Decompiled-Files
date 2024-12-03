/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.headers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface ProvideCapability {
    public String value() default "";

    public String ns();

    public String name() default "";

    public String version() default "";

    public String effective() default "resolve";

    public String[] uses() default {};

    public String[] mandatory() default {};
}

