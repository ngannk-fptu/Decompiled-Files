/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.metatype.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.osgi.service.metatype.annotations.Icon;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface ObjectClassDefinition {
    public String id() default "";

    public String name() default "";

    public String description() default "";

    public String localization() default "";

    public String[] pid() default {};

    public String[] factoryPid() default {};

    public Icon[] icon() default {};
}

