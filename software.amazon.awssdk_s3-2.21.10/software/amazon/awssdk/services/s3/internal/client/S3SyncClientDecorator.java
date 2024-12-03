/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.ConditionalDecorator
 */
package software.amazon.awssdk.services.s3.internal.client;

import java.util.ArrayList;
import java.util.function.Predicate;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.internal.crossregion.S3CrossRegionSyncClient;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ConditionalDecorator;

@SdkInternalApi
public class S3SyncClientDecorator {
    public S3Client decorate(S3Client base, SdkClientConfiguration clientConfiguration, AttributeMap clientContextParams) {
        ArrayList<ConditionalDecorator> decorators = new ArrayList<ConditionalDecorator>();
        decorators.add(ConditionalDecorator.create(this.isCrossRegionEnabledSync(clientContextParams), S3CrossRegionSyncClient::new));
        return (S3Client)ConditionalDecorator.decorate((Object)base, decorators);
    }

    private Predicate<S3Client> isCrossRegionEnabledSync(AttributeMap clientContextParams) {
        Boolean crossRegionEnabled = (Boolean)clientContextParams.get(S3ClientContextParams.CROSS_REGION_ACCESS_ENABLED);
        return client -> crossRegionEnabled != null && crossRegionEnabled != false;
    }
}

