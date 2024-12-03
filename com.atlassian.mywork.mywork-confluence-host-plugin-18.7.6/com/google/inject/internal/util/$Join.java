/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Preconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Join {
    private $Join() {
    }

    public static String join(String delimiter, Iterable<?> tokens) {
        return $Join.join(delimiter, tokens.iterator());
    }

    public static String join(String delimiter, Object[] tokens) {
        return $Join.join(delimiter, Arrays.asList(tokens));
    }

    public static String join(String delimiter, @$Nullable Object firstToken, Object ... otherTokens) {
        $Preconditions.checkNotNull(otherTokens);
        return $Join.join(delimiter, $Lists.newArrayList(firstToken, otherTokens));
    }

    public static String join(String delimiter, Iterator<?> tokens) {
        StringBuilder sb = new StringBuilder();
        $Join.join(sb, delimiter, tokens);
        return sb.toString();
    }

    public static String join(String keyValueSeparator, String entryDelimiter, Map<?, ?> map) {
        return $Join.join(new StringBuilder(), keyValueSeparator, entryDelimiter, map).toString();
    }

    public static <T extends Appendable> T join(T appendable, String delimiter, Iterable<?> tokens) {
        return $Join.join(appendable, delimiter, tokens.iterator());
    }

    public static <T extends Appendable> T join(T appendable, String delimiter, Object[] tokens) {
        return $Join.join(appendable, delimiter, Arrays.asList(tokens));
    }

    public static <T extends Appendable> T join(T appendable, String delimiter, @$Nullable Object firstToken, Object ... otherTokens) {
        $Preconditions.checkNotNull(otherTokens);
        return $Join.join(appendable, delimiter, $Lists.newArrayList(firstToken, otherTokens));
    }

    public static <T extends Appendable> T join(T appendable, String delimiter, Iterator<?> tokens) {
        $Preconditions.checkNotNull(appendable);
        $Preconditions.checkNotNull(delimiter);
        if (tokens.hasNext()) {
            try {
                $Join.appendOneToken(appendable, tokens.next());
                while (tokens.hasNext()) {
                    appendable.append(delimiter);
                    $Join.appendOneToken(appendable, tokens.next());
                }
            }
            catch (IOException e) {
                throw new JoinException(e);
            }
        }
        return appendable;
    }

    public static <T extends Appendable> T join(T appendable, String keyValueSeparator, String entryDelimiter, Map<?, ?> map) {
        $Preconditions.checkNotNull(appendable);
        $Preconditions.checkNotNull(keyValueSeparator);
        $Preconditions.checkNotNull(entryDelimiter);
        Iterator<Map.Entry<?, ?>> entries = map.entrySet().iterator();
        if (entries.hasNext()) {
            try {
                $Join.appendOneEntry(appendable, keyValueSeparator, entries.next());
                while (entries.hasNext()) {
                    appendable.append(entryDelimiter);
                    $Join.appendOneEntry(appendable, keyValueSeparator, entries.next());
                }
            }
            catch (IOException e) {
                throw new JoinException(e);
            }
        }
        return appendable;
    }

    private static void appendOneEntry(Appendable appendable, String keyValueSeparator, Map.Entry<?, ?> entry) throws IOException {
        $Join.appendOneToken(appendable, entry.getKey());
        appendable.append(keyValueSeparator);
        $Join.appendOneToken(appendable, entry.getValue());
    }

    private static void appendOneToken(Appendable appendable, Object token) throws IOException {
        appendable.append($Join.toCharSequence(token));
    }

    private static CharSequence toCharSequence(Object token) {
        return token instanceof CharSequence ? (CharSequence)token : String.valueOf(token);
    }

    public static class JoinException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private JoinException(IOException cause) {
            super(cause);
        }
    }
}

