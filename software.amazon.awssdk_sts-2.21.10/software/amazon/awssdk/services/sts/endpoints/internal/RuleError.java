/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.exception.SdkException$Builder
 *  software.amazon.awssdk.core.exception.SdkException$BuilderImpl
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkException;

@SdkInternalApi
public class RuleError
extends SdkException {
    protected RuleError(BuilderImpl builder) {
        super((SdkException.Builder)builder);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static <T> T ctx(String message, Supplier<T> f) {
        try {
            return f.get();
        }
        catch (Exception e) {
            throw RuleError.builder().message(message).cause((Throwable)e).build();
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
        public RuleError build();
    }
}

