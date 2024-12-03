/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.traits;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class ListTrait
implements Trait {
    private final String memberLocationName;
    private final SdkField memberFieldInfo;
    private final boolean isFlattened;

    private ListTrait(Builder builder) {
        this.memberLocationName = builder.memberLocationName;
        this.memberFieldInfo = builder.memberFieldInfo;
        this.isFlattened = builder.isFlattened;
    }

    public String memberLocationName() {
        return this.memberLocationName;
    }

    public SdkField memberFieldInfo() {
        return this.memberFieldInfo;
    }

    public boolean isFlattened() {
        return this.isFlattened;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String memberLocationName;
        private SdkField memberFieldInfo;
        private boolean isFlattened;

        private Builder() {
        }

        public Builder memberLocationName(String memberLocationName) {
            this.memberLocationName = memberLocationName;
            return this;
        }

        public Builder memberFieldInfo(SdkField memberFieldInfo) {
            this.memberFieldInfo = memberFieldInfo;
            return this;
        }

        public Builder isFlattened(boolean isFlattened) {
            this.isFlattened = isFlattened;
            return this;
        }

        public ListTrait build() {
            return new ListTrait(this);
        }
    }
}

