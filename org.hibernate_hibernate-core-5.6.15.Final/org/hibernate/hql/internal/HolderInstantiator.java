/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public final class HolderInstantiator {
    public static final HolderInstantiator NOOP_INSTANTIATOR = new HolderInstantiator(null);
    private final ResultTransformer transformer;
    private Supplier<String[]> queryReturnAliasesSupplier = () -> null;

    public static HolderInstantiator getHolderInstantiator(ResultTransformer selectNewTransformer, ResultTransformer customTransformer, String[] queryReturnAliases) {
        return new HolderInstantiator(HolderInstantiator.resolveResultTransformer(selectNewTransformer, customTransformer), queryReturnAliases);
    }

    public static ResultTransformer resolveResultTransformer(ResultTransformer selectNewTransformer, ResultTransformer customTransformer) {
        return selectNewTransformer != null ? selectNewTransformer : customTransformer;
    }

    public static ResultTransformer createSelectNewTransformer(Constructor constructor, boolean returnMaps, boolean returnLists) {
        if (constructor != null) {
            return new AliasToBeanConstructorResultTransformer(constructor);
        }
        if (returnMaps) {
            return Transformers.ALIAS_TO_ENTITY_MAP;
        }
        if (returnLists) {
            return Transformers.TO_LIST;
        }
        return null;
    }

    public static HolderInstantiator createClassicHolderInstantiator(Constructor constructor, ResultTransformer transformer) {
        return new HolderInstantiator(HolderInstantiator.resolveClassicResultTransformer(constructor, transformer));
    }

    public static ResultTransformer resolveClassicResultTransformer(Constructor constructor, ResultTransformer transformer) {
        return constructor != null ? new AliasToBeanConstructorResultTransformer(constructor) : transformer;
    }

    public HolderInstantiator(ResultTransformer transformer) {
        this.transformer = transformer;
    }

    public HolderInstantiator(ResultTransformer transformer, String[] queryReturnAliases) {
        this.transformer = transformer;
        this.queryReturnAliasesSupplier = () -> queryReturnAliases;
    }

    public HolderInstantiator(ResultTransformer transformer, Supplier<String[]> queryReturnAliasesSupplier) {
        this.transformer = transformer;
        this.queryReturnAliasesSupplier = queryReturnAliasesSupplier;
    }

    public boolean isRequired() {
        return this.transformer != null;
    }

    public Object instantiate(Object[] row) {
        if (this.transformer == null) {
            return row;
        }
        return this.transformer.transformTuple(row, this.getQueryReturnAliases());
    }

    public String[] getQueryReturnAliases() {
        return this.queryReturnAliasesSupplier.get();
    }

    public ResultTransformer getResultTransformer() {
        return this.transformer;
    }
}

