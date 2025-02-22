/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class Joiner {
    private final String separator;

    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    public static Joiner on(char separator) {
        return new Joiner(String.valueOf(separator));
    }

    private Joiner(String separator) {
        this.separator = Preconditions.checkNotNull(separator);
    }

    private Joiner(Joiner prototype) {
        this.separator = prototype.separator;
    }

    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterable<? extends @Nullable Object> parts) throws IOException {
        return this.appendTo(appendable, parts.iterator());
    }

    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterator<? extends @Nullable Object> parts) throws IOException {
        Preconditions.checkNotNull(appendable);
        if (parts.hasNext()) {
            appendable.append(this.toString(parts.next()));
            while (parts.hasNext()) {
                appendable.append(this.separator);
                appendable.append(this.toString(parts.next()));
            }
        }
        return appendable;
    }

    @CanIgnoreReturnValue
    public final <A extends Appendable> A appendTo(A appendable, @Nullable Object[] parts) throws IOException {
        return this.appendTo(appendable, Arrays.asList(parts));
    }

    @CanIgnoreReturnValue
    public final <A extends Appendable> A appendTo(A appendable, @CheckForNull Object first, @CheckForNull Object second, Object ... rest) throws IOException {
        return this.appendTo(appendable, Joiner.iterable(first, second, rest));
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, Iterable<? extends @Nullable Object> parts) {
        return this.appendTo(builder, parts.iterator());
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, Iterator<? extends @Nullable Object> parts) {
        try {
            this.appendTo((Appendable)builder, parts);
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
        return builder;
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, @Nullable Object[] parts) {
        return this.appendTo(builder, (Iterable<? extends Object>)Arrays.asList(parts));
    }

    @CanIgnoreReturnValue
    public final StringBuilder appendTo(StringBuilder builder, @CheckForNull Object first, @CheckForNull Object second, Object ... rest) {
        return this.appendTo(builder, Joiner.iterable(first, second, rest));
    }

    public final String join(Iterable<? extends @Nullable Object> parts) {
        return this.join(parts.iterator());
    }

    public final String join(Iterator<? extends @Nullable Object> parts) {
        return this.appendTo(new StringBuilder(), parts).toString();
    }

    public final String join(@Nullable Object[] parts) {
        return this.join(Arrays.asList(parts));
    }

    public final String join(@CheckForNull Object first, @CheckForNull Object second, Object ... rest) {
        return this.join(Joiner.iterable(first, second, rest));
    }

    public Joiner useForNull(final String nullText) {
        Preconditions.checkNotNull(nullText);
        return new Joiner(this){

            @Override
            CharSequence toString(@CheckForNull Object part) {
                return part == null ? nullText : Joiner.this.toString(part);
            }

            @Override
            public Joiner useForNull(String nullText2) {
                throw new UnsupportedOperationException("already specified useForNull");
            }

            @Override
            public Joiner skipNulls() {
                throw new UnsupportedOperationException("already specified useForNull");
            }
        };
    }

    public Joiner skipNulls() {
        return new Joiner(this){

            @Override
            public <A extends Appendable> A appendTo(A appendable, Iterator<? extends @Nullable Object> parts) throws IOException {
                Object part;
                Preconditions.checkNotNull(appendable, "appendable");
                Preconditions.checkNotNull(parts, "parts");
                while (parts.hasNext()) {
                    part = parts.next();
                    if (part == null) continue;
                    appendable.append(Joiner.this.toString(part));
                    break;
                }
                while (parts.hasNext()) {
                    part = parts.next();
                    if (part == null) continue;
                    appendable.append(Joiner.this.separator);
                    appendable.append(Joiner.this.toString(part));
                }
                return appendable;
            }

            @Override
            public Joiner useForNull(String nullText) {
                throw new UnsupportedOperationException("already specified skipNulls");
            }

            @Override
            public MapJoiner withKeyValueSeparator(String kvs) {
                throw new UnsupportedOperationException("can't use .skipNulls() with maps");
            }
        };
    }

    public MapJoiner withKeyValueSeparator(char keyValueSeparator) {
        return this.withKeyValueSeparator(String.valueOf(keyValueSeparator));
    }

    public MapJoiner withKeyValueSeparator(String keyValueSeparator) {
        return new MapJoiner(this, keyValueSeparator);
    }

    CharSequence toString(@CheckForNull Object part) {
        Objects.requireNonNull(part);
        return part instanceof CharSequence ? (CharSequence)part : part.toString();
    }

    private static Iterable<@Nullable Object> iterable(final @CheckForNull Object first, final @CheckForNull Object second, final @Nullable Object[] rest) {
        Preconditions.checkNotNull(rest);
        return new AbstractList<Object>(){

            @Override
            public int size() {
                return rest.length + 2;
            }

            @Override
            @CheckForNull
            public Object get(int index) {
                switch (index) {
                    case 0: {
                        return first;
                    }
                    case 1: {
                        return second;
                    }
                }
                return rest[index - 2];
            }
        };
    }

    public static final class MapJoiner {
        private final Joiner joiner;
        private final String keyValueSeparator;

        private MapJoiner(Joiner joiner, String keyValueSeparator) {
            this.joiner = joiner;
            this.keyValueSeparator = Preconditions.checkNotNull(keyValueSeparator);
        }

        @CanIgnoreReturnValue
        public <A extends Appendable> A appendTo(A appendable, Map<?, ?> map) throws IOException {
            return this.appendTo(appendable, map.entrySet());
        }

        @CanIgnoreReturnValue
        public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map) {
            return this.appendTo(builder, (Iterable<? extends Map.Entry<?, ?>>)map.entrySet());
        }

        @CanIgnoreReturnValue
        public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Map.Entry<?, ?>> entries) throws IOException {
            return this.appendTo(appendable, entries.iterator());
        }

        @CanIgnoreReturnValue
        public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Map.Entry<?, ?>> parts) throws IOException {
            Preconditions.checkNotNull(appendable);
            if (parts.hasNext()) {
                Map.Entry<?, ?> entry = parts.next();
                appendable.append(this.joiner.toString(entry.getKey()));
                appendable.append(this.keyValueSeparator);
                appendable.append(this.joiner.toString(entry.getValue()));
                while (parts.hasNext()) {
                    appendable.append(this.joiner.separator);
                    Map.Entry<?, ?> e = parts.next();
                    appendable.append(this.joiner.toString(e.getKey()));
                    appendable.append(this.keyValueSeparator);
                    appendable.append(this.joiner.toString(e.getValue()));
                }
            }
            return appendable;
        }

        @CanIgnoreReturnValue
        public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Map.Entry<?, ?>> entries) {
            return this.appendTo(builder, entries.iterator());
        }

        @CanIgnoreReturnValue
        public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Map.Entry<?, ?>> entries) {
            try {
                this.appendTo((Appendable)builder, entries);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
            return builder;
        }

        public String join(Map<?, ?> map) {
            return this.join(map.entrySet());
        }

        public String join(Iterable<? extends Map.Entry<?, ?>> entries) {
            return this.join(entries.iterator());
        }

        public String join(Iterator<? extends Map.Entry<?, ?>> entries) {
            return this.appendTo(new StringBuilder(), entries).toString();
        }

        public MapJoiner useForNull(String nullText) {
            return new MapJoiner(this.joiner.useForNull(nullText), this.keyValueSeparator);
        }
    }
}

