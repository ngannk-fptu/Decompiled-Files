/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.csv;

import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.csv.AuditCsvWriter;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditCsvExporter {
    private static final Logger log = LoggerFactory.getLogger(AuditCsvExporter.class);
    private final AuditSearchService searchService;
    private final AuditQuery query;
    private final I18nResolver resolver;

    public AuditCsvExporter(AuditSearchService searchService, AuditQuery query, I18nResolver resolver) {
        this.searchService = searchService;
        this.query = query;
        this.resolver = resolver;
    }

    public void export(@Nonnull OutputStream stream, int offset, int limit) {
        Objects.requireNonNull(stream, "stream");
        try (AuditCsvWriter writer = new AuditCsvWriter(this.resolver, stream);){
            writer.appendHeader();
            this.searchService.stream(this.query, offset, limit, writer::appendRow);
        }
        catch (TimeoutException e) {
            log.error("Failed to write Audit Log to a CSV because it took too long", (Throwable)e);
        }
        catch (IOException e) {
            log.error("Failed to close mapWriter", (Throwable)e);
        }
    }
}

