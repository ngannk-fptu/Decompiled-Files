/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.handlers.DefaultLoaderHandler;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface DigesterRule {
    public Class<? extends Rule> reflectsRule();

    public Class<? extends DigesterLoaderHandler<? extends Annotation, ? extends AnnotatedElement>> handledBy() default DefaultLoaderHandler.class;

    public Class<? extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> providedBy();
}

