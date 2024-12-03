/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.waiters;

import java.util.Optional;
import java.util.function.Predicate;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.waiters.WaiterState;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public interface WaiterAcceptor<T> {
    public WaiterState waiterState();

    default public boolean matches(T response) {
        return false;
    }

    default public boolean matches(Throwable throwable) {
        return false;
    }

    default public Optional<String> message() {
        return Optional.empty();
    }

    public static <T> WaiterAcceptor<T> successOnResponseAcceptor(final Predicate<T> responsePredicate) {
        Validate.paramNotNull(responsePredicate, "responsePredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.SUCCESS;
            }

            @Override
            public boolean matches(T response) {
                return responsePredicate.test(response);
            }
        };
    }

    public static <T> WaiterAcceptor<T> successOnExceptionAcceptor(final Predicate<Throwable> errorPredicate) {
        Validate.paramNotNull(errorPredicate, "errorPredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.SUCCESS;
            }

            @Override
            public boolean matches(Throwable t) {
                return errorPredicate.test(t);
            }
        };
    }

    public static <T> WaiterAcceptor<T> errorOnExceptionAcceptor(final Predicate<Throwable> errorPredicate) {
        Validate.paramNotNull(errorPredicate, "errorPredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.FAILURE;
            }

            @Override
            public boolean matches(Throwable t) {
                return errorPredicate.test(t);
            }
        };
    }

    public static <T> WaiterAcceptor<T> errorOnResponseAcceptor(final Predicate<T> responsePredicate) {
        Validate.paramNotNull(responsePredicate, "responsePredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.FAILURE;
            }

            @Override
            public boolean matches(T response) {
                return responsePredicate.test(response);
            }
        };
    }

    public static <T> WaiterAcceptor<T> errorOnResponseAcceptor(final Predicate<T> responsePredicate, final String message) {
        Validate.paramNotNull(responsePredicate, "responsePredicate");
        Validate.paramNotNull(message, "message");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.FAILURE;
            }

            @Override
            public boolean matches(T response) {
                return responsePredicate.test(response);
            }

            @Override
            public Optional<String> message() {
                return Optional.of(message);
            }
        };
    }

    public static <T> WaiterAcceptor<T> retryOnExceptionAcceptor(final Predicate<Throwable> errorPredicate) {
        Validate.paramNotNull(errorPredicate, "errorPredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.RETRY;
            }

            @Override
            public boolean matches(Throwable t) {
                return errorPredicate.test(t);
            }
        };
    }

    public static <T> WaiterAcceptor<T> retryOnResponseAcceptor(final Predicate<T> responsePredicate) {
        Validate.paramNotNull(responsePredicate, "responsePredicate");
        return new WaiterAcceptor<T>(){

            @Override
            public WaiterState waiterState() {
                return WaiterState.RETRY;
            }

            @Override
            public boolean matches(T t) {
                return responsePredicate.test(t);
            }
        };
    }
}

