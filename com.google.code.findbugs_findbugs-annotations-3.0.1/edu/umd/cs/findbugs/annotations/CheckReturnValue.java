/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import edu.umd.cs.findbugs.annotations.Confidence;
import edu.umd.cs.findbugs.annotations.Priority;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(value=RetentionPolicy.CLASS)
@Deprecated
public @interface CheckReturnValue {
    @Deprecated
    public Priority priority() default Priority.MEDIUM;

    public Confidence confidence() default Confidence.MEDIUM;

    public String explanation() default "";
}

