/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.DefaultSequenceNameConverter;
import net.java.ao.schema.SequenceNameConverter;

public final class AtlassianSequenceNameConverter
implements SequenceNameConverter {
    private final SequenceNameConverter delegate;

    public AtlassianSequenceNameConverter() {
        this(new DefaultSequenceNameConverter());
    }

    public AtlassianSequenceNameConverter(SequenceNameConverter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
    }

    @Override
    public String getName(String tableName, String columnName) {
        return Common.shorten(this.delegate.getName(tableName, columnName), 30);
    }
}

