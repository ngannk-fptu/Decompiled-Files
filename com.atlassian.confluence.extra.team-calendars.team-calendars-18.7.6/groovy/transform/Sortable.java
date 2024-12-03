/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.SortableASTTransformation"})
public @interface Sortable {
    public String[] includes() default {};

    public String[] excludes() default {};
}

