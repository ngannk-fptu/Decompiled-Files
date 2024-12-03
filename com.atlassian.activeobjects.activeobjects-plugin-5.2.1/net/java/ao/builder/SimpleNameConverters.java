/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.util.Objects;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SequenceNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;

public final class SimpleNameConverters
implements NameConverters {
    private final TableNameConverter tableNameConverter;
    private final FieldNameConverter fieldNameConverter;
    private final SequenceNameConverter sequenceNameConverter;
    private final TriggerNameConverter triggerNameConverter;
    private final IndexNameConverter indexNameConverter;
    private final UniqueNameConverter uniqueNameConverter;

    public SimpleNameConverters(TableNameConverter tableNameConverter, FieldNameConverter fieldNameConverter, SequenceNameConverter sequenceNameConverter, TriggerNameConverter triggerNameConverter, IndexNameConverter indexNameConverter, UniqueNameConverter uniqueNameConverter) {
        this.tableNameConverter = Objects.requireNonNull(tableNameConverter, "tableNameConverter can't be null");
        this.fieldNameConverter = Objects.requireNonNull(fieldNameConverter, "fieldNameConverter can't be null");
        this.sequenceNameConverter = Objects.requireNonNull(sequenceNameConverter, "sequenceNameConverter can't be null");
        this.triggerNameConverter = Objects.requireNonNull(triggerNameConverter, "triggerNameConverter can't be null");
        this.indexNameConverter = Objects.requireNonNull(indexNameConverter, "indexNameConverter can't be null");
        this.uniqueNameConverter = Objects.requireNonNull(uniqueNameConverter, "uniqueNameConverter can't be null");
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

