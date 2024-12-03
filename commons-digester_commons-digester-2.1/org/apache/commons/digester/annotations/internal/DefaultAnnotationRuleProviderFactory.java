/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.DigesterLoadingException;
import org.apache.commons.digester.annotations.spi.AnnotationRuleProviderFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DefaultAnnotationRuleProviderFactory
implements AnnotationRuleProviderFactory {
    @Override
    public <T extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> T newInstance(Class<T> type) throws DigesterLoadingException {
        try {
            return (T)((AnnotationRuleProvider)type.newInstance());
        }
        catch (Exception e) {
            throw new DigesterLoadingException("An error occurred while creating '" + type + "' instance", e);
        }
    }
}

