/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.pages.Page;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AncestorRecordsGenerator {
    public static final int MAX_TREE_HEIGHT = 10000;
    private final ExportableEntityInfo ancestorsEntityInfo;
    private final Map<Long, Long> childToParentRelations = new ConcurrentHashMap<Long, Long>();

    public AncestorRecordsGenerator(ExportableEntityInfo ancestorsEntityInfo) {
        this.ancestorsEntityInfo = ancestorsEntityInfo;
    }

    public Collection<ImportedObjectV2> generateAncestorObjects(List<ImportedObjectV2> contentEntityRecords) {
        List<ImportedObjectV2> draftsOrCurrentPages = this.getDraftsOrCurrentPagesOnly(contentEntityRecords);
        draftsOrCurrentPages.forEach(this::registerChildToParentRelation);
        return draftsOrCurrentPages.stream().map(this::generateAncestorObjectsForOnePage).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void registerChildToParentRelation(ImportedObjectV2 importedObjectV2) {
        Long parentId = (Long)importedObjectV2.getFieldValue("parent");
        if (parentId != null) {
            Long id = (Long)importedObjectV2.getId();
            this.childToParentRelations.put(id, parentId);
        }
    }

    private List<ImportedObjectV2> getDraftsOrCurrentPagesOnly(List<ImportedObjectV2> contentEntityObjects) {
        return contentEntityObjects.stream().filter(e -> Page.class.equals(e.getEntityClass())).filter(e -> {
            String contentStatus = (String)e.getFieldValue("contentStatus");
            return "draft".equals(contentStatus) || "current".equals(contentStatus);
        }).collect(Collectors.toList());
    }

    private Collection<ImportedObjectV2> generateAncestorObjectsForOnePage(ImportedObjectV2 page) {
        Long pageId = (Long)page.getId();
        Long parentId = (Long)page.getFieldValue("parent");
        List<Long> ancestorIds = this.findAllAncestorIds(parentId);
        AtomicInteger position = new AtomicInteger();
        return ancestorIds.stream().map(ancestorId -> this.generateAncestorObject((long)ancestorId, pageId, position.getAndIncrement())).collect(Collectors.toList());
    }

    private List<Long> findAllAncestorIds(Long parentId) {
        LinkedList<Long> ancestorIds = new LinkedList<Long>();
        while (parentId != null) {
            ancestorIds.addFirst(parentId);
            parentId = this.childToParentRelations.get(parentId);
            if (ancestorIds.size() <= 10000) continue;
            throw new IllegalStateException("Unable to build ancestors because found more than 10000 ancestors. Current page id is " + parentId + ". Circular dependencies between pages?");
        }
        return ancestorIds;
    }

    private ImportedObjectV2 generateAncestorObject(long ancestorId, long descendentId, int position) {
        Map<String, Object> properties = Map.of("ancestorId", ancestorId, "descendentId", descendentId, "position", position);
        return new ImportedObjectV2(this.ancestorsEntityInfo, null, properties);
    }
}

