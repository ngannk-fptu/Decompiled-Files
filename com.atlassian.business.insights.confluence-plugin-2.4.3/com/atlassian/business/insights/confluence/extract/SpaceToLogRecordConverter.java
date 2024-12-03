/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityToLogRecordConverter
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityToLogRecordConverter;
import com.atlassian.business.insights.confluence.attribute.SpaceAttributes;
import com.atlassian.business.insights.confluence.extract.ConverterHelper;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.HashMap;

public class SpaceToLogRecordConverter
implements EntityToLogRecordConverter<Long, SpaceDescription> {
    private final ApplicationProperties applicationProperties;
    private final ConverterHelper helper;

    public SpaceToLogRecordConverter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.helper = new ConverterHelper(applicationProperties);
    }

    public LogRecord convert(Entity<Long, SpaceDescription> entity) {
        Page homePage;
        SpaceDescription spaceDescription = (SpaceDescription)entity.getValue();
        HashMap<String, Object> payload = new HashMap<String, Object>();
        this.helper.populateCommonAttributes((ConfluenceEntityObject)spaceDescription, payload);
        payload.put(SpaceAttributes.SPACE_KEY_ATTR.getInternalName(), spaceDescription.getSpaceKey());
        payload.put(SpaceAttributes.SPACE_NAME_ATTR.getInternalName(), spaceDescription.getDisplayTitle());
        payload.put(SpaceAttributes.SPACE_TYPE_ATTR.getInternalName(), spaceDescription.getSpace() != null ? spaceDescription.getSpace().getSpaceType() : "");
        payload.put(SpaceAttributes.SPACE_STATUS_ATTR.getInternalName(), spaceDescription.getSpace() != null ? spaceDescription.getSpace().getSpaceStatus().name() : "");
        payload.put(SpaceAttributes.SPACE_URL_ATTR.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + spaceDescription.getUrlPath());
        Page page = homePage = spaceDescription.getSpace() != null ? spaceDescription.getSpace().getHomePage() : null;
        if (homePage != null) {
            payload.put(SpaceAttributes.HOMEPAGE_URL_ATTR.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + homePage.getUrlPath());
        }
        return LogRecord.getInstance((Object)entity.getId(), (long)entity.getTimestamp(), payload);
    }
}

