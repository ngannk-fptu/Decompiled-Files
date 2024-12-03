/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.ToStringASTTransformation"})
public @interface ToString {
    public String[] excludes() default {};

    public String[] includes() default {};

    public boolean includeSuper() default false;

    public boolean includeSuperProperties() default false;

    public boolean includeNames() default false;

    public boolean includeFields() default false;

    public boolean ignoreNulls() default false;

    public boolean includePackage() default true;

    public boolean cache() default false;
}

