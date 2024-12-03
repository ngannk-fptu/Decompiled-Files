/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.dto.InstanceMetadataDto;
import com.atlassian.migration.agent.dto.ProductDto;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceMetadataCollector {
    private final Logger log = LoggerFactory.getLogger(InstanceMetadataCollector.class);
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final SENSupplier senSupplier;
    private final SystemInformationService systemInformationService;
    private final GuardrailsResponseGroupStore guardrailsResponseGroupStore;
    private final LicenseHandler licenseHandler;
    private final TimeZoneManager timeZoneManager;

    public InstanceMetadataCollector(SENSupplier senSupplier, SystemInformationService systemInformationService, GuardrailsResponseGroupStore guardrailsResponseGroupStore, LicenseHandler licenseHandler, TimeZoneManager timeZoneManager) {
        this.senSupplier = senSupplier;
        this.systemInformationService = systemInformationService;
        this.guardrailsResponseGroupStore = guardrailsResponseGroupStore;
        this.licenseHandler = licenseHandler;
        this.timeZoneManager = timeZoneManager;
    }

    InstanceMetadataDto collectMetadata(String jobId) {
        this.log.info("Collecting Instance metadata.");
        String serverID = this.licenseHandler.getServerId();
        GuardrailsResponseGroup grResponseGroup = this.guardrailsResponseGroupStore.getResponseGroupByJobId(jobId);
        TimeZone timeZone = this.timeZoneManager.getDefaultTimeZone();
        String offsetHours = this.getOffsetInHours(timeZone);
        String gmtString = this.createGMTString(offsetHours);
        String startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(grResponseGroup != null ? grResponseGroup.getStartTimestamp() : 0L), timeZone.toZoneId()).format(this.dateTimeFormat);
        String sen = "";
        if (this.senSupplier.get() != null) {
            sen = this.senSupplier.get();
        }
        this.log.info("Instance metadata collected.");
        return new InstanceMetadataDto(new ProductDto("Confluence", this.systemInformationService.getConfluenceInfo().getVersion()), sen, serverID, gmtString, startDateTime);
    }

    private String getOffsetInHours(TimeZone timeZone) {
        int offset = timeZone.getRawOffset();
        return Integer.toString(offset / 1000 / 60 / 60);
    }

    private String createGMTString(String offset) {
        return String.format("GMT%s%s", offset.startsWith("-") ? "" : "+", offset);
    }
}

