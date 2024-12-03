/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.types.ListTypeBuilder
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.types.ListTypeBuilder;
import java.util.function.Function;

public class CollapsableListTypeBuilder
extends ListTypeBuilder {
    public CollapsableListTypeBuilder(GraphQLTypeBuilder elementTypeBuilder) {
        super(elementTypeBuilder);
    }

    protected Object transformList(Function<Object, Object> elementValueTransformer, Object obj) {
        return obj instanceof Collapsed ? null : super.transformList(elementValueTransformer, obj);
    }
}

