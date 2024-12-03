/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.AnalyticsEvent;
import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyticsEventStore {
    private final EntityManagerTemplate tmpl;

    public AnalyticsEventStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public void createAnalyticsEvent(AnalyticsEvent analyticsEvent) {
        this.tmpl.persist(analyticsEvent);
    }

    public List<AnalyticsEvent> pullAnalyticsEvents(int batchSize) {
        return this.tmpl.query(AnalyticsEvent.class, "select event from AnalyticsEvent event order by event.timestamp").max(batchSize).list();
    }

    public void deleteAnalyticsEvents(List<AnalyticsEvent> events) {
        this.tmpl.query("delete from AnalyticsEvent where id in :idsToDelete").param("idsToDelete", events.stream().map(WithId::getId).collect(Collectors.toList())).update();
    }

    public Long countAnalyticsEvents() {
        return this.tmpl.query(Long.class, "select count(*) from AnalyticsEvent").single();
    }

    public void deleteAllEvents() {
        this.tmpl.query("delete from AnalyticsEvent").update();
    }
}

