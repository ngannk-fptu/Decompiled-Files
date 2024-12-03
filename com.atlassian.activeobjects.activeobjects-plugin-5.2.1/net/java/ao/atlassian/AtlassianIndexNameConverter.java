/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.DefaultIndexNameConverter;
import net.java.ao.schema.IndexNameConverter;

public final class AtlassianIndexNameConverter
implements IndexNameConverter {
    private final IndexNameConverter delegate;

    public AtlassianIndexNameConverter() {
        this(new DefaultIndexNameConverter());
    }

    public AtlassianIndexNameConverter(IndexNameConverter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
    }

    @Override
    public String getName(String tableName, String columnName) {
        return Common.shorten(this.delegate.getName(tableName, columnName), 30);
    }

    @Override
    public String getPrefix(String tableName) {
        return Common.prefix(this.delegate.getPrefix(tableName), 30);
    }
}

