/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import edu.umd.cs.findbugs.annotations.Confidence;
import edu.umd.cs.findbugs.annotations.Priority;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.CLASS)
@Deprecated
public @interface DefaultAnnotationForMethods {
    public Class<? extends Annotation>[] value();

    @Deprecated
    public Priority priority() default Priority.MEDIUM;

    public Confidence confidence() default Confidence.MEDIUM;
}

