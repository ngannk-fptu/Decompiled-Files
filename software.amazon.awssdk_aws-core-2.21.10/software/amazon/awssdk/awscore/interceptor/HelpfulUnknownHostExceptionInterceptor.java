/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.Context$FailedExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.regions.PartitionMetadata
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.RegionMetadata
 *  software.amazon.awssdk.regions.ServiceMetadata
 *  software.amazon.awssdk.regions.ServicePartitionMetadata
 */
package software.amazon.awssdk.awscore.interceptor;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionMetadata;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServicePartitionMetadata;

@SdkInternalApi
public final class HelpfulUnknownHostExceptionInterceptor
implements ExecutionInterceptor {
    public Throwable modifyException(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        if (!this.hasCause(context.exception(), UnknownHostException.class)) {
            return context.exception();
        }
        StringBuilder error = new StringBuilder();
        error.append("Received an UnknownHostException when attempting to interact with a service. See cause for the exact endpoint that is failing to resolve. ");
        Optional<String> globalRegionErrorDetails = this.getGlobalRegionErrorDetails(executionAttributes);
        if (globalRegionErrorDetails.isPresent()) {
            error.append(globalRegionErrorDetails.get());
        } else {
            error.append("If this is happening on an endpoint that previously worked, there may be a network connectivity issue or your DNS cache could be storing endpoints for too long.");
        }
        return SdkClientException.builder().message(error.toString()).cause(context.exception()).build();
    }

    private Optional<String> getGlobalRegionErrorDetails(ExecutionAttributes executionAttributes) {
        Region clientRegion = this.clientRegion(executionAttributes);
        if (clientRegion.isGlobalRegion()) {
            return Optional.empty();
        }
        List<ServicePartitionMetadata> globalPartitionsForService = this.globalPartitionsForService(executionAttributes);
        if (globalPartitionsForService.isEmpty()) {
            return Optional.empty();
        }
        String clientPartition = Optional.ofNullable(clientRegion.metadata()).map(RegionMetadata::partition).map(PartitionMetadata::id).orElse(null);
        Optional globalRegionForClientRegion = globalPartitionsForService.stream().filter(p -> p.partition().id().equals(clientPartition)).findAny().flatMap(ServicePartitionMetadata::globalRegion);
        if (!globalRegionForClientRegion.isPresent()) {
            String globalRegionsForThisService = globalPartitionsForService.stream().map(ServicePartitionMetadata::globalRegion).filter(Optional::isPresent).map(Optional::get).filter(Region::isGlobalRegion).map(Region::id).collect(Collectors.joining("/"));
            return Optional.of("This specific service may be a global service, in which case you should configure a global region like " + globalRegionsForThisService + " on the client.");
        }
        Region globalRegion = (Region)globalRegionForClientRegion.get();
        return Optional.of("This specific service is global in the same partition as the region configured on this client (" + clientRegion + "). If this is the first time you're trying to talk to this service in this region, you should try configuring the global region on your client, instead: " + globalRegion);
    }

    private Region clientRegion(ExecutionAttributes executionAttributes) {
        return (Region)executionAttributes.getAttribute(AwsExecutionAttribute.AWS_REGION);
    }

    private List<ServicePartitionMetadata> globalPartitionsForService(ExecutionAttributes executionAttributes) {
        return ServiceMetadata.of((String)((String)executionAttributes.getAttribute(AwsExecutionAttribute.ENDPOINT_PREFIX))).servicePartitions().stream().filter(sp -> sp.globalRegion().isPresent()).collect(Collectors.toList());
    }

    private boolean hasCause(Throwable thrown, Class<? extends Throwable> cause) {
        if (thrown == null) {
            return false;
        }
        if (cause.isAssignableFrom(thrown.getClass())) {
            return true;
        }
        return this.hasCause(thrown.getCause(), cause);
    }
}

