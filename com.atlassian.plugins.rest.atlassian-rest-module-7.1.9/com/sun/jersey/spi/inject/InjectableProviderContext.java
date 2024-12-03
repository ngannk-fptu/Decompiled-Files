/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.util.List;

public interface InjectableProviderContext {
    public boolean isAnnotationRegistered(Class<? extends Annotation> var1, Class<?> var2);

    public boolean isInjectableProviderRegistered(Class<? extends Annotation> var1, Class<?> var2, ComponentScope var3);

    public <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> var1, ComponentContext var2, A var3, C var4, ComponentScope var5);

    public <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> var1, ComponentContext var2, A var3, C var4, List<ComponentScope> var5);

    public <A extends Annotation, C> InjectableScopePair getInjectableWithScope(Class<? extends Annotation> var1, ComponentContext var2, A var3, C var4, List<ComponentScope> var5);

    public static final class InjectableScopePair {
        public final Injectable i;
        public final ComponentScope cs;

        public InjectableScopePair(Injectable i, ComponentScope cs) {
            this.i = i;
            this.cs = cs;
        }
    }
}

