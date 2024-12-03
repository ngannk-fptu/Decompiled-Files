/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.DigesterLoadingException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface DigesterLoaderHandlerFactory {
    public <L extends DigesterLoaderHandler<? extends Annotation, ? extends AnnotatedElement>> L newInstance(Class<L> var1) throws DigesterLoadingException;
}

