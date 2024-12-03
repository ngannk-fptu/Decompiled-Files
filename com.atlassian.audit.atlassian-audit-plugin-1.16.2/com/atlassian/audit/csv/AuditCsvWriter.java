/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.csv;

import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.encoder.DefaultCsvEncoder;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class AuditCsvWriter
implements Closeable {
    private static final String I18N_TIMESTAMP = "atlassian.audit.common.timestamp";
    private static final String I18N_AUTHOR = "atlassian.audit.common.author";
    private static final String I18N_AUTHOR_ID = "atlassian.audit.common.author.id";
    private static final String I18N_SUMMARY = "atlassian.audit.common.summary";
    private static final String I18N_SYSTEM = "atlassian.audit.common.system";
    private static final String I18N_CATEGORY = "atlassian.audit.common.category";
    private static final String I18N_OBJECTS_AFFECTED = "atlassian.audit.common.objects.affected";
    private static final String I18N_CHANGED_VALUE = "atlassian.audit.common.changed.value";
    private static final String I18N_SOURCE = "atlassian.audit.common.source";
    private static final String I18N_METHOD = "atlassian.audit.common.method";
    private static final String I18N_NODE = "atlassian.audit.common.node";
    private static final String I18N_EXTRA_ATTRIBUTES = "atlassian.audit.common.extra.attributes";
    private static final String I18N_OBJECTS_AFFECTED_CELL_NAME = "atlassian.audit.common.objects.affected.cell.name";
    private static final String I18N_OBJECTS_AFFECTED_CELL_TYPE = "atlassian.audit.common.objects.affected.cell.type";
    private static final String I18N_OBJECTS_AFFECTED_CELL_ID = "atlassian.audit.common.objects.affected.cell.id";
    private static final String I18N_OBJECTS_AFFECTED_CELL_URI = "atlassian.audit.common.objects.affected.cell.uri";
    private static final String I18N_CHANGED_VALUE_CELL_NAME = "atlassian.audit.common.changed.values.cell.name";
    private static final String I18N_CHANGED_VALUE_CELL_FROM = "atlassian.audit.common.changed.values.cell.from";
    private static final String I18N_CHANGED_VALUE_CELL_TO = "atlassian.audit.common.changed.values.cell.to";
    private static final String I18N_EXTRA_ATTRIBUTES_CELL_NAME = "atlassian.audit.common.extra.attributes.cell.name";
    private static final String I18N_EXTRA_ATTRIBUTES_CELL_VALUE = "atlassian.audit.common.extra.attributes.cell.value";
    private static final Logger log = LoggerFactory.getLogger(AuditCsvWriter.class);
    private final String[] headingsRef;
    private CsvMapWriter csvMapWriter;
    private I18nResolver resolver;

    public AuditCsvWriter(I18nResolver resolver, OutputStream stream) {
        this.headingsRef = new String[]{resolver.getText(I18N_TIMESTAMP), resolver.getText(I18N_AUTHOR), resolver.getText(I18N_AUTHOR_ID), resolver.getText(I18N_CATEGORY), resolver.getText(I18N_SUMMARY), resolver.getText(I18N_SYSTEM), resolver.getText(I18N_OBJECTS_AFFECTED), resolver.getText(I18N_CHANGED_VALUE), resolver.getText(I18N_SOURCE), resolver.getText(I18N_METHOD), resolver.getText(I18N_NODE), resolver.getText(I18N_EXTRA_ATTRIBUTES)};
        this.csvMapWriter = this.createCsvWriter(stream);
        this.resolver = resolver;
    }

    public void appendHeader() {
        try {
            this.csvMapWriter.writeHeader(this.headingsRef);
        }
        catch (IOException e) {
            log.error("Failed to write header to outputStream", (Throwable)e);
        }
    }

    public void appendRow(@Nonnull AuditEntity auditEntity) {
        Objects.requireNonNull(auditEntity, "auditEntity");
        ImmutableMap csvLine = ImmutableMap.builder().put((Object)this.headingsRef[0], (Object)auditEntity.getTimestamp().atZone(ZoneId.systemDefault()).toString()).put((Object)this.headingsRef[1], (Object)Optional.ofNullable(auditEntity.getAuthor().getName()).orElse("")).put((Object)this.headingsRef[2], (Object)auditEntity.getAuthor().getId()).put((Object)this.headingsRef[3], (Object)Optional.ofNullable(auditEntity.getAuditType().getCategory()).orElse("")).put((Object)this.headingsRef[4], (Object)Optional.ofNullable(auditEntity.getAuditType().getAction()).orElse("")).put((Object)this.headingsRef[5], (Object)Optional.ofNullable(auditEntity.getSystem()).orElse("")).put((Object)this.headingsRef[6], (Object)this.affectedObjectsToString(auditEntity.getAffectedObjects())).put((Object)this.headingsRef[7], (Object)this.changedValuesToString(auditEntity.getChangedValues())).put((Object)this.headingsRef[8], (Object)Optional.ofNullable(auditEntity.getSource()).orElse("")).put((Object)this.headingsRef[9], (Object)Optional.ofNullable(auditEntity.getMethod()).orElse("")).put((Object)this.headingsRef[10], (Object)Optional.ofNullable(auditEntity.getNode()).orElse("")).put((Object)this.headingsRef[11], (Object)this.extraAttributesToString(auditEntity.getExtraAttributes())).build();
        try {
            this.csvMapWriter.write((Map<String, ?>)csvLine, this.headingsRef);
        }
        catch (IOException e) {
            log.error("Failed to append row to outputStream", (Throwable)e);
        }
    }

    private CsvMapWriter createCsvWriter(OutputStream stream) {
        return new CsvMapWriter(new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8)), new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useEncoder(new DefaultCsvEncoder()).build());
    }

    private String affectedObjectsToString(List<AuditResource> affectedObjects) {
        return affectedObjects.stream().map(affectedObject -> {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(this.resolver.getText(I18N_OBJECTS_AFFECTED_CELL_NAME, new Serializable[]{affectedObject.getName()}));
            joiner.add(this.resolver.getText(I18N_OBJECTS_AFFECTED_CELL_TYPE, new Serializable[]{affectedObject.getType()}));
            this.appendIfNotNull(joiner, I18N_OBJECTS_AFFECTED_CELL_ID, affectedObject.getId());
            this.appendIfNotNull(joiner, I18N_OBJECTS_AFFECTED_CELL_URI, affectedObject.getUri());
            return joiner.toString();
        }).collect(Collectors.joining(", "));
    }

    private void appendIfNotNull(StringJoiner joiner, String key, String value) {
        if (value != null) {
            joiner.add(this.resolver.getText(key, new Serializable[]{value}));
        }
    }

    private String changedValuesToString(List<ChangedValue> values) {
        return values.stream().map(changedValue -> {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(this.resolver.getText(I18N_CHANGED_VALUE_CELL_NAME, new Serializable[]{changedValue.getKey()}));
            this.appendIfNotNull(joiner, I18N_CHANGED_VALUE_CELL_FROM, changedValue.getFrom());
            this.appendIfNotNull(joiner, I18N_CHANGED_VALUE_CELL_TO, changedValue.getTo());
            return joiner.toString();
        }).collect(Collectors.joining(", "));
    }

    private String extraAttributesToString(Collection<AuditAttribute> values) {
        return values.stream().sorted(Comparator.comparing(AuditAttribute::getName)).map(auditAttribute -> {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(this.resolver.getText(I18N_EXTRA_ATTRIBUTES_CELL_NAME, new Serializable[]{auditAttribute.getName()}));
            this.appendIfNotNull(joiner, I18N_EXTRA_ATTRIBUTES_CELL_VALUE, auditAttribute.getValue());
            return joiner.toString();
        }).collect(Collectors.joining(", "));
    }

    @Override
    public void close() throws IOException {
        this.csvMapWriter.close();
    }
}

