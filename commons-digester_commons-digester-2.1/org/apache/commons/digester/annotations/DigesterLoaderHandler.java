/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface DigesterLoaderHandler<A extends Annotation, E extends AnnotatedElement> {
    public void handle(A var1, E var2, FromAnnotationsRuleSet var3);
}

