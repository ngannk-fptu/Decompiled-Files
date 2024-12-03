/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class MapTrait
implements Trait {
    private final String keyLocationName;
    private final String valueLocationName;
    private final SdkField valueFieldInfo;
    private final boolean isFlattened;

    private MapTrait(Builder builder) {
        this.keyLocationName = builder.keyLocationName;
        this.valueLocationName = builder.valueLocationName;
        this.valueFieldInfo = builder.valueFieldInfo;
        this.isFlattened = builder.isFlattened;
    }

    public String keyLocationName() {
        return this.keyLocationName;
    }

    public String valueLocationName() {
        return this.valueLocationName;
    }

    public SdkField valueFieldInfo() {
        return this.valueFieldInfo;
    }

    public boolean isFlattened() {
        return this.isFlattened;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String keyLocationName;
        private String valueLocationName;
        private SdkField valueFieldInfo;
        private boolean isFlattened;

        private Builder() {
        }

        public Builder keyLocationName(String keyLocationName) {
            this.keyLocationName = keyLocationName;
            return this;
        }

        public Builder valueLocationName(String valueLocationName) {
            this.valueLocationName = valueLocationName;
            return this;
        }

        public Builder valueFieldInfo(SdkField valueFieldInfo) {
            this.valueFieldInfo = valueFieldInfo;
            return this;
        }

        public Builder isFlattened(boolean isFlattened) {
            this.isFlattened = isFlattened;
            return this;
        }

        public MapTrait build() {
            return new MapTrait(this);
        }
    }
}

