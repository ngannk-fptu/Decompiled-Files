/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import edu.umd.cs.findbugs.annotations.Confidence;
import edu.umd.cs.findbugs.annotations.Priority;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.CLASS)
public @interface DesireNoWarning {
    public String value();

    @Deprecated
    public Priority priority() default Priority.LOW;

    public Confidence confidence() default Confidence.LOW;

    public int num() default 0;
}

