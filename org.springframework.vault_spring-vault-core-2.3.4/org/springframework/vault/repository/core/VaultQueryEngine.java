/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.CriteriaAccessor
 *  org.springframework.data.keyvalue.core.QueryEngine
 *  org.springframework.data.keyvalue.core.SortAccessor
 *  org.springframework.data.keyvalue.core.SpelSortAccessor
 *  org.springframework.data.keyvalue.core.query.KeyValueQuery
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.repository.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.keyvalue.core.CriteriaAccessor;
import org.springframework.data.keyvalue.core.QueryEngine;
import org.springframework.data.keyvalue.core.SortAccessor;
import org.springframework.data.keyvalue.core.SpelSortAccessor;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.vault.repository.core.VaultKeyValueAdapter;
import org.springframework.vault.repository.query.VaultQuery;

class VaultQueryEngine
extends QueryEngine<VaultKeyValueAdapter, VaultQuery, Comparator<?>> {
    private static final SpelExpressionParser parser = new SpelExpressionParser();

    VaultQueryEngine() {
        super((CriteriaAccessor)VaultCriteriaAccessor.INSTANCE, (SortAccessor)new SpelSortAccessor(parser));
    }

    public Collection<?> execute(@Nullable VaultQuery vaultQuery, @Nullable Comparator<?> comparator, long offset, int rows, String keyspace) {
        return this.execute(vaultQuery, comparator, offset, rows, keyspace, Object.class);
    }

    public <T> Collection<T> execute(@Nullable VaultQuery vaultQuery, @Nullable Comparator<?> comparator, long offset, int rows, String keyspace, Class<T> type) {
        Stream<Object> stream = ((VaultKeyValueAdapter)this.getRequiredAdapter()).doList(keyspace).stream();
        if (vaultQuery != null) {
            stream = stream.filter(vaultQuery::test);
        }
        if (comparator == null) {
            if (offset > 0L) {
                stream = stream.skip(offset);
            }
            if (rows > 0) {
                stream = stream.limit(rows);
            }
        }
        Stream<Object> typed = stream.map(it -> ((VaultKeyValueAdapter)this.getRequiredAdapter()).get(it, keyspace, type));
        if (comparator != null) {
            typed = typed.sorted(comparator);
            if (offset > 0L) {
                typed = typed.skip(offset);
            }
            if (rows > 0) {
                typed = typed.limit(rows);
            }
        }
        return typed.collect(Collectors.toCollection(ArrayList::new));
    }

    public long count(@Nullable VaultQuery vaultQuery, String keyspace) {
        Stream<Object> stream = ((VaultKeyValueAdapter)this.getRequiredAdapter()).doList(keyspace).stream();
        if (vaultQuery != null) {
            stream = stream.filter(vaultQuery::test);
        }
        return stream.count();
    }

    static enum VaultCriteriaAccessor implements CriteriaAccessor<VaultQuery>
    {
        INSTANCE;


        public VaultQuery resolve(KeyValueQuery<?> query) {
            return (VaultQuery)query.getCriteria();
        }
    }
}

