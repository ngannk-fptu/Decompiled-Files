/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.control.CompilePhase;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface GroovyASTTransformation {
    public CompilePhase phase() default CompilePhase.CANONICALIZATION;
}

