/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.core.util.DateTimeConverter
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.confluence.attribute.SharedAttributes;
import com.atlassian.business.insights.core.util.DateTimeConverter;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.text.StringEscapeUtils;

public class ConverterHelper {
    private final ApplicationProperties applicationProperties;

    public ConverterHelper(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void populateCommonAttributes(ConfluenceEntityObject entity, Map<String, Object> payload) {
        payload.put(SharedAttributes.INSTANCE_URL.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
        payload.put(SharedAttributes.CREATOR_ID.getInternalName(), entity.getCreator() != null ? entity.getCreator().getKey().getStringValue() : "");
        payload.put(SharedAttributes.CREATED_DATE.getInternalName(), entity.getCreationDate() != null ? DateTimeConverter.convertTimestampToDateTime((Instant)entity.getCreationDate().toInstant()) : "");
        payload.put(SharedAttributes.LAST_MODIFIER_ID.getInternalName(), entity.getLastModifier() != null ? entity.getLastModifier().getKey().getStringValue() : "");
        payload.put(SharedAttributes.UPDATED_DATE.getInternalName(), entity.getLastModificationDate() != null ? DateTimeConverter.convertTimestampToDateTime((Instant)entity.getLastModificationDate().toInstant()) : "");
    }

    @Nonnull
    public static String unescapeXhtml(@Nullable String content) {
        return Optional.ofNullable(content).map(StringEscapeUtils::unescapeXml).map(StringEscapeUtils::unescapeHtml4).orElse("");
    }
}

