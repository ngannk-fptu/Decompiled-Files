/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface AnnotationCollector {
    public String processor() default "org.codehaus.groovy.transform.AnnotationCollectorTransform";

    public Class[] value() default {};
}

