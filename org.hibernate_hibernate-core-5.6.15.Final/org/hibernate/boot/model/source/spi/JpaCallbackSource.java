/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.lang.annotation.Annotation;

public interface JpaCallbackSource {
    public String getCallbackMethod(Class<? extends Annotation> var1);

    public String getName();

    public boolean isListener();
}

