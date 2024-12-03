/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.arc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.external.arc.Stability;

@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PACKAGE})
public @interface Taxonomy {
    public Stability stability() default Stability.UNSPECIFIED;

    public String description() default "";
}

