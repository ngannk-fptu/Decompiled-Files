/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import java.util.HashMap;
import java.util.concurrent.CompletionException;

class ContainerMap
extends HashMap<String, String>
implements Container {
    ContainerMap() {
    }

    public <T extends Container> T convertTo(Class<T> clazz) {
        if (clazz.equals(Content.class)) {
            ContentId id;
            String idStr = (String)this.get("id");
            String type = (String)this.get("type");
            if (idStr == null || type == null) {
                throw new IllegalStateException("Must provide id and type for Content");
            }
            Content.ContentBuilder builder = Content.builder();
            builder.type(ContentType.valueOf(type));
            String status = (String)this.get("status");
            builder.status(status == null ? ContentStatus.CURRENT : ContentStatus.valueOf(status));
            try {
                id = ContentId.deserialise(idStr);
            }
            catch (BadRequestException e) {
                throw new CompletionException(e);
            }
            return (T)((Container)clazz.cast(builder.id(id).title((String)this.get("title")).build()));
        }
        if (clazz.equals(Space.class)) {
            String key;
            Space.SpaceBuilder builder = Space.builder();
            String id = (String)this.get("id");
            if (id != null) {
                builder.id(Long.parseLong(id));
            }
            if ((key = (String)this.get("key")) != null) {
                builder.key(key);
            }
            if (key == null && id == null) {
                throw new IllegalStateException("Must provide key or id for space");
            }
            return (T)((Container)clazz.cast(builder.name((String)this.get("name")).build()));
        }
        throw new IllegalArgumentException("Unsupported container class + " + clazz);
    }

    private static enum IdProperties {

    }
}

