/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;

public interface InjectableProvider<A extends Annotation, C> {
    public ComponentScope getScope();

    public Injectable getInjectable(ComponentContext var1, A var2, C var3);
}

