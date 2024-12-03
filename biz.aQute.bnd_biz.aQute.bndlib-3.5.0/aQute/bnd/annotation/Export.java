/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.PACKAGE})
public @interface Export {
    public static final String MANDATORY = "mandatory";
    public static final String OPTIONAL = "optional";
    public static final String USES = "uses";
    public static final String EXCLUDE = "exclude";
    public static final String INCLUDE = "include";

    public String[] mandatory() default {""};

    public String[] optional() default {""};

    public Class<?>[] exclude() default {Object.class};

    public Class<?>[] include() default {Object.class};
}

