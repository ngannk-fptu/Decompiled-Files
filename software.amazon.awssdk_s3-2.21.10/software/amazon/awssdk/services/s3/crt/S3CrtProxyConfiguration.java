/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.crtcore.CrtProxyConfiguration
 *  software.amazon.awssdk.crtcore.CrtProxyConfiguration$Builder
 *  software.amazon.awssdk.crtcore.CrtProxyConfiguration$DefaultBuilder
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.crt;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.crtcore.CrtProxyConfiguration;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class S3CrtProxyConfiguration
extends CrtProxyConfiguration
implements ToCopyableBuilder<Builder, S3CrtProxyConfiguration> {
    private S3CrtProxyConfiguration(DefaultBuilder builder) {
        super((CrtProxyConfiguration.DefaultBuilder)builder);
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public String toString() {
        return super.toString();
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    private static final class DefaultBuilder
    extends CrtProxyConfiguration.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private DefaultBuilder(S3CrtProxyConfiguration proxyConfiguration) {
            super((CrtProxyConfiguration)proxyConfiguration);
        }

        private DefaultBuilder() {
        }

        @Override
        public S3CrtProxyConfiguration build() {
            return new S3CrtProxyConfiguration(this);
        }
    }

    public static interface Builder
    extends CrtProxyConfiguration.Builder,
    CopyableBuilder<Builder, S3CrtProxyConfiguration> {
        public Builder host(String var1);

        public Builder port(int var1);

        public Builder scheme(String var1);

        public Builder username(String var1);

        public Builder password(String var1);

        public Builder useSystemPropertyValues(Boolean var1);

        public Builder useEnvironmentVariableValues(Boolean var1);

        public S3CrtProxyConfiguration build();
    }
}

