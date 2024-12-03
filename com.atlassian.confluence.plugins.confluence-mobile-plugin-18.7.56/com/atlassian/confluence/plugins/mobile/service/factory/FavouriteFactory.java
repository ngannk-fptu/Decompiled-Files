/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.json.jsonorg.JSONObject
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.plugins.mobile.dto.FavouriteDto;
import com.atlassian.confluence.plugins.mobile.helper.TimeHelper;
import com.atlassian.json.jsonorg.JSONObject;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavouriteFactory {
    public static final String CURRENT_USER_META = "currentuser";
    public static final String FAVOURITED_META = "favourited";
    public static final String FAVOURITED_DATE_META = "favouritedDate";
    private final TimeHelper timeHelper;

    @Autowired
    public FavouriteFactory(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public FavouriteDto convertToFavouriteDto(Content content) {
        String contentData = ((ContentBody)content.getBody().get(ContentRepresentation.STORAGE)).getValue();
        return new FavouriteDto.Builder().id(content.getId().asLong()).author(content.getHistory().getCreatedBy()).title(content.getTitle()).timeToRead(this.timeHelper.timeToReadWithMarkupContent(contentData, content.getTitle())).favouritedDate(this.getFavouritedDate(content)).build();
    }

    private String getFavouritedDate(Content content) {
        Map currentUser = (Map)content.getMetadata().get(CURRENT_USER_META);
        return new JSONObject(currentUser.get(FAVOURITED_META)).getString(FAVOURITED_DATE_META);
    }
}

