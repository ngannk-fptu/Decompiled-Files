/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.Objects;
import net.java.ao.RawEntity;
import net.java.ao.schema.TableNameConverter;

public abstract class CanonicalClassNameTableNameConverter
implements TableNameConverter {
    @Override
    public final String getName(Class<? extends RawEntity<?>> entityClass) {
        return this.getName(Objects.requireNonNull(entityClass).getCanonicalName());
    }

    protected abstract String getName(String var1);
}

