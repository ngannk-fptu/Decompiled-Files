/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.metatype.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface Designate {
    public Class<?> ocd();

    public boolean factory() default false;
}

