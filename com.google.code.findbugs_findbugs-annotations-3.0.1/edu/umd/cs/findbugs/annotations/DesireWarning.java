/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import edu.umd.cs.findbugs.annotations.Confidence;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.CLASS)
public @interface DesireWarning {
    public String value();

    public Confidence confidence() default Confidence.LOW;

    public int rank() default 20;

    public int num() default 1;
}

