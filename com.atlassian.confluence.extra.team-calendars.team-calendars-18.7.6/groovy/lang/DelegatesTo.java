/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={ElementType.PARAMETER})
public @interface DelegatesTo {
    public Class value() default Target.class;

    public int strategy() default 0;

    public int genericTypeIndex() default -1;

    public String target() default "";

    public String type() default "";

    @Retention(value=RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(value={ElementType.PARAMETER})
    public static @interface Target {
        public String value() default "";
    }
}

