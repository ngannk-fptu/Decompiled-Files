/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Target(value={ElementType.PACKAGE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.ConditionalInterruptibleASTTransformation"})
public @interface ConditionalInterrupt {
    public boolean applyToAllClasses() default true;

    public boolean applyToAllMembers() default true;

    public boolean checkOnMethodStart() default true;

    public Class thrown() default InterruptedException.class;

    public Class value();
}

