/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Enums
 *  com.google.common.base.Optional
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 */
package com.atlassian.crowd.directory.rest.endpoint;

import com.atlassian.crowd.directory.AzureAdDirectory;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.directory.rest.endpoint.BasicAzureApiUriResolver;
import com.atlassian.crowd.directory.rest.endpoint.CustomAzureApiUriResolver;
import com.atlassian.crowd.directory.rest.endpoint.DefaultRegion;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class AzureApiUriResolverFactory {
    public AzureApiUriResolver getEndpointDataProviderForDirectory(AzureAdDirectory azureAdDirectory) {
        String region = Strings.nullToEmpty((String)azureAdDirectory.getValue("AZURE_AD_REGION"));
        Optional knownRegion = Enums.getIfPresent(DefaultRegion.class, (String)region);
        if (knownRegion.isPresent()) {
            return new BasicAzureApiUriResolver((DefaultRegion)((Object)knownRegion.get()));
        }
        if ("CUSTOM".equals(region)) {
            String graphApiUrl = (String)Preconditions.checkNotNull((Object)azureAdDirectory.getValue("AZURE_AD_GRAPH_API_ENDPOINT"));
            String authorityApiUrl = (String)Preconditions.checkNotNull((Object)azureAdDirectory.getValue("AZURE_AD_AUTHORITY_API_ENDPOINT"));
            return new CustomAzureApiUriResolver(graphApiUrl, authorityApiUrl);
        }
        throw new IllegalArgumentException(String.format("The directory %s doesn't have the expected %s attribute", azureAdDirectory.getDirectoryId(), "AZURE_AD_REGION"));
    }
}

