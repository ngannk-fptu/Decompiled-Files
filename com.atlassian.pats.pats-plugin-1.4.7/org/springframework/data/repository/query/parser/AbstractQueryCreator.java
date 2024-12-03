/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query.parser;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractQueryCreator<T, S> {
    private final Optional<ParameterAccessor> parameters;
    private final PartTree tree;

    public AbstractQueryCreator(PartTree tree) {
        this(tree, Optional.empty());
    }

    public AbstractQueryCreator(PartTree tree, ParameterAccessor parameters) {
        this(tree, Optional.of(parameters));
    }

    private AbstractQueryCreator(PartTree tree, Optional<ParameterAccessor> parameters) {
        Assert.notNull((Object)tree, (String)"PartTree must not be null");
        Assert.notNull(parameters, (String)"ParameterAccessor must not be null");
        this.tree = tree;
        this.parameters = parameters;
    }

    public T createQuery() {
        return this.createQuery(this.parameters.map(ParameterAccessor::getSort).orElse(Sort.unsorted()));
    }

    public T createQuery(Sort dynamicSort) {
        Assert.notNull((Object)dynamicSort, (String)"DynamicSort must not be null!");
        return this.complete(this.createCriteria(this.tree), this.tree.getSort().and(dynamicSort));
    }

    @Nullable
    private S createCriteria(PartTree tree) {
        S base = null;
        Iterator<Object> iterator = this.parameters.map(ParameterAccessor::iterator).orElse(Collections.emptyIterator());
        for (PartTree.OrPart node : tree) {
            Iterator<Part> parts = node.iterator();
            if (!parts.hasNext()) {
                throw new IllegalStateException(String.format("No part found in PartTree %s!", tree));
            }
            S criteria = this.create(parts.next(), iterator);
            while (parts.hasNext()) {
                criteria = this.and(parts.next(), criteria, iterator);
            }
            base = (S)(base == null ? criteria : this.or(base, criteria));
        }
        return base;
    }

    protected abstract S create(Part var1, Iterator<Object> var2);

    protected abstract S and(Part var1, S var2, Iterator<Object> var3);

    protected abstract S or(S var1, S var2);

    protected abstract T complete(@Nullable S var1, Sort var2);
}

