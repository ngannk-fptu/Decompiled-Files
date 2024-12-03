/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.internal.DefaultConditionalDecorator;

@FunctionalInterface
@SdkProtectedApi
public interface ConditionalDecorator<T> {
    default public Predicate<T> predicate() {
        return t -> true;
    }

    public UnaryOperator<T> transform();

    public static <T> ConditionalDecorator<T> create(Predicate<T> predicate, UnaryOperator<T> transform) {
        DefaultConditionalDecorator.Builder<T> builder = new DefaultConditionalDecorator.Builder<T>();
        return builder.predicate(predicate).transform(transform).build();
    }

    public static <T> T decorate(T initialValue, List<ConditionalDecorator<T>> decorators) {
        return (T)decorators.stream().filter(d -> d.predicate().test(initialValue)).reduce(initialValue, (element, decorator) -> decorator.transform().apply(element), (el1, el2) -> {
            throw new IllegalStateException("Should not reach here, combine function not needed unless executed in parallel.");
        });
    }
}

