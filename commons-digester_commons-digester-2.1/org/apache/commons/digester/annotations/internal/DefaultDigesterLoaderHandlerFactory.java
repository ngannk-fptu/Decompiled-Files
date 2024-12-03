/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.DigesterLoadingException;
import org.apache.commons.digester.annotations.spi.DigesterLoaderHandlerFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DefaultDigesterLoaderHandlerFactory
implements DigesterLoaderHandlerFactory {
    @Override
    public <L extends DigesterLoaderHandler<? extends Annotation, ? extends AnnotatedElement>> L newInstance(Class<L> type) throws DigesterLoadingException {
        try {
            return (L)((DigesterLoaderHandler)type.newInstance());
        }
        catch (Exception e) {
            throw new DigesterLoadingException("An error occurred while creating '" + type + "' instance", e);
        }
    }
}

