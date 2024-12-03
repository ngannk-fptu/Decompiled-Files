/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Tag;
import java.lang.invoke.LambdaMetafactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Tags
implements Iterable<Tag> {
    private static final Tags EMPTY = new Tags(new Tag[0]);
    private final Tag[] tags;
    private int last;

    private Tags(Tag[] tags) {
        this.tags = tags;
        Arrays.sort(this.tags);
        this.dedup();
    }

    private void dedup() {
        int n = this.tags.length;
        if (n == 0 || n == 1) {
            this.last = n;
            return;
        }
        int j = 0;
        for (int i = 0; i < n - 1; ++i) {
            if (this.tags[i].getKey().equals(this.tags[i + 1].getKey())) continue;
            this.tags[j++] = this.tags[i];
        }
        this.tags[j++] = this.tags[n - 1];
        this.last = j;
    }

    public Tags and(String key, String value) {
        return this.and(Tag.of(key, value));
    }

    public Tags and(String ... keyValues) {
        if (Tags.blankVarargs(keyValues)) {
            return this;
        }
        return this.and(Tags.of(keyValues));
    }

    public Tags and(Tag ... tags) {
        if (Tags.blankVarargs(tags)) {
            return this;
        }
        Tag[] newTags = new Tag[this.last + tags.length];
        System.arraycopy(this.tags, 0, newTags, 0, this.last);
        System.arraycopy(tags, 0, newTags, this.last, tags.length);
        return new Tags(newTags);
    }

    public Tags and(@Nullable Iterable<? extends Tag> tags) {
        if (tags == null || tags == EMPTY || !tags.iterator().hasNext()) {
            return this;
        }
        if (this.tags.length == 0) {
            return Tags.of(tags);
        }
        return this.and(Tags.of(tags).tags);
    }

    @Override
    public Iterator<Tag> iterator() {
        return new ArrayIterator();
    }

    @Override
    public Spliterator<Tag> spliterator() {
        return Spliterators.spliterator(this.tags, 0, this.last, 1301);
    }

    public Stream<Tag> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.last; ++i) {
            result = 31 * result + this.tags[i].hashCode();
        }
        return result;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && this.getClass() == obj.getClass() && this.tagsEqual((Tags)obj);
    }

    private boolean tagsEqual(Tags obj) {
        if (this.tags == obj.tags) {
            return true;
        }
        if (this.last != obj.last) {
            return false;
        }
        for (int i = 0; i < this.last; ++i) {
            if (this.tags[i].equals(obj.tags[i])) continue;
            return false;
        }
        return true;
    }

    public static Tags concat(@Nullable Iterable<? extends Tag> tags, @Nullable Iterable<? extends Tag> otherTags) {
        return Tags.of(tags).and(otherTags);
    }

    public static Tags concat(@Nullable Iterable<? extends Tag> tags, String ... keyValues) {
        return Tags.of(tags).and(keyValues);
    }

    public static Tags of(@Nullable Iterable<? extends Tag> tags) {
        if (tags == null || tags == EMPTY || !tags.iterator().hasNext()) {
            return Tags.empty();
        }
        if (tags instanceof Tags) {
            return (Tags)tags;
        }
        if (tags instanceof Collection) {
            Collection tagsCollection = (Collection)tags;
            return new Tags(tagsCollection.toArray(new Tag[0]));
        }
        return new Tags((Tag[])StreamSupport.stream(tags.spliterator(), false).toArray(Tag[]::new));
    }

    public static Tags of(String key, String value) {
        return new Tags(new Tag[]{Tag.of(key, value)});
    }

    public static Tags of(String ... keyValues) {
        if (Tags.blankVarargs(keyValues)) {
            return Tags.empty();
        }
        if (keyValues.length % 2 == 1) {
            throw new IllegalArgumentException("size must be even, it is a set of key=value pairs");
        }
        Tag[] tags = new Tag[keyValues.length / 2];
        for (int i = 0; i < keyValues.length; i += 2) {
            tags[i / 2] = Tag.of(keyValues[i], keyValues[i + 1]);
        }
        return new Tags(tags);
    }

    private static boolean blankVarargs(@Nullable Object[] args) {
        return args == null || args.length == 0 || args.length == 1 && args[0] == null;
    }

    public static Tags of(Tag ... tags) {
        return Tags.empty().and(tags);
    }

    public static Tags empty() {
        return EMPTY;
    }

    public String toString() {
        return this.stream().map((Function<Tag, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, toString(), (Lio/micrometer/core/instrument/Tag;)Ljava/lang/String;)()).collect(Collectors.joining(",", "[", "]"));
    }

    private class ArrayIterator
    implements Iterator<Tag> {
        private int currentIndex = 0;

        private ArrayIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.currentIndex < Tags.this.last;
        }

        @Override
        public Tag next() {
            return Tags.this.tags[this.currentIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("cannot remove items from tags");
        }
    }
}

