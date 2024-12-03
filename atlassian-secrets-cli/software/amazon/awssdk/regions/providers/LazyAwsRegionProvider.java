/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.ToString;

@SdkProtectedApi
public class LazyAwsRegionProvider
implements AwsRegionProvider {
    private final Lazy<AwsRegionProvider> delegate;

    public LazyAwsRegionProvider(Supplier<AwsRegionProvider> delegateConstructor) {
        this.delegate = new Lazy<AwsRegionProvider>(delegateConstructor);
    }

    @Override
    public Region getRegion() {
        return this.delegate.getValue().getRegion();
    }

    public String toString() {
        return ToString.builder("LazyAwsRegionProvider").add("delegate", this.delegate).build();
    }
}

