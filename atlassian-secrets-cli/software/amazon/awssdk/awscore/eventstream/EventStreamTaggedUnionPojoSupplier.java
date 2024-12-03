/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkProtectedApi
public final class EventStreamTaggedUnionPojoSupplier
implements Function<SdkHttpFullResponse, SdkPojo> {
    private final Map<String, Supplier<SdkPojo>> pojoSuppliers;
    private final Supplier<SdkPojo> defaultPojoSupplier;

    private EventStreamTaggedUnionPojoSupplier(Builder builder) {
        this.pojoSuppliers = new HashMap<String, Supplier<SdkPojo>>(builder.pojoSuppliers);
        this.defaultPojoSupplier = builder.defaultPojoSupplier;
    }

    @Override
    public SdkPojo apply(SdkHttpFullResponse sdkHttpFullResponse) {
        String eventType = sdkHttpFullResponse.firstMatchingHeader(":event-type").orElse(null);
        return this.pojoSuppliers.getOrDefault(eventType, this.defaultPojoSupplier).get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Supplier<SdkPojo>> pojoSuppliers = new HashMap<String, Supplier<SdkPojo>>();
        private Supplier<SdkPojo> defaultPojoSupplier;

        private Builder() {
        }

        public Builder putSdkPojoSupplier(String type, Supplier<SdkPojo> pojoSupplier) {
            this.pojoSuppliers.put(type, pojoSupplier);
            return this;
        }

        public Builder defaultSdkPojoSupplier(Supplier<SdkPojo> defaultPojoSupplier) {
            this.defaultPojoSupplier = defaultPojoSupplier;
            return this;
        }

        public EventStreamTaggedUnionPojoSupplier build() {
            return new EventStreamTaggedUnionPojoSupplier(this);
        }
    }
}

