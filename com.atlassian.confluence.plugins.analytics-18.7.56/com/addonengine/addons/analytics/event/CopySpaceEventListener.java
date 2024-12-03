/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.space.SpaceEvent
 *  com.atlassian.confluence.plugin.copyspace.event.SpaceCopyEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.event;

import com.addonengine.addons.analytics.service.RestrictionsService;
import com.addonengine.addons.analytics.service.model.restrictions.SpaceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.UserGroupRestriction;
import com.addonengine.addons.analytics.service.model.restrictions.UserRestriction;
import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.plugin.copyspace.event.SpaceCopyEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\u0010\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0014H\u0007R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/event/CopySpaceEventListener;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "eventPublisher", "Lcom/atlassian/event/api/EventPublisher;", "restrictionsService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "(Lcom/atlassian/event/api/EventPublisher;Lcom/addonengine/addons/analytics/service/RestrictionsService;)V", "getEventPublisher", "()Lcom/atlassian/event/api/EventPublisher;", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "getRestrictionsService", "()Lcom/addonengine/addons/analytics/service/RestrictionsService;", "afterPropertiesSet", "", "destroy", "onSpaceCopyEvent", "event", "Lcom/atlassian/confluence/event/events/space/SpaceEvent;", "analytics"})
@SourceDebugExtension(value={"SMAP\nCopySpaceEventListener.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CopySpaceEventListener.kt\ncom/addonengine/addons/analytics/event/CopySpaceEventListener\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,51:1\n1549#2:52\n1620#2,3:53\n1549#2:56\n1620#2,3:57\n*S KotlinDebug\n*F\n+ 1 CopySpaceEventListener.kt\ncom/addonengine/addons/analytics/event/CopySpaceEventListener\n*L\n35#1:52\n35#1:53,3\n38#1:56\n38#1:57,3\n*E\n"})
public final class CopySpaceEventListener
implements InitializingBean,
DisposableBean {
    @NotNull
    private final EventPublisher eventPublisher;
    @NotNull
    private final RestrictionsService restrictionsService;
    private final Logger log;

    @Autowired
    public CopySpaceEventListener(@ComponentImport @NotNull EventPublisher eventPublisher, @NotNull RestrictionsService restrictionsService) {
        Intrinsics.checkNotNullParameter((Object)eventPublisher, (String)"eventPublisher");
        Intrinsics.checkNotNullParameter((Object)restrictionsService, (String)"restrictionsService");
        this.eventPublisher = eventPublisher;
        this.restrictionsService = restrictionsService;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @NotNull
    public final EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @NotNull
    public final RestrictionsService getRestrictionsService() {
        return this.restrictionsService;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    /*
     * WARNING - void declaration
     */
    @EventListener
    public final void onSpaceCopyEvent(@NotNull SpaceEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (event.getClass().getName().equals("com.atlassian.confluence.plugin.copyspace.event.SpaceCopyEvent") && event instanceof SpaceCopyEvent) {
            void $this$mapTo$iv$iv;
            Collection collection;
            void $this$mapTo$iv$iv2;
            String string = ((SpaceCopyEvent)event).getOriginalSpaceKey();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getOriginalSpaceKey(...)");
            SpaceRestrictions originalSpaceRestrictions = this.restrictionsService.getSpaceRestrictions(string);
            Iterable $this$map$iv = originalSpaceRestrictions.getUsers();
            boolean $i$f$map = false;
            Iterable iterable = $this$map$iv;
            Iterable destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv2) {
                void userRestriction;
                UserRestriction userRestriction2 = (UserRestriction)item$iv$iv;
                collection = destination$iv$iv;
                boolean bl = false;
                String string2 = userRestriction.getUser().getUserKey();
                Intrinsics.checkNotNull((Object)string2);
                collection.add(string2);
            }
            List userKeys = CollectionsKt.toList((Iterable)((List)destination$iv$iv));
            this.log.debug("found {} user restrictions", (Object)userKeys.size());
            Iterable $this$map$iv2 = originalSpaceRestrictions.getUserGroups();
            boolean $i$f$map2 = false;
            destination$iv$iv = $this$map$iv2;
            Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
            boolean $i$f$mapTo2 = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void userGroupRestriction;
                UserGroupRestriction bl = (UserGroupRestriction)item$iv$iv;
                collection = destination$iv$iv2;
                boolean bl2 = false;
                collection.add(userGroupRestriction.getGroup().getName());
            }
            List groupNames = CollectionsKt.toList((Iterable)((List)destination$iv$iv2));
            this.log.debug("found {} group restrictions", (Object)groupNames.size());
            String string3 = ((SpaceCopyEvent)event).getSpace().getKey();
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getKey(...)");
            this.restrictionsService.saveSpaceRestrictions(string3, userKeys, groupNames);
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

