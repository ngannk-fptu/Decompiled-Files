/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.DefaultTriggerNameConverter;
import net.java.ao.schema.TriggerNameConverter;

public final class AtlassianTriggerNameConverter
implements TriggerNameConverter {
    private final TriggerNameConverter delegate;

    public AtlassianTriggerNameConverter() {
        this(new DefaultTriggerNameConverter());
    }

    public AtlassianTriggerNameConverter(TriggerNameConverter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
    }

    @Override
    public String autoIncrementName(String tableName, String columnName) {
        return Common.shorten(this.delegate.autoIncrementName(tableName, columnName), 30);
    }
}

