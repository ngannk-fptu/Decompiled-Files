/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public class LazyAwsCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable {
    private final Lazy<AwsCredentialsProvider> delegate;

    private LazyAwsCredentialsProvider(Supplier<AwsCredentialsProvider> delegateConstructor) {
        this.delegate = new Lazy<AwsCredentialsProvider>(delegateConstructor);
    }

    public static LazyAwsCredentialsProvider create(Supplier<AwsCredentialsProvider> delegateConstructor) {
        return new LazyAwsCredentialsProvider(delegateConstructor);
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return this.delegate.getValue().resolveCredentials();
    }

    @Override
    public void close() {
        IoUtils.closeIfCloseable(this.delegate, null);
    }

    public String toString() {
        return ToString.builder("LazyAwsCredentialsProvider").add("delegate", this.delegate).build();
    }
}

