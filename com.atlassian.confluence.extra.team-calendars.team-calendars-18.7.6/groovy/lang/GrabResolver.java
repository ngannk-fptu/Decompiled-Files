/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface GrabResolver {
    public String value() default "";

    public String name() default "";

    public String root() default "";

    public boolean m2Compatible() default true;

    public boolean initClass() default true;
}

