/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.helper.TimeHelper;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileConverter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileSavedContentConverter
implements MobileConverter<ContentDto, Content> {
    private static final String FAVOURITED_META = "favourited";
    private final TimeHelper timeHelper;

    @Autowired
    public MobileSavedContentConverter(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    @Override
    public ContentDto to(Content content) {
        String contentData = ((ContentBody)content.getBody().get(ContentRepresentation.STORAGE)).getValue();
        return AbstractPageDto.builder().id(content.getId().asLong()).title(content.getTitle()).contentType(content.getType().getValue()).author(content.getHistory().getCreatedBy()).timeToRead(this.timeHelper.timeToReadWithMarkupContent(contentData, content.getTitle())).build();
    }

    @Override
    public List<ContentDto> to(List<Content> sources) {
        return sources.stream().filter(this::isSaved).map(this::to).collect(Collectors.toList());
    }

    private boolean isSaved(Content content) {
        Map metadata = content.getMetadata();
        if (Objects.isNull(metadata)) {
            return false;
        }
        Map currentUser = (Map)metadata.get("currentuser");
        return currentUser != null && currentUser.containsKey(FAVOURITED_META);
    }
}

