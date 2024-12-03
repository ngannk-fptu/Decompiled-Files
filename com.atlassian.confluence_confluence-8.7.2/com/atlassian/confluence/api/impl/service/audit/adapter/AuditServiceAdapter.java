/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.audit.RetentionPeriod
 *  com.atlassian.confluence.api.service.audit.AuditService
 *  com.atlassian.confluence.api.service.audit.AuditService$AuditCSVWriter
 *  com.atlassian.confluence.api.service.audit.AuditService$AuditRecordFinder
 *  com.atlassian.confluence.api.service.audit.AuditService$Validator
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.api.impl.service.audit.adapter;

import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.api.impl.service.audit.AuditCSVWriterImpl;
import com.atlassian.confluence.api.impl.service.audit.AuditRecordValidator;
import com.atlassian.confluence.api.impl.service.audit.adapter.AdapterUtils;
import com.atlassian.confluence.api.impl.service.audit.adapter.AuditRecordFinderAdapter;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.RetentionPeriod;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.internal.audit.AuditFormatConverter;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Transactional
public class AuditServiceAdapter
implements com.atlassian.confluence.api.service.audit.AuditService {
    private final AuditService service;
    private final AuditSearchService searchService;
    private final AuditFormatConverter formatConverter;
    private final PaginationService paginationService;
    private final AuditRecordValidator validator;
    private final AuditRetentionConfigService configService;
    private final TimeZoneManager timeZoneManager;
    private final SettingsManager settingsManager;
    private final I18NBeanFactory i18NBeanFactory;

    public AuditServiceAdapter(AuditService service, AuditSearchService searchService, AuditFormatConverter formatConverter, PaginationService paginationService, AuditRecordValidator validator, AuditRetentionConfigService configService, TimeZoneManager timeZoneManager, SettingsManager settingsManager, I18NBeanFactory i18NBeanFactory) {
        this.service = service;
        this.searchService = searchService;
        this.formatConverter = formatConverter;
        this.paginationService = paginationService;
        this.validator = validator;
        this.configService = configService;
        this.timeZoneManager = timeZoneManager;
        this.settingsManager = settingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public AuditRecord storeRecord(AuditRecord record) {
        this.validator().validateCreate(record).throwIfNotSuccessful("Could not create audit record");
        AuditEvent event = this.formatConverter.toAuditEvent(record, CoverageArea.ECOSYSTEM, CoverageLevel.BASE);
        this.service.audit(event);
        return record;
    }

    @Transactional(readOnly=true)
    public AuditService.AuditRecordFinder getRecords(@Nullable Instant startDate, @Nullable Instant endDate) {
        return new AuditRecordFinderAdapter(this.searchService, this.formatConverter, this.paginationService, startDate, endDate, null);
    }

    @Transactional(readOnly=true)
    public RetentionPeriod getRetentionPeriod() {
        Period currentPeriod = this.configService.getConfig().getPeriod();
        if (currentPeriod.getYears() > 0) {
            return RetentionPeriod.of((int)currentPeriod.getYears(), (ChronoUnit)ChronoUnit.YEARS);
        }
        if (currentPeriod.getMonths() > 0) {
            return RetentionPeriod.of((int)currentPeriod.getMonths(), (ChronoUnit)ChronoUnit.MONTHS);
        }
        return RetentionPeriod.of((int)currentPeriod.getDays(), (ChronoUnit)ChronoUnit.DAYS);
    }

    public RetentionPeriod setRetentionPeriod(RetentionPeriod retentionPeriod) {
        this.configService.updateConfig(new AuditRetentionConfig(AdapterUtils.toPeriod(retentionPeriod)));
        return retentionPeriod;
    }

    public void deleteRecords(Instant before) {
    }

    public AuditService.Validator validator() {
        return this.validator;
    }

    public AuditService.AuditCSVWriter exportCSV() {
        return new AuditCSVWriterImpl(this.timeZoneManager, this.settingsManager, this.i18NBeanFactory);
    }
}

