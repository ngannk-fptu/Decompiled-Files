/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.search.v2.extractor.BulkExtractor
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search.extractors;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class NotificationContributionBulkExtractor
implements BulkExtractor<Searchable> {
    private final NotificationManager notificationManager;

    public NotificationContributionBulkExtractor(@ComponentImport NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public boolean canHandle(@Nonnull Class<?> type) {
        return Space.class.isAssignableFrom(type) || AbstractPage.class.isAssignableFrom(type);
    }

    public void extractAll(@Nonnull Collection<Searchable> searchables, @Nonnull Class<? extends Searchable> entityType, @Nonnull BiConsumer<Searchable, FieldDescriptor> sink) {
        Map searchableMap = searchables.stream().collect(Collectors.toMap(Searchable::getId, Function.identity()));
        if (Space.class.isAssignableFrom(entityType)) {
            this.notificationManager.getNotificationsBySpacesAndType(this.castSearchables(searchables), null).forEach(notification -> sink.accept((Searchable)searchableMap.get(notification.getSpace().getId()), this.getFieldDescriptor((Notification)notification)));
        } else if (AbstractPage.class.isAssignableFrom(entityType)) {
            this.notificationManager.getNotificationsByContents(this.castSearchables(searchables)).forEach(notification -> sink.accept((Searchable)searchableMap.get(notification.getContent().getId()), this.getFieldDescriptor((Notification)notification)));
        }
    }

    private <T> List<T> castSearchables(Collection<Searchable> searchables) {
        ArrayList result = new ArrayList();
        searchables.forEach(searchable -> result.add(searchable));
        return result;
    }

    private FieldDescriptor getFieldDescriptor(Notification notification) {
        return new FieldDescriptor("watchers", notification.getReceiver().getKey().getStringValue(), FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED);
    }
}

