/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ContentModifiersBulkExtractor
implements BulkExtractor<ConfluenceEntityObject> {
    private static final Logger log = LoggerFactory.getLogger(ContentModifiersBulkExtractor.class);
    private final ContentEntityObjectDao<ContentEntityObject> contentEntityObjectDao;

    public ContentModifiersBulkExtractor(ContentEntityObjectDao<ContentEntityObject> contentEntityObjectDao) {
        this.contentEntityObjectDao = Objects.requireNonNull(contentEntityObjectDao);
    }

    @Override
    public boolean canHandle(Class<?> entityType) {
        return ConfluenceEntityObject.class.isAssignableFrom(entityType);
    }

    @Override
    public void extractAll(Collection<ConfluenceEntityObject> entities, Class<? extends ConfluenceEntityObject> entityType, BiConsumer<ConfluenceEntityObject, FieldDescriptor> sink) {
        log.debug("Extracting last modifiers for {} {} entities", (Object)entities.size(), (Object)entityType.getName());
        if (ContentEntityObject.class.isAssignableFrom(entityType)) {
            this.extractAllModifiers(ContentModifiersBulkExtractor.mapById(entities, ContentEntityObject.class, ceo -> ceo), sink);
        } else if (Space.class.isAssignableFrom(entityType)) {
            this.extractAllModifiers(ContentModifiersBulkExtractor.mapById(entities, Space.class, Space::getDescription), sink);
        } else {
            for (ConfluenceEntityObject ceo2 : entities) {
                Optional.of(ceo2).map(ConfluenceEntityObject::getLastModifier).map(ContentModifiersBulkExtractor::lastModifierField).ifPresent(field -> sink.accept(ceo2, (FieldDescriptor)field));
            }
        }
    }

    private <T> void extractAllModifiers(Map<Long, T> entitiesById, BiConsumer<T, FieldDescriptor> sink) {
        this.contentEntityObjectDao.getAllModifiers(entitiesById.keySet()).forEach((entityId, users) -> {
            Object entity = entitiesById.get(entityId);
            log.debug("Generating fields for {} last modifiers of entity {}", (Object)users.size(), entity);
            users.forEach(user -> sink.accept(entity, ContentModifiersBulkExtractor.lastModifierField(user)));
        });
    }

    private static FieldDescriptor lastModifierField(ConfluenceUser user) {
        return SearchFieldMappings.LAST_MODIFIERS.createField(user.getKey().getStringValue());
    }

    private static <T extends ConfluenceEntityObject> Map<Long, ConfluenceEntityObject> mapById(Collection<ConfluenceEntityObject> entities, Class<T> entityType, Function<T, EntityObject> f) {
        return entities.stream().map(entityType::cast).collect(Collectors.toMap(entity -> ((EntityObject)f.apply(entity)).getId(), entity -> entity));
    }
}

