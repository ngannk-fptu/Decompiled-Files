/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.DigesterLoadingException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotationRuleProviderFactory {
    public <T extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> T newInstance(Class<T> var1) throws DigesterLoadingException;
}

