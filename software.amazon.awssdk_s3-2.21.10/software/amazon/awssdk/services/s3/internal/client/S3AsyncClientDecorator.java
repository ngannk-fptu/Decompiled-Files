/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.ConditionalDecorator
 */
package software.amazon.awssdk.services.s3.internal.client;

import java.util.ArrayList;
import java.util.function.Predicate;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.internal.crossregion.S3CrossRegionAsyncClient;
import software.amazon.awssdk.services.s3.internal.multipart.MultipartS3AsyncClient;
import software.amazon.awssdk.services.s3.multipart.MultipartConfiguration;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ConditionalDecorator;

@SdkInternalApi
public class S3AsyncClientDecorator {
    public static final AttributeMap.Key<MultipartConfiguration> MULTIPART_CONFIGURATION_KEY = new AttributeMap.Key<MultipartConfiguration>(MultipartConfiguration.class){};
    public static final AttributeMap.Key<Boolean> MULTIPART_ENABLED_KEY = new AttributeMap.Key<Boolean>(Boolean.class){};

    public S3AsyncClient decorate(S3AsyncClient base, SdkClientConfiguration clientConfiguration, AttributeMap clientContextParams) {
        ArrayList<ConditionalDecorator> decorators = new ArrayList<ConditionalDecorator>();
        decorators.add(ConditionalDecorator.create(this.isCrossRegionEnabledAsync(clientContextParams), S3CrossRegionAsyncClient::new));
        decorators.add(ConditionalDecorator.create(this.isMultipartEnable(clientContextParams), client -> {
            MultipartConfiguration multipartConfiguration = (MultipartConfiguration)clientContextParams.get(MULTIPART_CONFIGURATION_KEY);
            return MultipartS3AsyncClient.create(client, multipartConfiguration);
        }));
        return (S3AsyncClient)ConditionalDecorator.decorate((Object)base, decorators);
    }

    private Predicate<S3AsyncClient> isCrossRegionEnabledAsync(AttributeMap clientContextParams) {
        Boolean crossRegionEnabled = (Boolean)clientContextParams.get(S3ClientContextParams.CROSS_REGION_ACCESS_ENABLED);
        return client -> crossRegionEnabled != null && crossRegionEnabled != false;
    }

    private Predicate<S3AsyncClient> isMultipartEnable(AttributeMap clientContextParams) {
        Boolean multipartEnabled = (Boolean)clientContextParams.get(MULTIPART_ENABLED_KEY);
        return client -> multipartEnabled != null && multipartEnabled != false;
    }
}

