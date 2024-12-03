/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ChildrenMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.service.MobileChildContentService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSavedContentConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileContentFactory {
    public static final String IMAGE_LAZY_LOADING_PRO = "image-lazy-loading";
    private final MobileSavedContentConverter savedContentConverter;
    private final MobileAbstractPageConverter abstractPageConverter;
    private final MobileChildContentService childContentService;

    @Autowired
    public MobileContentFactory(MobileSavedContentConverter savedContentConverter, MobileAbstractPageConverter abstractPageConverter, MobileChildContentService childContentService) {
        this.savedContentConverter = savedContentConverter;
        this.abstractPageConverter = abstractPageConverter;
        this.childContentService = childContentService;
    }

    public List<ContentDto> convert(List<Page> pages, Expansions expansions) {
        if (pages == null || pages.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ContentMetadataDto> metadataMap = this.buildMetadataMap(pages, expansions);
        return pages.stream().map(child -> this.abstractPageConverter.to((ContentEntityObject)child, (ContentMetadataDto)metadataMap.get(child.getId()), expansions)).collect(Collectors.toList());
    }

    private Map<Long, ContentMetadataDto> buildMetadataMap(List<Page> pages, Expansions expansions) {
        HashMap<Long, ContentMetadataDto> metadataMap = new HashMap<Long, ContentMetadataDto>();
        if (expansions.getSubExpansions("metadata").canExpand("children")) {
            Map<Long, Integer> childrenCountMap = this.childContentService.getPageChildrenCount(pages.stream().map(page -> page.getId()).collect(Collectors.toList()));
            childrenCountMap.forEach((pageId, childrenCount) -> metadataMap.put((Long)pageId, this.buildMetadata((int)childrenCount)));
        }
        return metadataMap;
    }

    private ContentMetadataDto buildMetadata(int childrenCount) {
        ChildrenMetadataDto childrenMetadataDto = new ChildrenMetadataDto();
        childrenMetadataDto.setCount(childrenCount);
        return ContentMetadataDto.builder().children(childrenMetadataDto).build();
    }

    public ContentDto getContent(ContentEntityObject ceo) {
        return this.abstractPageConverter.to(ceo, Expansions.of((String[])new String[]{"body", "space", "author", "timeToRead", "watched"}));
    }

    public List<ContentDto> convert(List<Content> sources, Type type) {
        if (type == Type.SAVE_CONTENT) {
            return this.savedContentConverter.to(sources);
        }
        return Collections.emptyList();
    }

    public static enum Type {
        SAVE_CONTENT,
        RELATION_CONTENT;

    }
}

