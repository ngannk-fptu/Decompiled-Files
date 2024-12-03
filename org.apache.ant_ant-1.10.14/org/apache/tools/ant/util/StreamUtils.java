/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {
    public static <T> Stream<T> enumerationAsStream(final Enumeration<T> e) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 16){

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if (e.hasMoreElements()) {
                    action.accept(e.nextElement());
                    return true;
                }
                return false;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                while (e.hasMoreElements()) {
                    action.accept(e.nextElement());
                }
            }
        }, false);
    }

    public static <T> Stream<T> iteratorAsStream(Iterator<T> i) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(i, 16), false);
    }
}

