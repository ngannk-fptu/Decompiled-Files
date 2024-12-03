/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.DefaultUniqueNameConverter;
import net.java.ao.schema.UniqueNameConverter;

public final class AtlassianUniqueNameConverter
implements UniqueNameConverter {
    private final UniqueNameConverter delegate;

    public AtlassianUniqueNameConverter() {
        this(new DefaultUniqueNameConverter());
    }

    public AtlassianUniqueNameConverter(UniqueNameConverter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
    }

    @Override
    public String getName(String tableName, String columnName) {
        return Common.shorten(this.delegate.getName(tableName, columnName), 30);
    }
}

