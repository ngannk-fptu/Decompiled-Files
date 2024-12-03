/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import org.apache.commons.digester.annotations.DigesterLoader;
import org.apache.commons.digester.annotations.internal.DefaultDigesterLoaderHandlerFactory;
import org.apache.commons.digester.annotations.spi.AnnotationRuleProviderFactory;
import org.apache.commons.digester.annotations.spi.DigesterLoaderHandlerFactory;

public final class FromAnnotationRuleProviderFactory {
    private final AnnotationRuleProviderFactory annotationRuleProviderFactory;

    protected FromAnnotationRuleProviderFactory(AnnotationRuleProviderFactory annotationRuleProviderFactory) {
        this.annotationRuleProviderFactory = annotationRuleProviderFactory;
    }

    public DigesterLoader useDefaultDigesterLoaderHandlerFactory() {
        return this.useDigesterLoaderHandlerFactory(new DefaultDigesterLoaderHandlerFactory());
    }

    public DigesterLoader useDigesterLoaderHandlerFactory(DigesterLoaderHandlerFactory digesterLoaderHandlerFactory) {
        if (digesterLoaderHandlerFactory == null) {
            throw new IllegalArgumentException("Parameter 'digesterLoaderHandlerFactory' must be not null");
        }
        return new DigesterLoader(this.annotationRuleProviderFactory, digesterLoaderHandlerFactory);
    }
}

