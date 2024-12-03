/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty$SpacePropertyBuilder
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.JsonContentPropertyId
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Function
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.JsonContentPropertyId;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Function;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonPropertyFactory {
    private final ContentService contentService;
    private final SpaceService spaceService;

    @Autowired
    public JsonPropertyFactory(@ComponentImport ContentService contentService, @ComponentImport SpaceService spaceService) {
        this.contentService = contentService;
        this.spaceService = spaceService;
    }

    public Function<CustomContentEntityObject, JsonContentProperty> buildContentPropertyFromFunction(Content content, Expansions expansions) {
        return input -> this.buildFrom(content, (CustomContentEntityObject)input, expansions);
    }

    public Function<CustomContentEntityObject, JsonContentProperty> buildContentPropertyFromFunction() {
        return input -> this.buildFrom((CustomContentEntityObject)input);
    }

    public Function<CustomContentEntityObject, JsonSpaceProperty> buildSpacePropertyFromFunction(Space space, Expansions expansions) {
        return input -> this.buildFrom(space, (CustomContentEntityObject)input, expansions);
    }

    public JsonContentProperty buildContentPropertyFrom(CustomContentEntityObject storageContentProperty, Expansions expansions) {
        ContentEntityObject container = Objects.requireNonNull(storageContentProperty.getContainer());
        Optional contentOption = this.contentService.find(expansions.getSubExpansions("content").toArray()).withId(container.getContentId()).fetch();
        return this.buildFrom((Content)contentOption.orElse(null), storageContentProperty, expansions);
    }

    public JsonSpaceProperty buildSpacePropertyFrom(CustomContentEntityObject property, Expansions expansions) {
        Optional spaceOption = this.spaceService.find(expansions.getSubExpansions("space").toArray()).withKeys(new String[]{property.getSpace().getKey()}).fetch();
        return this.buildFrom((Space)spaceOption.orElse(null), property, expansions);
    }

    public JsonContentProperty buildFrom(Content content, CustomContentEntityObject storageContentProperty, Expansions expansions) {
        JsonContentProperty contentProperty = JsonContentProperty.builder().content(this.makeRef(content, Content.class, expansions.canExpand("content"))).id(JsonContentPropertyId.of((long)storageContentProperty.getId())).key(storageContentProperty.getTitle()).value(new JsonString(storageContentProperty.getBodyAsString())).version(this.makeVersion((ContentEntityObject)storageContentProperty, expansions.canExpand("version"))).build();
        return contentProperty;
    }

    public JsonContentProperty buildFrom(CustomContentEntityObject storageContentProperty) {
        ContentEntityObject container = Objects.requireNonNull(storageContentProperty.getContainer());
        Reference contentRef = Reference.collapsed((Object)Content.builder().id(container.getContentId()).build());
        JsonContentProperty contentProperty = JsonContentProperty.builder().content(contentRef).id(JsonContentPropertyId.of((long)storageContentProperty.getId())).key(storageContentProperty.getTitle()).value(new JsonString(storageContentProperty.getBodyAsString())).version(this.makeVersion((ContentEntityObject)storageContentProperty, true)).build();
        return contentProperty;
    }

    public JsonSpaceProperty buildFrom(Space space, CustomContentEntityObject storageContentProperty, Expansions expansions) {
        JsonSpaceProperty contentProperty = ((JsonSpaceProperty.SpacePropertyBuilder)((JsonSpaceProperty.SpacePropertyBuilder)((JsonSpaceProperty.SpacePropertyBuilder)JsonSpaceProperty.builder().space(this.makeRef(space, Space.class, expansions.canExpand("space"))).key(storageContentProperty.getTitle())).value(new JsonString(storageContentProperty.getBodyAsString()))).version(this.makeVersion((ContentEntityObject)storageContentProperty, expansions.canExpand("version")))).build();
        return contentProperty;
    }

    private <T> Reference<T> makeRef(T entity, Class<T> clazz, boolean expanded) {
        if (expanded) {
            return Reference.orEmpty(entity, clazz);
        }
        if (entity != null) {
            return Reference.collapsed(entity);
        }
        return Reference.empty(clazz);
    }

    public Version makeVersion(ContentEntityObject entity) {
        if (entity == null) {
            return null;
        }
        return Version.builder().when(entity.getLastModificationDate()).message(entity.getVersionComment()).number(entity.getVersion()).build();
    }

    public Reference<Version> makeVersion(ContentEntityObject entity, boolean expanded) {
        if (!expanded) {
            return Version.buildReference((int)entity.getVersion());
        }
        return Reference.to((Object)this.makeVersion(entity));
    }
}

