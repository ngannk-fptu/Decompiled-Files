/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.SequenceNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;

public interface NameConverters {
    public TableNameConverter getTableNameConverter();

    public FieldNameConverter getFieldNameConverter();

    public SequenceNameConverter getSequenceNameConverter();

    public TriggerNameConverter getTriggerNameConverter();

    public IndexNameConverter getIndexNameConverter();

    public UniqueNameConverter getUniqueNameConverter();
}

