/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.audit.AuditService$AuditCSVWriter
 *  com.atlassian.confluence.api.service.audit.AuditService$AuditRecordFinder
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  org.supercsv.encoder.CsvEncoder
 *  org.supercsv.encoder.DefaultCsvEncoder
 *  org.supercsv.io.CsvMapWriter
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.prefs.CsvPreference$Builder
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.encoder.DefaultCsvEncoder;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

@Deprecated
public class AuditCSVWriterImpl
implements AuditService.AuditCSVWriter {
    private static final String I18N_PREFIX = "audit.logging.csv.header.";
    private final TimeZoneManager timeZoneManager;
    private final SettingsManager settingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    private AuditService.AuditRecordFinder finder;

    public AuditCSVWriterImpl(TimeZoneManager timeZoneManager, SettingsManager settingsManager, I18NBeanFactory i18NBeanFactory) {
        this.timeZoneManager = timeZoneManager;
        this.settingsManager = settingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void write(OutputStream output) throws IOException {
        int start = 0;
        int limit = 100;
        boolean hasMore = true;
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.getSystemLocale());
        Object[] headings = new String[]{i18NBean.getText("audit.logging.csv.header.date"), i18NBean.getText("audit.logging.csv.header.timezone"), i18NBean.getText("audit.logging.csv.header.category"), i18NBean.getText("audit.logging.csv.header.address"), i18NBean.getText("audit.logging.csv.header.summary"), i18NBean.getText("audit.logging.csv.header.description"), i18NBean.getText("audit.logging.csv.header.author"), i18NBean.getText("audit.logging.csv.header.author.name"), i18NBean.getText("audit.logging.csv.header.changed.object"), i18NBean.getText("audit.logging.csv.header.changed.object.type"), i18NBean.getText("audit.logging.csv.header.details"), i18NBean.getText("audit.logging.csv.header.associated.items")};
        ArrayList headingsList = Lists.newArrayList((Object[])headings);
        CsvMapWriter csvWriter = new CsvMapWriter((Writer)new BufferedWriter(new OutputStreamWriter(output, "UTF-8")), new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useEncoder((CsvEncoder)new DefaultCsvEncoder()).build());
        ZoneId zone = this.timeZoneManager.getDefaultTimeZone().toZoneId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zone);
        String zoneString = zone.getDisplayName(TextStyle.NARROW, this.getSystemLocale());
        csvWriter.writeHeader((String[])headings);
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, limit);
            PageResponse auditRecords = this.finder.fetchMany((PageRequest)request);
            for (AuditRecord auditRecord : auditRecords) {
                HashMap<String, String> values = new HashMap<String, String>();
                Iterator iterator = headingsList.iterator();
                values.put((String)iterator.next(), formatter.format(Instant.ofEpochMilli(auditRecord.getCreationDate().getMillis())));
                values.put((String)iterator.next(), zoneString);
                values.put((String)iterator.next(), auditRecord.getAuthor().getUsername());
                values.put((String)iterator.next(), auditRecord.getAuthor().getDisplayName());
                values.put((String)iterator.next(), auditRecord.getCategory());
                values.put((String)iterator.next(), auditRecord.getRemoteAddress());
                values.put((String)iterator.next(), auditRecord.getSummary());
                values.put((String)iterator.next(), auditRecord.getDescription());
                values.put((String)iterator.next(), auditRecord.getAffectedObject() != null ? auditRecord.getAffectedObject().getName() : "");
                values.put((String)iterator.next(), auditRecord.getAffectedObject() != null ? auditRecord.getAffectedObject().getName() : "");
                values.put((String)iterator.next(), Joiner.on((String)"\r\n").join((Iterable)auditRecord.getChangedValues()));
                values.put((String)iterator.next(), Joiner.on((String)"\r\n").join((Iterable)auditRecord.getAssociatedObjects()));
                csvWriter.write(values, (String[])headings);
            }
            hasMore = auditRecords.hasMore();
            start += limit;
        }
        csvWriter.flush();
    }

    public AuditService.AuditCSVWriter withFinder(AuditService.AuditRecordFinder finder) {
        this.finder = finder;
        return this;
    }

    private Locale getSystemLocale() {
        String localeSetting = this.settingsManager.getGlobalSettings().getGlobalDefaultLocale();
        if (localeSetting == null) {
            return Locale.UK;
        }
        return Locale.forLanguageTag(localeSetting);
    }
}

