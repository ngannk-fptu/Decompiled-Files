/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import java.util.Objects;
import net.java.ao.RawEntity;
import net.java.ao.atlassian.TablePrefix;
import net.java.ao.schema.TableNameConverter;

final class PrefixedTableNameConverter
implements TableNameConverter {
    private final TablePrefix prefix;
    private final TableNameConverter delegate;

    public PrefixedTableNameConverter(TablePrefix prefix, TableNameConverter delegate) {
        this.prefix = Objects.requireNonNull(prefix, "prefix can't be null");
        this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
    }

    @Override
    public String getName(Class<? extends RawEntity<?>> clazz) {
        return this.prefix.prepend(this.delegate.getName(clazz));
    }
}

