/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Target(value={ElementType.PACKAGE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.TimedInterruptibleASTTransformation"})
public @interface TimedInterrupt {
    public boolean applyToAllClasses() default true;

    public boolean applyToAllMembers() default true;

    public boolean checkOnMethodStart() default true;

    public long value();

    public TimeUnit unit() default TimeUnit.SECONDS;

    public Class thrown() default TimeoutException.class;
}

