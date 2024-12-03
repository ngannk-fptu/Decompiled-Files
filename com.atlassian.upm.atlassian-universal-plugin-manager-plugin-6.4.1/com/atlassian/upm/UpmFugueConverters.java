/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import java.util.Optional;

public interface UpmFugueConverters {
    public static <A> io.atlassian.fugue.Option<A> fugueNone() {
        return io.atlassian.fugue.Option.none();
    }

    public static <A> io.atlassian.fugue.Option<A> fugueNone(Class<A> c) {
        return io.atlassian.fugue.Option.none(c);
    }

    public static <A> io.atlassian.fugue.Option<A> fugueOption(A a) {
        return io.atlassian.fugue.Option.option(a);
    }

    public static <A> io.atlassian.fugue.Option<A> fugueSome(A a) {
        return io.atlassian.fugue.Option.some(a);
    }

    public static <A> io.atlassian.fugue.Option<A> toFugueOption(Option<A> o) {
        return io.atlassian.fugue.Option.option(o.getOrElse(null));
    }

    public static <A> io.atlassian.fugue.Option<A> toFugueOption(Optional<A> o) {
        return io.atlassian.fugue.Option.option(o.orElse(null));
    }

    public static <A> Optional<A> toJavaOptional(Option<A> o) {
        return Optional.ofNullable(o.getOrElse(null));
    }

    public static <A> Optional<A> toJavaOptional(io.atlassian.fugue.Option<A> o) {
        return Optional.ofNullable(o.getOrNull());
    }

    public static <A> Option<A> toUpmOption(io.atlassian.fugue.Option<A> o) {
        return Option.option(o.getOrNull());
    }

    public static <A> Option<A> toUpmOption(Optional<A> o) {
        return Option.option(o.orElse(null));
    }
}

