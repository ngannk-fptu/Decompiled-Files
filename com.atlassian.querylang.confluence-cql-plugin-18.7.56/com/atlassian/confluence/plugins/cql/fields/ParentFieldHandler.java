/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.content.ChildContentService$ChildContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.plugins.cql.v2search.query.ContentIdQuery;
import com.atlassian.confluence.plugins.cql.v2search.query.ParentIdQuery;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(ParentFieldHandler.class);
    static final int MAX_TO_FETCH = 1001;
    public static final String SHOULD_USE_LUCENE_ONLY = "confluence.cql.parentfield.lucene";
    private final ContentService contentService;
    private final ChildContentService childContentService;
    private final DarkFeatureManager darkFeatureManager;

    public ParentFieldHandler(@ComponentImport ContentService contentService, @ComponentImport ChildContentService childContentService, @ComponentImport DarkFeatureManager darkFeatureManager) {
        super("parent");
        this.contentService = contentService;
        this.childContentService = childContentService;
        this.darkFeatureManager = darkFeatureManager;
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Collections.emptySet());
        return null;
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        ContentId contentId = (ContentId)V2FieldHandlerHelper.stringToContentId.apply((Object)value);
        Content parent = this.contentService.find(new Expansion[0]).withId(contentId).fetch().orElse(null);
        if (parent == null) {
            return V2FieldHandlerHelper.wrapV2Search((SearchQuery)MatchNoDocsQuery.getInstance(), (ExpressionData)expressionData);
        }
        if (!this.shouldUserLuceneOnly(parent)) {
            logger.debug("Using DB for Parent Field Handler");
            return this.getContentIdQuery(expressionData, contentId, parent);
        }
        logger.debug("Using Lucene for Parent Field Handler");
        return this.getParentFieldIdQuery(expressionData, contentId, parent);
    }

    private boolean shouldUserLuceneOnly(Content parent) {
        return (ContentType.COMMENT.equals((Object)parent.getType()) || ContentType.PAGE.equals((Object)parent.getType())) && this.darkFeatureManager.isEnabledForAllUsers(SHOULD_USE_LUCENE_ONLY).orElse(false) != false;
    }

    private V2SearchQueryWrapper getParentFieldIdQuery(EqualityExpressionData expressionData, ContentId contentId, Content parent) {
        V2SearchQueryWrapper v2SearchQueryWrapper = V2FieldHandlerHelper.wrapV2Search((SearchQuery)MatchNoDocsQuery.getInstance(), (ExpressionData)expressionData);
        ParentIdQuery query = new ParentIdQuery(contentId);
        v2SearchQueryWrapper = V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
        return v2SearchQueryWrapper;
    }

    private V2SearchQueryWrapper getContentIdQuery(EqualityExpressionData expressionData, ContentId contentId, Content parent) {
        ImmutableList supportedClasses;
        Collection<Content> children;
        if (ContentType.PAGE.equals((Object)parent.getType())) {
            children = this.fetchChildren(this.childContentService.findContent(contentId, new Expansion[0]), ContentType.PAGE);
            supportedClasses = ImmutableList.builder().add(Page.class).build();
        } else if (ContentType.COMMENT.equals((Object)parent.getType())) {
            children = this.fetchChildren(this.childContentService.findContent(contentId, new Expansion[0]), ContentType.COMMENT);
            supportedClasses = ImmutableList.builder().add(Comment.class).build();
        } else {
            return V2FieldHandlerHelper.wrapV2Search((SearchQuery)MatchNoDocsQuery.getInstance(), (ExpressionData)expressionData);
        }
        if (children.isEmpty()) {
            return V2FieldHandlerHelper.wrapV2Search((SearchQuery)MatchNoDocsQuery.getInstance(), (ExpressionData)expressionData);
        }
        ContentIdQuery query = new ContentIdQuery(children.stream().map(Content::getId).collect(Collectors.toList()), (List<Class<? extends Searchable>>)supportedClasses);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    private Collection<Content> fetchChildren(ChildContentService.ChildContentFinder finder, ContentType type) {
        PageResponse response;
        Stream results = Stream.empty();
        SimplePageRequest pageRequest = new SimplePageRequest(0, 1001);
        int count = 0;
        do {
            response = finder.fetchMany(type, (PageRequest)pageRequest);
            results = Stream.concat(results, response.getResults().stream());
            int offset = response.getPageRequest().getStart() + response.getPageRequest().getLimit();
            pageRequest = new SimplePageRequest(offset, 1001 - offset);
        } while (response.hasMore() && (count += response.size()) < 1001);
        return results.collect(Collectors.toList());
    }
}

