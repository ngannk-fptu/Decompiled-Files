/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.plugins.rest.manager.DateEntityFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityEdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityObjectId;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetId;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.plugins.edgeindex.rest.ContentEntityHelper;
import com.atlassian.confluence.plugins.edgeindex.rest.CountItem;
import com.atlassian.confluence.plugins.edgeindex.rest.StreamItem;
import com.atlassian.confluence.plugins.edgeindex.rest.UrlStrategy;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StreamItemFactory {
    private final UserDtoFactory userDtoFactory;
    private final DateEntityFactory dateEntityFactory;
    private final ContentEntityHelper contentEntityHelper;
    private final EdgeTypeRepository edgeTypeRepository;
    private final ContentUiSupport<ContentEntityObject> contentUiSupport;
    private final Map<String, UrlStrategy> urlStrategies;

    @Autowired
    public StreamItemFactory(ContextPathHolder contextPathHolder, @ComponentImport UserDtoFactory userDtoFactory, @ComponentImport DateEntityFactory dateEntityFactory, ContentEntityHelper contentEntityHelper, EdgeTypeRepository edgeTypeRepository, ContentUiSupport<ContentEntityObject> contentUiSupport) {
        this.userDtoFactory = userDtoFactory;
        this.dateEntityFactory = dateEntityFactory;
        this.contentEntityHelper = contentEntityHelper;
        this.edgeTypeRepository = edgeTypeRepository;
        this.contentUiSupport = contentUiSupport;
        this.urlStrategies = StreamItemFactory.buildUrlStrategies(contextPathHolder);
    }

    public List<StreamItem> fromEdgeTargetInfos(List<EdgeTargetInfo> edgeTargetInfos, String urlStrategy, boolean debug) {
        LinkedList<Long> contentIdsSubset = new LinkedList<Long>();
        HashMap<String, List<EdgeTargetId>> edgeTargetIdsByTargetType = new HashMap<String, List<EdgeTargetId>>();
        HashMap<Long, EdgeTargetInfo> edgeTargetInfoByContentId = new HashMap<Long, EdgeTargetInfo>();
        HashMap<Long, Float> edgeTargetScores = new HashMap<Long, Float>();
        for (EdgeTargetInfo edgeTargetInfo : edgeTargetInfos) {
            if (!(edgeTargetInfo instanceof ContentEntityEdgeTargetInfo) || "mobile".equalsIgnoreCase(urlStrategy) && "attachment".equalsIgnoreCase(edgeTargetInfo.getTargetType())) continue;
            ContentEntityEdgeTargetInfo contentEntityObjectInfo = (ContentEntityEdgeTargetInfo)edgeTargetInfo;
            ContentEntityObjectId contentEntityObjectId = contentEntityObjectInfo.getTargetId();
            contentIdsSubset.add(contentEntityObjectId.getId());
            edgeTargetInfoByContentId.put(contentEntityObjectId.getId(), edgeTargetInfo);
            List contentIds = edgeTargetIdsByTargetType.computeIfAbsent(contentEntityObjectInfo.getTargetType(), k -> new ArrayList());
            contentIds.add(contentEntityObjectId);
            if (!debug) continue;
            edgeTargetScores.put(contentEntityObjectId.getId(), Float.valueOf(edgeTargetInfo.getScore()));
        }
        Collection<EdgeType> edgeIndexTypes = this.edgeTypeRepository.getEdgeIndexTypes();
        Map<EdgeType, Map<String, Map<EdgeTargetId, Integer>>> edgeTypesCountsByTargetTypeAndId = this.getEdgeTypesCountByTargetTypeAndIdMap(edgeTargetIdsByTargetType, edgeIndexTypes);
        LinkedList<StreamItem> streamItems = new LinkedList<StreamItem>();
        List<ContentEntityObject> contentEntities = this.contentEntityHelper.getContentEntities(contentIdsSubset);
        for (ContentEntityObject contentEntity : contentEntities) {
            Comment comment;
            if ("mobile".equalsIgnoreCase(urlStrategy) && contentEntity instanceof Comment && (comment = (Comment)contentEntity).getContainer() instanceof Attachment) continue;
            long contentId = contentEntity.getId();
            EdgeTargetInfo targetInfo = (EdgeTargetInfo)edgeTargetInfoByContentId.get(contentId);
            StreamItem streamItem = new StreamItem();
            streamItem.setId(contentEntity.getId());
            streamItem.setTitle(contentEntity.getDisplayTitle());
            streamItem.setUrl(this.urlStrategies.get(urlStrategy).getUrl(contentEntity));
            streamItem.setAuthor(this.userDtoFactory.getUserDto(contentEntity.getCreator()));
            streamItem.setFriendlyDate(this.dateEntityFactory.buildDateEntity(contentEntity.getCreationDate()).getFriendly());
            streamItem.setDate(this.dateEntityFactory.buildDateEntity(contentEntity.getCreationDate()).getDate());
            streamItem.setContentCssClass(this.contentUiSupport.getContentCssClass((ConfluenceEntityObject)contentEntity));
            streamItem.setIconCssClass("icon " + this.contentUiSupport.getIconCssClass((ConfluenceEntityObject)contentEntity));
            List<CountItem> edges = this.getEdgeCounts(edgeIndexTypes, edgeTypesCountsByTargetTypeAndId, contentId, targetInfo, streamItem);
            streamItem.setCounts(edges);
            if (debug && edgeTargetScores.containsKey(contentId)) {
                streamItem.setScore(Float.toString(((Float)edgeTargetScores.get(contentId)).floatValue()));
            }
            streamItems.add(streamItem);
        }
        return streamItems;
    }

    private List<CountItem> getEdgeCounts(Collection<EdgeType> edgeIndexTypes, Map<EdgeType, Map<String, Map<EdgeTargetId, Integer>>> edgeTypesCountsByTargetTypeAndId, long contentId, EdgeTargetInfo targetInfo, StreamItem streamItem) {
        ArrayList<CountItem> edges = new ArrayList<CountItem>();
        for (EdgeType edgeType : edgeIndexTypes) {
            Integer numberOf;
            Map<EdgeTargetId, Integer> countById;
            Map<String, Map<EdgeTargetId, Integer>> countsMapByTargetType = edgeTypesCountsByTargetTypeAndId.get(edgeType);
            if (countsMapByTargetType == null || (countById = countsMapByTargetType.get(targetInfo.getTargetType())) == null || (numberOf = countById.get(new ContentEntityObjectId(contentId))) == null || numberOf <= 0) continue;
            edges.add(new CountItem(numberOf, edgeType.getEdgeUiSupport().getCssClass()));
            if ("like.create".equals(edgeType.getKey())) {
                streamItem.setNumberOfLikes(numberOf);
                continue;
            }
            if (!"comment.create".equals(edgeType.getKey())) continue;
            streamItem.setNumberOfComments(numberOf);
        }
        return edges;
    }

    private Map<EdgeType, Map<String, Map<EdgeTargetId, Integer>>> getEdgeTypesCountByTargetTypeAndIdMap(Map<String, List<EdgeTargetId>> edgeTargetIdsByTargetType, Collection<EdgeType> edgeIndexTypes) {
        HashMap<EdgeType, Map<String, Map<EdgeTargetId, Integer>>> edgeTypesCountsByTargetTypeAndId = new HashMap<EdgeType, Map<String, Map<EdgeTargetId, Integer>>>(edgeIndexTypes.size());
        for (EdgeType edgeType : edgeIndexTypes) {
            Map<EdgeCountQuery, Set<String>> edgeCountQueries = edgeType.getEdgeCountQueries(edgeTargetIdsByTargetType.keySet());
            HashMap<String, Map<EdgeTargetId, Integer>> edgeTypeCountByTargetTypeAndId = new HashMap<String, Map<EdgeTargetId, Integer>>();
            for (Map.Entry<EdgeCountQuery, Set<String>> kvp : edgeCountQueries.entrySet()) {
                Set<String> compatibleTargetTypes = kvp.getValue();
                EdgeCountQuery edgeCountQuery = kvp.getKey();
                for (String targetType : compatibleTargetTypes) {
                    List<EdgeTargetId> targetTypeIds = edgeTargetIdsByTargetType.get(targetType);
                    if (targetTypeIds == null) continue;
                    Map<EdgeTargetId, Integer> edgeCountForTargetIds = edgeCountQuery.getEdgeCountForTargetIds(targetTypeIds);
                    edgeTypeCountByTargetTypeAndId.put(targetType, edgeCountForTargetIds);
                }
            }
            edgeTypesCountsByTargetTypeAndId.put(edgeType, edgeTypeCountByTargetTypeAndId);
        }
        return edgeTypesCountsByTargetTypeAndId;
    }

    private static Map<String, UrlStrategy> buildUrlStrategies(ContextPathHolder contextPathHolder) {
        HashMap<String, UrlStrategy> urlStrategies = new HashMap<String, UrlStrategy>();
        urlStrategies.put("desktop", contentEntity -> contextPathHolder.getContextPath() + contentEntity.getUrlPath());
        urlStrategies.put("mobile", contentEntity -> {
            if (contentEntity instanceof Comment) {
                Comment comment = (Comment)contentEntity;
                ContentEntityObject owningContent = Objects.requireNonNull(comment.getContainer());
                return contextPathHolder.getContextPath() + "/plugins/servlet/mobile#content/view/" + owningContent.getId() + "/" + comment.getId();
            }
            return contextPathHolder.getContextPath() + "/plugins/servlet/mobile#content/view/" + contentEntity.getId();
        });
        return urlStrategies;
    }
}

