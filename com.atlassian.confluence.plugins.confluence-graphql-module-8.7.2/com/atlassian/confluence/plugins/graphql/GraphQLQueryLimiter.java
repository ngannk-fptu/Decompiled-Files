/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.graphql.rest.GraphQLRestRequest
 *  graphql.language.Definition
 *  graphql.language.Document
 *  graphql.language.Field
 *  graphql.language.FragmentDefinition
 *  graphql.language.FragmentSpread
 *  graphql.language.InlineFragment
 *  graphql.language.OperationDefinition
 *  graphql.language.Selection
 *  graphql.language.SelectionSet
 */
package com.atlassian.confluence.plugins.graphql;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.graphql.rest.GraphQLRestRequest;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphQLQueryLimiter {
    private static final int ALLOWED_PAGINATION_QUERIES = 15;

    public void checkQuery(List<GraphQLRestRequest> requests) {
        int count = this.countPaginationQueries(requests);
        if (count > 15) {
            throw new BadRequestException("GraphQL throttle exceeded");
        }
    }

    int countPaginationQueries(List<GraphQLRestRequest> requests) {
        int count = 0;
        for (GraphQLRestRequest request : requests) {
            count += GraphQLQueryLimiter.visit(request.getQueryDocument(), false);
        }
        return count;
    }

    private static int visit(Document document, boolean hasChildrenNode) {
        int count = 0;
        Map<String, FragmentDefinition> fragments = GraphQLQueryLimiter.getFragmentsMap(document);
        for (Definition definition : document.getDefinitions()) {
            if (!(definition instanceof OperationDefinition)) continue;
            count += GraphQLQueryLimiter.visit(((OperationDefinition)definition).getSelectionSet(), fragments, true, hasChildrenNode);
        }
        return count;
    }

    private static int visit(SelectionSet selectionSet, Map<String, FragmentDefinition> fragments, boolean checkPaginationFields, boolean hasChildrenNode) {
        if (selectionSet == null) {
            return 0;
        }
        int count = 0;
        if (checkPaginationFields && GraphQLQueryLimiter.hasPaginationQuery(selectionSet, fragments)) {
            ++count;
        }
        for (Selection selection : selectionSet.getSelections()) {
            if (selection instanceof InlineFragment) {
                count += GraphQLQueryLimiter.visit(((InlineFragment)selection).getSelectionSet(), fragments, false, hasChildrenNode);
                continue;
            }
            if (selection instanceof FragmentSpread) {
                count += GraphQLQueryLimiter.visitFragmentSpread(fragments, ((FragmentSpread)selection).getName(), checkPaginationFields, hasChildrenNode);
                continue;
            }
            if (!(selection instanceof Field)) continue;
            hasChildrenNode = GraphQLQueryLimiter.checkHasChildrenNode((Field)selection, hasChildrenNode);
            count += GraphQLQueryLimiter.visit(((Field)selection).getSelectionSet(), fragments, true, hasChildrenNode);
        }
        return count;
    }

    private static boolean checkHasChildrenNode(Field field, boolean hasChildrenNode) {
        if (field.getName().equals("children")) {
            if (hasChildrenNode) {
                throw new BadRequestException("'children' may not be used recursively");
            }
            return true;
        }
        return hasChildrenNode;
    }

    private static int visitFragmentSpread(Map<String, FragmentDefinition> fragments, String fragmentName, boolean checkPaginationFields, boolean hasChildrenNode) {
        FragmentDefinition fragment = fragments.get(fragmentName);
        if (fragment == null) {
            throw new BadRequestException("Expected a fragment with name '" + fragmentName + "'");
        }
        return GraphQLQueryLimiter.visit(fragment.getSelectionSet(), fragments, checkPaginationFields, hasChildrenNode);
    }

    private static boolean hasPaginationQuery(SelectionSet selectionSet, Map<String, FragmentDefinition> fragments) {
        if (selectionSet == null) {
            return false;
        }
        for (Selection selection : selectionSet.getSelections()) {
            String fieldName;
            if (selection instanceof InlineFragment) {
                return GraphQLQueryLimiter.hasPaginationQuery(((InlineFragment)selection).getSelectionSet(), fragments);
            }
            if (selection instanceof FragmentSpread) {
                return GraphQLQueryLimiter.hasPaginationQuery(fragments, ((FragmentSpread)selection).getName());
            }
            if (!(selection instanceof Field) || !GraphQLQueryLimiter.isPaginationField(fieldName = ((Field)selection).getName())) continue;
            return true;
        }
        return false;
    }

    private static boolean isPaginationField(String fieldName) {
        return fieldName.equals("edges") || fieldName.equals("nodes") || fieldName.equals("count") || fieldName.equals("pageInfo");
    }

    private static boolean hasPaginationQuery(Map<String, FragmentDefinition> fragments, String fragmentName) {
        FragmentDefinition fragment = fragments.get(fragmentName);
        if (fragment == null) {
            throw new BadRequestException("Expected a fragment with name '" + fragmentName + "'");
        }
        return GraphQLQueryLimiter.hasPaginationQuery(fragment.getSelectionSet(), fragments);
    }

    private static Map<String, FragmentDefinition> getFragmentsMap(Document document) {
        HashMap<String, FragmentDefinition> fragments = new HashMap<String, FragmentDefinition>();
        for (Definition definition : document.getDefinitions()) {
            if (!(definition instanceof FragmentDefinition)) continue;
            FragmentDefinition fragmentDefinition = (FragmentDefinition)definition;
            fragments.put(fragmentDefinition.getName(), fragmentDefinition);
        }
        return fragments;
    }
}

