/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Annotations {
    public <A extends Annotation> A get(Class<A> var1);

    public int size();
}

