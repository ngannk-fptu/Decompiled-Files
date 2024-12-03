/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.graphql.extensions;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ExpansionFieldsExtension
implements GraphQLExtensions {
    public boolean isExpansionField(Member accessor) {
        Field field;
        return accessor instanceof Field && ((field = (Field)accessor).getType() == Reference.class || field.getType() == List.class || field.getType() == Map.class);
    }

    public List<String> getExpansionRootPaths(Type type) {
        return PageResponse.class.isAssignableFrom(ReflectionUtils.getClazz((Type)type)) || GraphQLPagination.class.isAssignableFrom(ReflectionUtils.getClazz((Type)type)) ? Lists.newArrayList((Object[])new String[]{"edges.node", "nodes"}) : null;
    }
}

