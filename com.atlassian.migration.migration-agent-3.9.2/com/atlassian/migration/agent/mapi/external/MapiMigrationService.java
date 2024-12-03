/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.migration.agent.mapi.external;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.mapi.entity.MapiStatusDto;
import com.atlassian.migration.agent.mapi.external.model.JobDefinitionCloudId;
import com.atlassian.migration.agent.mapi.external.model.JobDefinitionResponse;
import com.atlassian.migration.agent.mapi.external.model.JobDetails;
import com.atlassian.migration.agent.mapi.external.model.JobValidationException;
import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.mapi.job.JobDefinition;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MapiMigrationService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MapiMigrationService.class);
    private final CloudSiteService cloudSiteService;
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final Integer MAPI_STATUS_API_BATCH_SIZE = 50;
    @VisibleForTesting
    public static final JobValidationException UNAUTHORISED_DESTINATION = new JobValidationException("Destination cloud site is not authorised with server. Please Authorise the CloudSite and retry.");
    public static final JobValidationException JOB_ID_NOT_FOUND = new JobValidationException("Migration Job Definition not found for JobId. Please recheck JobId and try again.");

    public MapiMigrationService(CloudSiteService cloudSiteService, EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        this.cloudSiteService = cloudSiteService;
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
    }

    public JobDefinition getMigrationJobDefinition(String jobId, Optional<String> cloudId) {
        JobDetails jobDetails;
        log.info("Fetching the job from the MAPI using jobId: {}", (Object)jobId);
        Optional<Object> containerToken = Optional.empty();
        if (cloudId.isPresent()) {
            Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(cloudId.get());
            if (cloudSite.isPresent()) {
                containerToken = Optional.of(cloudSite.get().getContainerToken());
            }
        } else {
            containerToken = this.cloudSiteService.getNonFailingToken();
        }
        if (!containerToken.isPresent()) {
            throw UNAUTHORISED_DESTINATION;
        }
        try {
            jobDetails = this.toJobDefinition(this.enterpriseGatekeeperClient.getMigrationJobDefinition(jobId, (String)containerToken.get()), jobId);
        }
        catch (Exception e) {
            log.info("Error while getting job definition for job: {}", (Object)jobId, (Object)e.getMessage());
            if (e.getCause() instanceof HttpServiceException) {
                HttpServiceException httpException = (HttpServiceException)e.getCause();
                int statusCode = httpException.getStatusCode();
                if (statusCode == 401) {
                    throw UNAUTHORISED_DESTINATION;
                }
                if (statusCode == 404) {
                    throw JOB_ID_NOT_FOUND;
                }
            }
            throw e;
        }
        if (jobDetails instanceof JobDefinitionCloudId) {
            return this.getMigrationJobDefinition(jobId, Optional.of(((JobDefinitionCloudId)jobDetails).getExpectedCloudId()));
        }
        return (JobDefinition)jobDetails;
    }

    private JobDetails toJobDefinition(JobDefinitionResponse jobDefinitionResponse, String jobId) {
        if (jobDefinitionResponse.getJobDefinition() != null) {
            JobDefinition jobDefinition;
            try {
                jobDefinition = Jsons.readValue(jobDefinitionResponse.getJobDefinition(), JobDefinition.class);
            }
            catch (Exception e) {
                log.error("Migration Definition parsing error for job: {}", (Object)jobId);
                throw new PublicApiException.MigrationDefinitionParsingError(jobId, e);
            }
            jobDefinition.setMigrationId(jobDefinitionResponse.getMigrationId());
            return jobDefinition;
        }
        if (Strings.isNullOrEmpty((String)jobDefinitionResponse.getMessage())) {
            throw new PublicApiException.MigrationDefinitionParsingError(jobId, new RuntimeException("Cloud ID Can not be null/blank for METHOD_NOT_ALLOWED response"));
        }
        return new JobDefinitionCloudId(jobDefinitionResponse.getMessage());
    }

    public void sendTaskStatus(String jobId, String taskId, String cloudId, List<MapiStatusDto> mapiStatusDtoList) {
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(cloudId);
        if (!cloudSite.isPresent()) {
            throw new PublicApiException.CloudIdDoesNotExist(cloudId);
        }
        Lists.partition(mapiStatusDtoList, (int)this.MAPI_STATUS_API_BATCH_SIZE).forEach(mapiStatuses -> this.enterpriseGatekeeperClient.sendMapiTaskStatus(jobId, taskId, cloudId, ((CloudSite)cloudSite.get()).getContainerToken(), (List<MapiStatusDto>)mapiStatuses));
    }
}

