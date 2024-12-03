/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.iterator;

import java.util.Collection;
import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.jackrabbit.spi.commons.iterator.Predicate;
import org.apache.jackrabbit.spi.commons.iterator.Transformer;

public final class Iterators {
    private Iterators() {
    }

    public static <T> Iterator<T> singleton(T element) {
        return new SingletonIterator<T>(element);
    }

    public static <T> Iterator<T> empty() {
        return EmptyIterator.emptyIterator();
    }

    public static <T> Iterator<T> iteratorChain(Iterator<? extends T> iterator1, Iterator<? extends T> iterator2) {
        return new IteratorChain<T>(iterator1, iterator2);
    }

    public static <T> Iterator<T> iteratorChain(Iterator<? extends T>[] iterators) {
        return new IteratorChain<T>(iterators);
    }

    public static <T> Iterator<T> iteratorChain(Collection<Iterator<? extends T>> iterators) {
        return new IteratorChain(iterators);
    }

    public static <T> Iterator<T> arrayIterator(T[] values, int from, int to) {
        return new ArrayIterator(values, from, to);
    }

    public static <T> Iterator<T> filterIterator(Iterator<? extends T> iterator, final Predicate<? super T> predicate) {
        return new FilterIterator<T>(iterator, new org.apache.commons.collections4.Predicate<T>(){

            @Override
            public boolean evaluate(T object) {
                return predicate.evaluate(object);
            }
        });
    }

    public static <T> Iterator<T> filterIterator(Iterator<? extends T> iterator, final java.util.function.Predicate<? super T> predicate) {
        return new FilterIterator<T>(iterator, new org.apache.commons.collections4.Predicate<T>(){

            @Override
            public boolean evaluate(T object) {
                return predicate.test(object);
            }
        });
    }

    public static <S, R> Iterator<R> transformIterator(Iterator<S> iterator, final Transformer<S, R> transformer) {
        org.apache.commons.collections4.Transformer tf = new org.apache.commons.collections4.Transformer<S, R>(){

            @Override
            public R transform(S input) {
                return transformer.transform(input);
            }
        };
        return new TransformIterator(iterator, tf);
    }

    public static Iterator<Property> properties(PropertyIterator propertyIterator) {
        return propertyIterator;
    }

    public static Iterator<Node> nodes(NodeIterator nodeIterator) {
        return nodeIterator;
    }
}

