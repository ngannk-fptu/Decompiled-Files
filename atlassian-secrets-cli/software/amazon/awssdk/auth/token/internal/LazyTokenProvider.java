/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.internal;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public class LazyTokenProvider
implements SdkTokenProvider,
SdkAutoCloseable {
    private final Lazy<SdkTokenProvider> delegate;

    public LazyTokenProvider(Supplier<SdkTokenProvider> delegateConstructor) {
        this.delegate = new Lazy<SdkTokenProvider>(delegateConstructor);
    }

    public static LazyTokenProvider create(Supplier<SdkTokenProvider> delegateConstructor) {
        return new LazyTokenProvider(delegateConstructor);
    }

    @Override
    public SdkToken resolveToken() {
        return this.delegate.getValue().resolveToken();
    }

    @Override
    public void close() {
        IoUtils.closeIfCloseable(this.delegate, null);
    }

    public String toString() {
        return ToString.builder("LazyTokenProvider").add("delegate", this.delegate).build();
    }
}

