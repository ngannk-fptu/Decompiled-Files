/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import java.util.Date;

public class ContentMetadataJsonator
implements Jsonator<ContentEntityObject> {
    private final I18NBeanFactory i18NBeanFactory;
    private final DateFormatterFactory dateFormatterFactory;

    public ContentMetadataJsonator(I18NBeanFactory i18NBeanFactory, DateFormatterFactory dateFormatterFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.dateFormatterFactory = dateFormatterFactory;
    }

    @Override
    public Json convert(ContentEntityObject content) {
        JsonObject json = new JsonObject();
        DateFormatter dateFormatter = this.dateFormatterFactory.createForUser();
        json.setProperty("id", content.getIdAsString());
        json.setProperty("title", content.getTitle());
        json.setProperty("creatorName", content.getCreatorName());
        json.setProperty("creationDate", dateFormatter.format(content.getCreationDate()));
        json.setProperty("lastModifier", content.getLastModifierName());
        json.setProperty("lastModificationDate", dateFormatter.format(content.getLastModificationDate()));
        json.setProperty("date", dateFormatter.formatDateTime(content.getLastModificationDate()));
        json.setProperty("friendlyDate", this.formatFriendlyDate(content.getLastModificationDate()));
        json.setProperty("type", content.getType());
        json.setProperty("url", content.getUrlPath());
        if (content instanceof SpaceContentEntityObject && ((SpaceContentEntityObject)content).getSpace() != null) {
            json.setProperty("spaceKey", ((SpaceContentEntityObject)content).getSpaceKey());
            json.setProperty("spaceName", ((SpaceContentEntityObject)content).getSpace().getName());
        }
        if (content instanceof PersonalInformation) {
            json.setProperty("username", ((PersonalInformation)content).getUsername());
        }
        return json;
    }

    private String formatFriendlyDate(Date date) {
        FriendlyDateFormatter friendlyFormatter = this.dateFormatterFactory.createFriendlyForUser(RequestTimeThreadLocal.getTimeOrNow());
        Message message = friendlyFormatter.getFormatMessage(date);
        return this.getText(message.getKey(), message.getArguments());
    }

    private String getText(String key, Object ... args) {
        return this.i18NBeanFactory.getI18NBean().getText(key, args);
    }
}

