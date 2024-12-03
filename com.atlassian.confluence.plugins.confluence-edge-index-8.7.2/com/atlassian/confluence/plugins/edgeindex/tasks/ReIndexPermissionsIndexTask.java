/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.LongFieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.ScannedDocument
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.FieldExistsQuery
 *  com.atlassian.confluence.search.v2.query.LongRangeQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Throwables
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.edgeindex.tasks;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexSchema;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.LongFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.ScannedDocument;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ReIndexPermissionsIndexTask
implements EdgeIndexTask {
    private static final Logger log = LoggerFactory.getLogger(ReIndexPermissionsIndexTask.class);
    private final String contentId;
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate txTemplate;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;
    private final ContentPermissionCalculator contentPermissionCalculator;

    public ReIndexPermissionsIndexTask(String contentId, ContentEntityManager contentEntityManager, TransactionTemplate txTemplate, EdgeSearchIndexAccessor edgeSearchIndexAccessor, ContentPermissionCalculator contentPermissionCalculator) {
        this.contentId = contentId;
        this.contentEntityManager = contentEntityManager;
        this.txTemplate = txTemplate;
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.contentPermissionCalculator = contentPermissionCalculator;
    }

    public String getDescription() {
        return String.format("Reindex permissions task on content ID '%s'.", this.contentId);
    }

    public SearchIndex getSearchIndex() {
        return SearchIndex.CUSTOM;
    }

    public void perform(SearchIndexWriter searchIndexWriter) {
        this.txTemplate.execute(() -> {
            try {
                this.doReindex(searchIndexWriter);
                return null;
            }
            catch (IOException e) {
                return Throwables.propagate((Throwable)e);
            }
        });
    }

    private void doReindex(SearchIndexWriter writer) throws IOException {
        log.debug("Beginning permissions re-indexing process for content {}", (Object)this.contentId);
        ContentEntityObject contentEntity = this.contentEntityManager.getById(Long.parseLong(this.contentId));
        if (contentEntity != null) {
            try {
                this.doReindex(writer, contentEntity);
            }
            catch (InvalidSearchException e) {
                log.error("Could not update the permissions field of the edge index for content {}", (Object)this.contentId, (Object)e);
            }
        } else {
            log.debug("Cannot reindex content {} - no such ContentEntityObject found", (Object)this.contentId);
        }
    }

    private void doReindex(SearchIndexWriter writer, ContentEntityObject contentEntity) throws InvalidSearchException {
        HashSet<String> edgeEntityIds = new HashSet<String>();
        edgeEntityIds.add(this.contentId);
        List comments = contentEntity.getComments();
        for (Comment comment : comments) {
            if (CollectionUtils.isEmpty((Collection)comment.getChildren())) continue;
            String commentId = comment.getIdAsString();
            edgeEntityIds.add(commentId);
        }
        int numDocumentsReindexed = this.findAndReindexDocuments(edgeEntityIds, writer);
        log.trace("Found and updated {} edge index documents for content {}", (Object)numDocumentsReindexed, (Object)this.contentId);
        log.debug("Completed permissions re-indexing for content {}", (Object)this.contentId);
    }

    private int findAndReindexDocuments(Set<String> edgeEntityIds, SearchIndexWriter writer) {
        TermSetQuery edgesRelatedToTargetQuery = new TermSetQuery(EdgeIndexSchema.EDGE_TARGET_ID, edgeEntityIds);
        AtomicInteger numDocumentsReIndexed = new AtomicInteger(0);
        long total = this.edgeSearchIndexAccessor.scan((SearchQuery)edgesRelatedToTargetQuery, null, scannedDocument -> {
            AtlassianDocument doc = this.getDocument((ScannedDocument)scannedDocument);
            String edgeId = scannedDocument.getFieldValue("edge.id");
            try {
                if (!StringUtils.isEmpty((CharSequence)edgeId)) {
                    writer.delete((SearchQuery)new TermQuery("edge.id", edgeId));
                } else {
                    long date = Long.parseLong(scannedDocument.getFieldValue("edge.date"));
                    writer.delete((SearchQuery)new BooleanQuery.Builder().addMust((Object)new TermQuery(EdgeIndexSchema.EDGE_USERKEY, scannedDocument.getFieldValue(EdgeIndexSchema.EDGE_USERKEY))).addMust((Object)new TermQuery("edge.targetType", scannedDocument.getFieldValue("edge.targetType"))).addMust((Object)new TermQuery(EdgeIndexSchema.EDGE_TARGET_ID, scannedDocument.getFieldValue(EdgeIndexSchema.EDGE_TARGET_ID))).addMust((Object)FieldExistsQuery.fieldNotExistsQuery((String)"edge.id")).addMust((Object)new LongRangeQuery("edge.date", new Range((Object)date, (Object)date, true, true))).build());
                    doc.addField((FieldDescriptor)new StringFieldDescriptor("edge.id", UUID.randomUUID().toString(), FieldDescriptor.Store.YES));
                }
                writer.add(doc);
                numDocumentsReIndexed.getAndIncrement();
            }
            catch (IOException e) {
                log.error(String.format("Error occurred while reindexing edge with ID %s", edgeId), (Throwable)e);
            }
        }, 0.0f);
        if (total != (long)numDocumentsReIndexed.get()) {
            log.warn("Total {} edge index docs scanned but only {} re-built. Please check the ERROR messages in the log for more details.", (Object)total, (Object)numDocumentsReIndexed.get());
        }
        return numDocumentsReIndexed.get();
    }

    private AtlassianDocument getDocument(ScannedDocument scannedDocument) {
        AtlassianDocument document = new AtlassianDocument();
        String edgeTargetId = scannedDocument.getFieldValue(EdgeIndexSchema.EDGE_TARGET_ID);
        document.addField((FieldDescriptor)new StringFieldDescriptor(EdgeIndexSchema.EDGE_USERKEY, scannedDocument.getFieldValue(EdgeIndexSchema.EDGE_USERKEY), FieldDescriptor.Store.YES));
        document.addField((FieldDescriptor)new StringFieldDescriptor(EdgeIndexSchema.EDGE_TARGET_ID, edgeTargetId, FieldDescriptor.Store.YES));
        document.addField((FieldDescriptor)new StringFieldDescriptor("edge.type", scannedDocument.getFieldValue("edge.type"), FieldDescriptor.Store.YES));
        if (scannedDocument.getFieldValue("edge.id") != null) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("edge.id", scannedDocument.getFieldValue("edge.id"), FieldDescriptor.Store.YES));
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor("edge.targetAuthor", (String)StringUtils.defaultIfBlank((CharSequence)scannedDocument.getFieldValue("edge.targetAuthor"), (CharSequence)""), FieldDescriptor.Store.YES));
        document.addField((FieldDescriptor)new StringFieldDescriptor("edge.targetType", scannedDocument.getFieldValue("edge.targetType"), FieldDescriptor.Store.YES));
        if (StringUtils.isNotBlank((CharSequence)scannedDocument.getFieldValue(SearchFieldNames.SPACE_KEY))) {
            document.addField((FieldDescriptor)new StringFieldDescriptor(SearchFieldNames.SPACE_KEY, (String)StringUtils.defaultIfBlank((CharSequence)scannedDocument.getFieldValue(SearchFieldNames.SPACE_KEY), (CharSequence)""), FieldDescriptor.Store.YES));
        } else {
            document.addField((FieldDescriptor)new StringFieldDescriptor(SearchFieldNames.IN_SPACE, "false", FieldDescriptor.Store.NO));
        }
        document.addField((FieldDescriptor)new LongFieldDescriptor("edge.date", Long.parseLong(scannedDocument.getFieldValue("edge.date")), FieldDescriptor.Store.YES));
        ContentEntityObject target = this.contentEntityManager.getById(Long.parseLong(edgeTargetId));
        Collection permissions = this.contentPermissionCalculator.calculate(target);
        if (!permissions.isEmpty()) {
            permissions.stream().map(arg_0 -> ((ContentPermissionCalculator)this.contentPermissionCalculator).getEncodedPermissionsCollection(arg_0)).map(arg_0 -> ((NestedStringFieldMapping)SearchFieldMappings.CONTENT_PERMISSION_SETS).createField(arg_0)).forEach(arg_0 -> ((AtlassianDocument)document).addField(arg_0));
        }
        return document;
    }
}

