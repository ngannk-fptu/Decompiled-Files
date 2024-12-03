/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
public @interface Reference {
    public static final String RNAME = "LaQute/bnd/annotation/component/Reference;";
    public static final String NAME = "name";
    public static final String SERVICE = "service";
    public static final String OPTIONAL = "optional";
    public static final String MULTIPLE = "multiple";
    public static final String DYNAMIC = "dynamic";
    public static final String TARGET = "target";
    public static final String TYPE = "type";
    public static final String UNBIND = "unbind";

    public String name() default "";

    public Class<?> service() default Object.class;

    public boolean optional() default false;

    public boolean multiple() default false;

    public boolean dynamic() default false;

    public String target() default "";

    public String unbind() default "";

    public char type() default 0;
}

