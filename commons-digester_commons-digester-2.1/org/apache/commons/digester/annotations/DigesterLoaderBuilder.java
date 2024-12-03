/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import org.apache.commons.digester.annotations.DigesterLoader;
import org.apache.commons.digester.annotations.FromAnnotationRuleProviderFactory;
import org.apache.commons.digester.annotations.internal.DefaultAnnotationRuleProviderFactory;
import org.apache.commons.digester.annotations.spi.AnnotationRuleProviderFactory;

public final class DigesterLoaderBuilder {
    public static DigesterLoader byDefaultFactories() {
        return new DigesterLoaderBuilder().useDefaultAnnotationRuleProviderFactory().useDefaultDigesterLoaderHandlerFactory();
    }

    public FromAnnotationRuleProviderFactory useDefaultAnnotationRuleProviderFactory() {
        return this.useAnnotationRuleProviderFactory(new DefaultAnnotationRuleProviderFactory());
    }

    public FromAnnotationRuleProviderFactory useAnnotationRuleProviderFactory(AnnotationRuleProviderFactory annotationRuleProviderFactory) {
        if (annotationRuleProviderFactory == null) {
            throw new IllegalArgumentException("Parameter 'annotationRuleProviderFactory' must be not null");
        }
        return new FromAnnotationRuleProviderFactory(annotationRuleProviderFactory);
    }
}

