/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.builder;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;

@SdkPublicApi
public interface SdkAsyncClientBuilder<B extends SdkAsyncClientBuilder<B, C>, C> {
    public B asyncConfiguration(ClientAsyncConfiguration var1);

    default public B asyncConfiguration(Consumer<ClientAsyncConfiguration.Builder> clientAsyncConfiguration) {
        return this.asyncConfiguration((ClientAsyncConfiguration)ClientAsyncConfiguration.builder().applyMutation(clientAsyncConfiguration).build());
    }

    public B httpClient(SdkAsyncHttpClient var1);

    public B httpClientBuilder(SdkAsyncHttpClient.Builder var1);
}

