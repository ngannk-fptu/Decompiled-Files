/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.plugin.webresource.util;

import com.atlassian.annotations.Internal;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import java.util.Iterator;
import java.util.function.Predicate;

@Internal
public abstract class ConsList<A>
implements Iterable<A> {
    public static <A> ConsList<A> empty() {
        return new Empty();
    }

    public static <A> ConsList<A> of(A ... as) {
        ConsList<A> result = ConsList.empty();
        for (A a : as) {
            result = result.prepend(a);
        }
        return result.reverse();
    }

    public abstract Option<A> head();

    public abstract Option<ConsList<A>> tail();

    public abstract boolean isEmpty();

    public ConsList<A> prepend(A a) {
        return new Node<A>(a, this.asOption());
    }

    public <B> B foldLeft(B zero, Function<Pair<B, A>, B> append) {
        Object result = zero;
        for (A a : this) {
            result = append.apply((Object)Pair.pair(result, a));
        }
        return result;
    }

    public ConsList<A> remove(final A a) {
        com.google.common.base.Predicate isRemove = new com.google.common.base.Predicate<A>(){

            public boolean apply(A input) {
                return input == a;
            }
        };
        ConsList result = new Empty<Object>();
        Option<ConsList<A>> next = this.asOption();
        while (next.isDefined()) {
            ConsList cell = (ConsList)next.get();
            Option<A> head = cell.head();
            if (!head.exists((Predicate)isRemove)) {
                result = result.prepend(head.get());
            }
            next = cell.tail();
        }
        return result.reverse();
    }

    @Override
    public Iterator<A> iterator() {
        return new Iterator<A>(){
            private Option<ConsList<A>> next;
            {
                this.next = ConsList.this.asOption();
            }

            @Override
            public boolean hasNext() {
                return this.next.isDefined();
            }

            @Override
            public A next() {
                ConsList n = (ConsList)this.next.get();
                try {
                    Object object = n.head().get();
                    return object;
                }
                finally {
                    this.next = n.tail();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    abstract Option<ConsList<A>> asOption();

    ConsList<A> reverse() {
        return this.foldLeft(ConsList.empty(), new Function<Pair<ConsList<A>, A>, ConsList<A>>(){

            public ConsList<A> apply(Pair<ConsList<A>, A> input) {
                return ((ConsList)input.left()).prepend(input.right());
            }
        });
    }

    public String toString() {
        return Iterables.toString((Iterable)this);
    }

    static final class Empty<A>
    extends ConsList<A> {
        Empty() {
        }

        @Override
        public Option<A> head() {
            return Option.none();
        }

        @Override
        public Option<ConsList<A>> tail() {
            return Option.none();
        }

        @Override
        public ConsList<A> remove(A a) {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        Option<ConsList<A>> asOption() {
            return Option.none();
        }
    }

    static final class Node<A>
    extends ConsList<A> {
        private final A head;
        private Option<ConsList<A>> tail;

        Node(A head, Option<ConsList<A>> tail) {
            this.head = Preconditions.checkNotNull(head);
            this.tail = tail;
        }

        @Override
        public final Option<A> head() {
            return Option.some(this.head);
        }

        @Override
        public final Option<ConsList<A>> tail() {
            return this.tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        Option<ConsList<A>> asOption() {
            return Option.some((Object)this);
        }
    }
}

