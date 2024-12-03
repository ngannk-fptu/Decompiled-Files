/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class LocationTrait
implements Trait {
    private final MarshallLocation location;
    private final String locationName;
    private final String unmarshallLocationName;

    private LocationTrait(Builder builder) {
        this.location = builder.location;
        this.locationName = builder.locationName;
        this.unmarshallLocationName = builder.unmarshallLocationName == null ? builder.locationName : builder.unmarshallLocationName;
    }

    public MarshallLocation location() {
        return this.location;
    }

    public String locationName() {
        return this.locationName;
    }

    public String unmarshallLocationName() {
        return this.unmarshallLocationName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private MarshallLocation location;
        private String locationName;
        private String unmarshallLocationName;

        private Builder() {
        }

        public Builder location(MarshallLocation location) {
            this.location = location;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder unmarshallLocationName(String unmarshallLocationName) {
            this.unmarshallLocationName = unmarshallLocationName;
            return this;
        }

        public LocationTrait build() {
            return new LocationTrait(this);
        }
    }
}

