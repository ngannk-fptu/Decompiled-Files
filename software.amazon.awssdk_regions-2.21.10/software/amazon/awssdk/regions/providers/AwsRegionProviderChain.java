/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 */
package software.amazon.awssdk.regions.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@SdkProtectedApi
public class AwsRegionProviderChain
implements AwsRegionProvider {
    private static final Logger log = LoggerFactory.getLogger(AwsRegionProviderChain.class);
    private final List<AwsRegionProvider> providers;

    public AwsRegionProviderChain(AwsRegionProvider ... providers) {
        this.providers = new ArrayList<AwsRegionProvider>(providers.length);
        Collections.addAll(this.providers, providers);
    }

    @Override
    public Region getRegion() throws SdkClientException {
        ArrayList<String> exceptionMessages = null;
        for (AwsRegionProvider provider : this.providers) {
            try {
                Region region = provider.getRegion();
                if (region == null) continue;
                return region;
            }
            catch (Exception e) {
                log.debug("Unable to load region from {}:{}", (Object)provider.toString(), (Object)e.getMessage());
                String message = provider.toString() + ": " + e.getMessage();
                if (exceptionMessages == null) {
                    exceptionMessages = new ArrayList<String>();
                }
                exceptionMessages.add(message);
            }
        }
        throw SdkClientException.builder().message("Unable to load region from any of the providers in the chain " + this + ": " + exceptionMessages).build();
    }
}

