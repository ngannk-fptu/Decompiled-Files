/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.Objects;
import net.java.ao.schema.CachingTableNameConverter;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SequenceNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;

public final class CachingNameConverters
implements NameConverters {
    private final TableNameConverter tableNameConverter;
    private final FieldNameConverter fieldNameConverter;
    private final SequenceNameConverter sequenceNameConverter;
    private final TriggerNameConverter triggerNameConverter;
    private final IndexNameConverter indexNameConverter;
    private final UniqueNameConverter uniqueNameConverter;

    public CachingNameConverters(NameConverters nameConverters) {
        Objects.requireNonNull(nameConverters, "nameConverters can't be null");
        this.tableNameConverter = new CachingTableNameConverter(nameConverters.getTableNameConverter());
        this.fieldNameConverter = nameConverters.getFieldNameConverter();
        this.sequenceNameConverter = nameConverters.getSequenceNameConverter();
        this.triggerNameConverter = nameConverters.getTriggerNameConverter();
        this.indexNameConverter = nameConverters.getIndexNameConverter();
        this.uniqueNameConverter = nameConverters.getUniqueNameConverter();
    }

    @Override
    public TableNameConverter getTableNameConverter() {
        return this.tableNameConverter;
    }

    @Override
    public FieldNameConverter getFieldNameConverter() {
        return this.fieldNameConverter;
    }

    @Override
    public SequenceNameConverter getSequenceNameConverter() {
        return this.sequenceNameConverter;
    }

    @Override
    public TriggerNameConverter getTriggerNameConverter() {
        return this.triggerNameConverter;
    }

    @Override
    public IndexNameConverter getIndexNameConverter() {
        return this.indexNameConverter;
    }

    @Override
    public UniqueNameConverter getUniqueNameConverter() {
        return this.uniqueNameConverter;
    }
}

