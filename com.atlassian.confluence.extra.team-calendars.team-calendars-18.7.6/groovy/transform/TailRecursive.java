/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Target(value={ElementType.METHOD})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.tailrec.TailRecursiveASTTransformation"})
public @interface TailRecursive {
}

