/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface BndPlugin {
    public String name();

    public Class<?> parameters() default Object.class;
}

