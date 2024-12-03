/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.matcher.ElementMatcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FilterableList<T, S extends FilterableList<T, S>>
extends List<T> {
    public S filter(ElementMatcher<? super T> var1);

    public T getOnly();

    public S subList(int var1, int var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<T, S extends FilterableList<T, S>>
    extends AbstractList<T>
    implements FilterableList<T, S> {
        private static final int ONLY = 0;

        @Override
        public S filter(ElementMatcher<? super T> elementMatcher) {
            ArrayList filteredElements = new ArrayList(this.size());
            for (Object value : this) {
                if (!elementMatcher.matches(value)) continue;
                filteredElements.add(value);
            }
            return (S)(filteredElements.size() == this.size() ? this : this.wrap(filteredElements));
        }

        @Override
        public T getOnly() {
            if (this.size() != 1) {
                throw new IllegalStateException("size = " + this.size());
            }
            return (T)this.get(0);
        }

        @Override
        public S subList(int fromIndex, int toIndex) {
            return this.wrap(super.subList(fromIndex, toIndex));
        }

        protected abstract S wrap(List<T> var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty<T, S extends FilterableList<T, S>>
    extends AbstractList<T>
    implements FilterableList<T, S> {
        @Override
        public T get(int index) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public T getOnly() {
            throw new IllegalStateException("size = 0");
        }

        @Override
        public S filter(ElementMatcher<? super T> elementMatcher) {
            return (S)this;
        }

        @Override
        public S subList(int fromIndex, int toIndex) {
            if (fromIndex == toIndex && toIndex == 0) {
                return (S)this;
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
    }
}

