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
public @interface Grab {
    public String group() default "";

    public String module() default "";

    public String version() default "";

    public String classifier() default "";

    public boolean transitive() default true;

    public boolean force() default false;

    public boolean changing() default false;

    public String conf() default "";

    public String ext() default "";

    public String type() default "";

    public String value() default "";

    public boolean initClass() default true;
}

