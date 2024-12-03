/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;

final class AnnotatedValueIterator<E>
implements Iterator<AnnotatedValue<E>>,
BoxedValue<Iterator<E>> {
    private final Iterator<E> boxedIterator;
    private final Collection<Annotation> annotations;

    public AnnotatedValueIterator(Iterator<E> iterator, Collection<Annotation> annotations) {
        this.boxedIterator = (Iterator)Preconditions.checkNotNull(iterator, (Object)"iterator must not be null");
        this.annotations = ImmutableSet.copyOf(annotations);
    }

    @Override
    public boolean hasNext() {
        return this.boxedIterator.hasNext();
    }

    @Override
    public AnnotatedValue<E> next() {
        return new AnnotatedValue<E>(this.boxedIterator.next(), this.annotations);
    }

    @Override
    public void remove() {
        this.boxedIterator.remove();
    }

    @Override
    public Iterator<E> unbox() {
        return this.boxedIterator;
    }
}

