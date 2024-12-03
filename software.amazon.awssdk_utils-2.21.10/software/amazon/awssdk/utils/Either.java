/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class Either<L, R> {
    private final Optional<L> left;
    private final Optional<R> right;

    private Either(Optional<L> l, Optional<R> r) {
        this.left = l;
        this.right = r;
    }

    public <T> T map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc) {
        return (T)this.left.map(lFunc).orElseGet(() -> this.right.map(rFunc).get());
    }

    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
        return new Either<T, R>(this.left.map(lFunc), this.right);
    }

    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
        return new Either<L, T>(this.left, this.right.map(rFunc));
    }

    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        this.left.ifPresent(lFunc);
        this.right.ifPresent(rFunc);
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Either(Optional.of(value), Optional.empty());
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either(Optional.empty(), Optional.of(value));
    }

    public Optional<L> left() {
        return this.left;
    }

    public Optional<R> right() {
        return this.right;
    }

    public static <L, R> Optional<Either<L, R>> fromNullable(L left, R right) {
        if (left != null && right == null) {
            return Optional.of(Either.left(left));
        }
        if (left == null && right != null) {
            return Optional.of(Either.right(right));
        }
        if (left == null && right == null) {
            return Optional.empty();
        }
        throw new IllegalArgumentException(String.format("Only one of either left or right should be non-null. Got (left: %s, right: %s)", left, right));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Either)) {
            return false;
        }
        Either either = (Either)o;
        return this.left.equals(either.left) && this.right.equals(either.right);
    }

    public int hashCode() {
        return 31 * this.left.hashCode() + this.right.hashCode();
    }
}

