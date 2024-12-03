/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkException;

@SdkInternalApi
public class RuleError
extends SdkException {
    protected RuleError(BuilderImpl builder) {
        super(builder);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static <T> T ctx(String message, Supplier<T> f) {
        try {
            return f.get();
        }
        catch (Exception e) {
            throw RuleError.builder().message(message).cause(e).build();
        }
    }

    public static <T> T ctx(String message, Runnable f) {
        return (T)RuleError.ctx(message, () -> {
            f.run();
            return null;
        });
    }

    private static class BuilderImpl
    extends SdkException.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        @Override
        public RuleError build() {
            return new RuleError(this);
        }
    }

    public static interface Builder
    extends SdkException.Builder {
        @Override
        public RuleError build();
    }
}

