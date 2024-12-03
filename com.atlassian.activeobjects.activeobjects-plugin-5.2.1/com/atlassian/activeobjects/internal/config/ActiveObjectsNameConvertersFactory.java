/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.internal.config;

import com.atlassian.activeobjects.ao.AtlassianTablePrefix;
import com.atlassian.activeobjects.internal.Prefix;
import com.atlassian.activeobjects.internal.config.NameConvertersFactory;
import com.google.common.base.Preconditions;
import net.java.ao.atlassian.AtlassianTableNameConverter;
import net.java.ao.builder.SimpleNameConverters;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SequenceNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;

public final class ActiveObjectsNameConvertersFactory
implements NameConvertersFactory {
    private final FieldNameConverter fieldNameConverter;
    private final SequenceNameConverter sequenceNameConverter;
    private final TriggerNameConverter triggerNameConverter;
    private final IndexNameConverter indexNameConverter;
    private final UniqueNameConverter uniqueNameConverter;

    public ActiveObjectsNameConvertersFactory(FieldNameConverter fieldNameConverter, SequenceNameConverter sequenceNameConverter, TriggerNameConverter triggerNameConverter, IndexNameConverter indexNameConverter, UniqueNameConverter uniqueNameConverter) {
        this.fieldNameConverter = (FieldNameConverter)Preconditions.checkNotNull((Object)fieldNameConverter);
        this.sequenceNameConverter = (SequenceNameConverter)Preconditions.checkNotNull((Object)sequenceNameConverter);
        this.triggerNameConverter = (TriggerNameConverter)Preconditions.checkNotNull((Object)triggerNameConverter);
        this.indexNameConverter = (IndexNameConverter)Preconditions.checkNotNull((Object)indexNameConverter);
        this.uniqueNameConverter = (UniqueNameConverter)Preconditions.checkNotNull((Object)uniqueNameConverter);
    }

    @Override
    public NameConverters getNameConverters(Prefix prefix) {
        return new SimpleNameConverters(new AtlassianTableNameConverter(new AtlassianTablePrefix(prefix)), this.fieldNameConverter, this.sequenceNameConverter, this.triggerNameConverter, this.indexNameConverter, this.uniqueNameConverter);
    }
}

