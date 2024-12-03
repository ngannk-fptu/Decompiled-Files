/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import edu.umd.cs.findbugs.annotations.When;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Deprecated
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.CLASS)
public @interface OverrideMustInvoke {
    public When value() default When.ANYTIME;
}

