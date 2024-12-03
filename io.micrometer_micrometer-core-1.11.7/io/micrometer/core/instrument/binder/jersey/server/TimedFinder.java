/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.annotation.TimedSet;
import io.micrometer.core.instrument.binder.jersey.server.AnnotationFinder;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

class TimedFinder {
    private final AnnotationFinder annotationFinder;

    TimedFinder(AnnotationFinder annotationFinder) {
        this.annotationFinder = annotationFinder;
    }

    Set<Timed> findTimedAnnotations(AnnotatedElement element) {
        Timed t = this.annotationFinder.findAnnotation(element, Timed.class);
        if (t != null) {
            return Collections.singleton(t);
        }
        TimedSet ts = this.annotationFinder.findAnnotation(element, TimedSet.class);
        if (ts != null) {
            return Arrays.stream(ts.value()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}

