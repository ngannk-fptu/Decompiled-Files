/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import groovy.transform.TypeCheckingMode;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.sc.StaticCompileTransformation"})
public @interface CompileStatic {
    public TypeCheckingMode value() default TypeCheckingMode.PASS;

    public String[] extensions() default {};
}

