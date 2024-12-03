/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityToLogRecordConverter
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityToLogRecordConverter;
import com.atlassian.business.insights.confluence.attribute.SharedAttributes;
import com.atlassian.business.insights.confluence.attribute.UserAttributes;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.HashMap;

public class UserToLogRecordConverter
implements EntityToLogRecordConverter<Long, PersonalInformation> {
    private final ApplicationProperties applicationProperties;

    public UserToLogRecordConverter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public LogRecord convert(Entity<Long, PersonalInformation> entity) {
        PersonalInformation user = (PersonalInformation)entity.getValue();
        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put(SharedAttributes.INSTANCE_URL.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
        payload.put(UserAttributes.ID_ATTR.getInternalName(), user.getUser().getKey().getStringValue());
        payload.put(UserAttributes.NAME_ATTR.getInternalName(), user.getUser().getName());
        payload.put(UserAttributes.FULL_NAME_ATTR.getInternalName(), user.getUser().getFullName());
        payload.put(UserAttributes.EMAIL_ATTR.getInternalName(), user.getUser().getEmail());
        return LogRecord.getInstance((Object)entity.getId(), (long)entity.getTimestamp(), payload);
    }
}

