/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.ANNOTATION_TYPE})
public @interface GroovyASTTransformationClass {
    public String[] value() default {};

    public Class[] classes() default {};
}

