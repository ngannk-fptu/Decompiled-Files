/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 */
package com.atlassian.plugin.event.impl;

import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.impl.ListenerMethodSelector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationListenerMethodSelector
implements ListenerMethodSelector {
    private final Class<? extends Annotation> markerAnnotation;

    public AnnotationListenerMethodSelector() {
        this(PluginEventListener.class);
    }

    public AnnotationListenerMethodSelector(Class<? extends Annotation> markerAnnotation) {
        this.markerAnnotation = markerAnnotation;
    }

    @Override
    public boolean isListenerMethod(Method method) {
        return method.getAnnotation(this.markerAnnotation) != null;
    }
}

