/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Lazy
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ToString
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
        this.delegate = new Lazy(delegateConstructor);
    }

    public static LazyTokenProvider create(Supplier<SdkTokenProvider> delegateConstructor) {
        return new LazyTokenProvider(delegateConstructor);
    }

    @Override
    public SdkToken resolveToken() {
        return ((SdkTokenProvider)this.delegate.getValue()).resolveToken();
    }

    public void close() {
        IoUtils.closeIfCloseable(this.delegate, null);
    }

    public String toString() {
        return ToString.builder((String)"LazyTokenProvider").add("delegate", this.delegate).build();
    }
}

