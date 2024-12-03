/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Target(value={ElementType.FIELD})
@GroovyASTTransformationClass(value={"groovy.beans.ListenerListASTTransformation"})
public @interface ListenerList {
    public String name() default "";

    public boolean synchronize() default false;
}

