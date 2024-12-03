/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.cloud;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.entity.CloudEdition;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.store.AttachmentMigrationStore;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class CloudSiteService {
    private static final Logger log = LoggerFactory.getLogger(CloudSiteService.class);
    private final PluginTransactionTemplate ptx;
    private final CloudSiteStore cloudSiteStore;
    private final AttachmentMigrationStore attachmentMigrationStore;
    private final PlanStore planStore;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public CloudSiteService(PluginTransactionTemplate ptx, CloudSiteStore cloudSiteStore, AttachmentMigrationStore attachmentMigrationStore, PlanStore planStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.ptx = ptx;
        this.cloudSiteStore = cloudSiteStore;
        this.attachmentMigrationStore = attachmentMigrationStore;
        this.planStore = planStore;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @Nonnull
    public List<CloudSite> getAllSites() {
        return this.ptx.read(this.cloudSiteStore::getAllSites);
    }

    public Optional<CloudSite> getByCloudId(String cloudId) {
        return this.ptx.read(() -> this.cloudSiteStore.getByCloudId(cloudId));
    }

    public Optional<CloudSite> getByCloudUrl(String cloudUrl) {
        return this.ptx.read(() -> this.cloudSiteStore.getByCloudUrl(cloudUrl));
    }

    public Optional<CloudSite> getByContainerToken(String containerToken) {
        if (this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            List cloudSites = this.ptx.read(this.cloudSiteStore::getAllSites);
            return cloudSites.stream().filter(site -> site.getContainerToken().equals(containerToken)).findFirst();
        }
        return this.ptx.read(() -> this.cloudSiteStore.getByContainerToken(containerToken));
    }

    public CloudSite getByStepId(String stepId) {
        return this.ptx.read(() -> this.cloudSiteStore.getByStepId(stepId));
    }

    public Optional<String> getNonFailingToken() {
        return this.ptx.read(this.cloudSiteStore::getNonFailingToken);
    }

    public void markTokenAsFailed(String token) {
        if (this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            List cloudSites = this.ptx.read(this.cloudSiteStore::getAllSites);
            Optional<CloudSite> cloudSite = cloudSites.stream().filter(site -> site.getContainerToken().equals(token)).findFirst();
            cloudSite.ifPresent(site -> this.cloudSiteStore.markTokenAsFailedForCloudId(site.getCloudId()));
        } else {
            this.ptx.write(() -> this.cloudSiteStore.markTokenAsFailed(token));
        }
    }

    public CloudSite createOrUpdate(String cloudId, String cloudUrl, String containerToken, Optional<Edition> maybeEdition, CloudType cloudType) {
        Optional maybeCloudSiteByCloudUrl;
        Optional<CloudEdition> maybeCloudEdition = maybeEdition.map(CloudEdition::from);
        Optional maybeCloudSiteByCloudId = this.ptx.read(() -> this.cloudSiteStore.getByCloudId(cloudId));
        CloudSite oldCloudSite = maybeCloudSiteByCloudId.orElse((maybeCloudSiteByCloudUrl = this.ptx.read(() -> this.cloudSiteStore.getByCloudUrl(cloudUrl))).orElse(null));
        if (Objects.isNull(oldCloudSite)) {
            return this.createNewCloudSite(cloudId, cloudUrl, containerToken, maybeCloudEdition, cloudType);
        }
        if (!cloudId.equals(oldCloudSite.getCloudId())) {
            return this.replaceOldCloudSite(cloudId, cloudUrl, containerToken, maybeCloudEdition, cloudType, oldCloudSite);
        }
        return this.updateCloudSite(cloudUrl, containerToken, maybeCloudEdition, cloudType, oldCloudSite);
    }

    private CloudSite createNewCloudSite(String cloudId, String cloudUrl, String containerToken, Optional<CloudEdition> maybeCloudEdition, CloudType cloudType) {
        return this.ptx.write(() -> this.cloudSiteStore.create(new CloudSite(cloudId, cloudUrl, containerToken, cloudType).withEdition(maybeCloudEdition)));
    }

    private CloudSite replaceOldCloudSite(String cloudId, String cloudUrl, String containerToken, Optional<CloudEdition> maybeCloudEdition, CloudType cloudType, CloudSite oldCloudSite) {
        this.replaceCloudUrlWithTemporary(oldCloudSite);
        return this.ptx.write(() -> {
            CloudSite newCloudSite = this.cloudSiteStore.create(new CloudSite(cloudId, cloudUrl, containerToken, cloudType).withEdition(maybeCloudEdition));
            this.relinkPlans(oldCloudSite, newCloudSite);
            this.removeOldCloudSite(oldCloudSite);
            return newCloudSite;
        });
    }

    private void replaceCloudUrlWithTemporary(CloudSite oldCloudSite) {
        this.ptx.write(() -> {
            String tempCloudURL = UUID.randomUUID().toString();
            oldCloudSite.setCloudUrl(tempCloudURL);
            this.cloudSiteStore.update(oldCloudSite);
        });
    }

    private void relinkPlans(CloudSite oldCloudSite, CloudSite newCloudSite) {
        List<Plan> plans = this.planStore.getAllPlansByCloudId(oldCloudSite.getCloudId());
        plans.forEach(plan -> {
            plan.setCloudSite(newCloudSite);
            this.planStore.updatePlan((Plan)plan);
        });
    }

    private void removeOldCloudSite(CloudSite oldCloudSite) {
        this.attachmentMigrationStore.deleteMigrationsByCloudId(oldCloudSite.getCloudId());
        this.cloudSiteStore.removeSiteByCloudId(oldCloudSite.getCloudId());
    }

    private CloudSite updateCloudSite(String cloudUrl, String containerToken, Optional<CloudEdition> maybeCloudEdition, CloudType cloudType, CloudSite oldCloudSite) {
        return this.ptx.write(() -> {
            this.updateCloudUrl(cloudUrl, oldCloudSite);
            this.updateContainerToken(containerToken, oldCloudSite);
            this.updateCloudType(cloudType, oldCloudSite);
            this.updateCreatedTime(Instant.now(), oldCloudSite);
            return this.cloudSiteStore.update(oldCloudSite.withEdition(maybeCloudEdition));
        });
    }

    private void updateCloudUrl(String cloudUrl, CloudSite oldCloudSite) {
        if (!cloudUrl.equals(oldCloudSite.getCloudUrl())) {
            oldCloudSite.setCloudUrl(cloudUrl);
        }
    }

    private void updateContainerToken(String containerToken, CloudSite oldCloudSite) {
        if (!containerToken.equals(oldCloudSite.getContainerToken())) {
            oldCloudSite.setContainerToken(containerToken);
            oldCloudSite.setFailing(false);
        }
    }

    public CloudSite updateMediaClientId(String cloudId, String mediaClientId) {
        return this.ptx.write(() -> {
            Optional<CloudSite> maybeCloudSite = this.cloudSiteStore.getByCloudId(cloudId);
            CloudSite cloudSite = maybeCloudSite.orElseThrow(() -> new IllegalStateException(String.format("Failed to find cloud site record for cloudId = %s", cloudId)));
            cloudSite.setMediaClientId(mediaClientId);
            return this.cloudSiteStore.update(cloudSite);
        });
    }

    public CloudSite updateCloudEdition(String cloudId, CloudEdition cloudEdition) {
        return this.ptx.write(() -> {
            Optional<CloudSite> maybeCloudSite = this.cloudSiteStore.getByCloudId(cloudId);
            CloudSite cloudSite = maybeCloudSite.orElseThrow(() -> new IllegalStateException(String.format("Failed to find cloud site record for cloudId = %s", cloudId)));
            cloudSite.setEdition(cloudEdition);
            return this.cloudSiteStore.update(cloudSite);
        });
    }

    private void updateCloudType(CloudType cloudType, CloudSite oldCloudSite) {
        if (!cloudType.equals((Object)oldCloudSite.getCloudType())) {
            oldCloudSite.setCloudType(cloudType);
        }
    }

    private void updateCreatedTime(Instant createdTime, CloudSite oldCloudSite) {
        oldCloudSite.setCreatedTime(createdTime);
    }

    public void markAllTokensAsFailed() {
        this.ptx.write(this.cloudSiteStore::markAllTokensAsFailed);
    }
}

