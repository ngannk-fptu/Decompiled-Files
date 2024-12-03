/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.util;

import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import io.atlassian.fugue.Either;
import java.util.Optional;

@Deprecated
public final class FugueConversionUtil {
    public static <T> Optional<T> toOptional(Maybe<T> comMaybe) {
        if (comMaybe.isDefined()) {
            return Optional.of(comMaybe.get());
        }
        return Optional.empty();
    }

    public static <L, R> io.atlassian.fugue.Pair<L, R> toIoPair(Pair<L, R> comPair) {
        return io.atlassian.fugue.Pair.pair((Object)comPair.left(), (Object)comPair.right());
    }

    public static <L, R> Pair<L, R> toComPair(io.atlassian.fugue.Pair<L, R> ioPair) {
        return Pair.pair((Object)ioPair.left(), (Object)ioPair.right());
    }

    public static <L, R> com.atlassian.fugue.Either<L, R> toComEither(Either<L, R> ioEither) {
        if (ioEither.isLeft()) {
            return com.atlassian.fugue.Either.left((Object)ioEither.left().get());
        }
        return com.atlassian.fugue.Either.right((Object)ioEither.right().get());
    }

    public static <L, R> Either<L, R> toIoEither(com.atlassian.fugue.Either<L, R> comEither) {
        if (comEither.isLeft()) {
            return Either.left((Object)comEither.left().get());
        }
        return Either.right((Object)comEither.right().get());
    }

    public static <T> Option<T> toComOption(Optional<T> optional) {
        return optional.map(Option::some).orElse(Option.none());
    }

    public static <T> Maybe<T> toComMaybe(Optional<T> optional) {
        return (Maybe)optional.map(Option::some).orElseGet(Option::none);
    }
}

