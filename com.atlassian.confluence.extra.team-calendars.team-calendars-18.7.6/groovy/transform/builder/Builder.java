/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.builder;

import groovy.transform.Undefined;
import groovy.transform.builder.DefaultStrategy;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.BuilderASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.BuilderASTTransformation"})
public @interface Builder {
    public Class forClass() default Undefined.CLASS.class;

    public Class<? extends BuilderASTTransformation.BuilderStrategy> builderStrategy() default DefaultStrategy.class;

    public String prefix() default "<DummyUndefinedMarkerString-DoNotUse>";

    public String builderClassName() default "<DummyUndefinedMarkerString-DoNotUse>";

    public String buildMethodName() default "<DummyUndefinedMarkerString-DoNotUse>";

    public String builderMethodName() default "<DummyUndefinedMarkerString-DoNotUse>";

    public String[] excludes() default {};

    public String[] includes() default {};
}

