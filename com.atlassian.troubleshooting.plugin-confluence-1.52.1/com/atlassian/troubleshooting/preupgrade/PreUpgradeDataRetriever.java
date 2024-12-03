/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.troubleshooting.preupgrade.PupDataResource;
import com.atlassian.troubleshooting.preupgrade.client.PreUpgradeDataServiceClient;
import com.atlassian.troubleshooting.preupgrade.model.DefaultSupportedPlatformRules;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResourceService;
import com.atlassian.troubleshooting.util.OptionalUtils;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PreUpgradeDataRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(PreUpgradeDataRetriever.class);
    private final PreUpgradeDataServiceClient preUpgradeDataServiceClient;
    private final SavedExternalResourceService savedExternalResourceService;

    @Autowired
    public PreUpgradeDataRetriever(@Nonnull PreUpgradeDataServiceClient preUpgradeDataServiceClient, @Nonnull SavedExternalResourceService savedExternalResourceService) {
        this.preUpgradeDataServiceClient = Objects.requireNonNull(preUpgradeDataServiceClient);
        this.savedExternalResourceService = Objects.requireNonNull(savedExternalResourceService);
    }

    @Nonnull
    private Optional<MicroservicePreUpgradeDataDTOWithSource> requestInfoFromRemoteService(@Nonnull SupportedPlatformQuery query) {
        Optional<String> response = this.preUpgradeDataServiceClient.findSupportedPlatformInfoJsonForQuery(query);
        if (response.isPresent()) {
            return this.createUpgradeInfo(query, response.get(), MicroservicePreUpgradeDataDTOWithSource.Source.MICROSERVICE);
        }
        return Optional.empty();
    }

    @Nonnull
    private Optional<MicroservicePreUpgradeDataDTOWithSource> findInfoInResource(@Nonnull SupportedPlatformQuery query) {
        return this.createUpgradeInfo(query, this.savedExternalResourceService.resolveFromClassloader(PupDataResource.INSTANCE), MicroservicePreUpgradeDataDTOWithSource.Source.RESOURCE);
    }

    private Optional<MicroservicePreUpgradeDataDTOWithSource> createUpgradeInfo(@Nonnull SupportedPlatformQuery query, @Nullable String json, @Nonnull MicroservicePreUpgradeDataDTOWithSource.Source source) {
        try {
            TypeReference<ArrayList<MicroservicePreUpgradeDataDTO>> listType = new TypeReference<ArrayList<MicroservicePreUpgradeDataDTO>>(){};
            return ((ArrayList)new ObjectMapper().readValue(json, (TypeReference)listType)).stream().filter(dto -> dto.product.equals(query.getProduct())).findFirst().map(productInfo -> new MicroservicePreUpgradeDataDTOWithSource(source, this.applyRules((MicroservicePreUpgradeDataDTO)productInfo, query)));
        }
        catch (Exception e) {
            LOG.error("Unable to generate upgrade info", (Throwable)e);
            return Optional.empty();
        }
    }

    @Nonnull
    private MicroservicePreUpgradeDataDTO applyRules(@Nonnull MicroservicePreUpgradeDataDTO info, @Nonnull SupportedPlatformQuery query) {
        return new DefaultSupportedPlatformRules().apply(info, query);
    }

    @Nonnull
    Optional<MicroservicePreUpgradeDataDTOWithSource> getUpgradeInfoDto(@Nonnull SupportedPlatformQuery query) {
        Objects.requireNonNull(query);
        return OptionalUtils.orElseGetOptional(this.requestInfoFromRemoteService(query), () -> this.findInfoInResource(query));
    }

    public static class MicroservicePreUpgradeDataDTOWithSource {
        private final Source source;
        private final MicroservicePreUpgradeDataDTO upgradeInfo;

        MicroservicePreUpgradeDataDTOWithSource(Source source, MicroservicePreUpgradeDataDTO upgradeInfo) {
            this.source = source;
            this.upgradeInfo = upgradeInfo;
        }

        boolean isFromResource() {
            return this.source == Source.RESOURCE;
        }

        protected Source getSource() {
            return this.source;
        }

        MicroservicePreUpgradeDataDTO getUpgradeInfo() {
            return this.upgradeInfo;
        }

        static enum Source {
            MICROSERVICE,
            RESOURCE;

        }
    }
}

