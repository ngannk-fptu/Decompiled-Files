/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.token.credentials;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class SdkTokenProviderFactoryProperties {
    private final String startUrl;
    private final String region;

    private SdkTokenProviderFactoryProperties(BuilderImpl builder) {
        Validate.paramNotNull((Object)builder.startUrl, (String)"startUrl");
        Validate.paramNotNull((Object)builder.region, (String)"region");
        this.startUrl = builder.startUrl;
        this.region = builder.region;
    }

    public String startUrl() {
        return this.startUrl;
    }

    public String region() {
        return this.region;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl
    implements Builder {
        private String startUrl;
        private String region;

        private BuilderImpl() {
        }

        @Override
        public Builder startUrl(String startUrl) {
            this.startUrl = startUrl;
            return this;
        }

        @Override
        public Builder region(String region) {
            this.region = region;
            return this;
        }

        @Override
        public SdkTokenProviderFactoryProperties build() {
            return new SdkTokenProviderFactoryProperties(this);
        }
    }

    public static interface Builder {
        public Builder startUrl(String var1);

        public Builder region(String var1);

        public SdkTokenProviderFactoryProperties build();
    }
}

