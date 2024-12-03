/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import groovy.lang.Script;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.LOCAL_VARIABLE, ElementType.PACKAGE, ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.BaseScriptASTTransformation"})
public @interface BaseScript {
    public Class value() default Script.class;
}

