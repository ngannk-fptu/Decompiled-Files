/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import groovy.transform.AutoCloneStyle;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.AutoCloneASTTransformation"})
public @interface AutoClone {
    public String[] excludes() default {};

    public boolean includeFields() default false;

    public AutoCloneStyle style() default AutoCloneStyle.CLONE;
}

