/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.DelegateASTTransformation"})
public @interface Delegate {
    public boolean interfaces() default true;

    public boolean deprecated() default false;

    public boolean methodAnnotations() default false;

    public boolean parameterAnnotations() default false;

    public String[] excludes() default {};

    public Class[] excludeTypes() default {};

    public String[] includes() default {};

    public Class[] includeTypes() default {};
}

