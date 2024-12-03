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
@Target(value={ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.ast.MixinASTTransformation"})
@Deprecated
public @interface Mixin {
    public Class[] value();
}

