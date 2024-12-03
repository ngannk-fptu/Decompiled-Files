/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.service.audit;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.RetentionPeriod;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
@ExperimentalApi
public interface AuditService {
    public AuditRecord storeRecord(AuditRecord var1) throws ServiceException;

    public AuditRecordFinder getRecords(@Nullable Instant var1, @Nullable Instant var2);

    public RetentionPeriod getRetentionPeriod();

    public RetentionPeriod setRetentionPeriod(RetentionPeriod var1) throws ServiceException;

    @Deprecated
    public void deleteRecords(Instant var1);

    public Validator validator();

    public AuditCSVWriter exportCSV();

    @Deprecated
    public static interface AuditRecordFinder
    extends ManyFetcher<AuditRecord> {
        public AuditRecordFinder withSearchString(String var1);
    }

    @Deprecated
    public static interface AuditCSVWriter {
        public void write(OutputStream var1) throws IOException;

        public AuditCSVWriter withFinder(AuditRecordFinder var1);
    }

    @Deprecated
    public static interface Validator {
        public ValidationResult validateCreate(AuditRecord var1) throws ServiceException;

        public ValidationResult validateDelete(Instant var1);
    }
}

