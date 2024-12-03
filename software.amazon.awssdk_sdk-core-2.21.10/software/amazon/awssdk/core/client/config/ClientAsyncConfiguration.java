/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.ExecutorUtils
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.client.config;

import java.util.Map;
import java.util.concurrent.Executor;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ExecutorUtils;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@Immutable
@SdkPublicApi
public final class ClientAsyncConfiguration
implements ToCopyableBuilder<Builder, ClientAsyncConfiguration> {
    private final AttributeMap advancedOptions;

    private ClientAsyncConfiguration(DefaultBuilder builder) {
        this.advancedOptions = builder.advancedOptions.build();
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Builder toBuilder() {
        return new DefaultBuilder().advancedOptions(this.advancedOptions);
    }

    public <T> T advancedOption(SdkAdvancedAsyncClientOption<T> option) {
        return (T)this.advancedOptions.get(option);
    }

    private static class DefaultBuilder
    implements Builder {
        private AttributeMap.Builder advancedOptions = AttributeMap.builder();

        private DefaultBuilder() {
        }

        @Override
        public <T> Builder advancedOption(SdkAdvancedAsyncClientOption<T> option, T value) {
            if (option == SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR) {
                Executor executor = (Executor)SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR.convertValue(value);
                this.advancedOptions.put(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR, (Object)ExecutorUtils.unmanagedExecutor((Executor)executor));
            } else {
                this.advancedOptions.put(option, value);
            }
            return this;
        }

        @Override
        public Builder advancedOptions(Map<SdkAdvancedAsyncClientOption<?>, ?> advancedOptions) {
            this.advancedOptions.putAll(advancedOptions);
            return this;
        }

        public void setAdvancedOptions(Map<SdkAdvancedAsyncClientOption<?>, Object> advancedOptions) {
            this.advancedOptions(advancedOptions);
        }

        public ClientAsyncConfiguration build() {
            return new ClientAsyncConfiguration(this);
        }

        Builder advancedOptions(AttributeMap advancedOptions) {
            this.advancedOptions = advancedOptions.toBuilder();
            return this;
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ClientAsyncConfiguration> {
        public <T> Builder advancedOption(SdkAdvancedAsyncClientOption<T> var1, T var2);

        public Builder advancedOptions(Map<SdkAdvancedAsyncClientOption<?>, ?> var1);
    }
}

