/*
 * Decompiled with CFR 0.152.
 */
package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface WebParam {
    public String name() default "";

    public String partName() default "";

    public String targetNamespace() default "";

    public Mode mode() default Mode.IN;

    public boolean header() default false;

    public static enum Mode {
        IN,
        OUT,
        INOUT;

    }
}

