/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.reflect.KClass
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.RestrictionsService;
import com.addonengine.addons.analytics.service.confluence.ConfluenceInfoService;
import com.addonengine.addons.analytics.service.confluence.ContentService;
import com.addonengine.addons.analytics.service.confluence.GroupService;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.Group;
import com.addonengine.addons.analytics.service.confluence.model.User;
import com.addonengine.addons.analytics.service.model.restrictions.InstanceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.SpaceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.UserGroupRestriction;
import com.addonengine.addons.analytics.service.model.restrictions.UserRestriction;
import com.addonengine.addons.analytics.store.RestrictionsRepository;
import com.addonengine.addons.analytics.store.model.InstanceRestrictionsData;
import com.addonengine.addons.analytics.store.model.SpaceRestrictionsData;
import com.addonengine.addons.analytics.store.model.UserGroupRestrictionData;
import com.addonengine.addons.analytics.store.model.UserRestrictionData;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KClass;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u00a6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003BC\b\u0007\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0012J\b\u0010\u001d\u001a\u00020\u001eH\u0016J\u001e\u0010\u001f\u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\f\u0012\n \u0019*\u0004\u0018\u00010\u00180\u00180\u0016H\u0002J\u001e\u0010 \u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\f\u0012\n \u0019*\u0004\u0018\u00010\u00180\u00180\u0016H\u0002J\b\u0010!\u001a\u00020\u001eH\u0002J\u0010\u0010\"\u001a\u00020\u001e2\u0006\u0010#\u001a\u00020\u0017H\u0002J\b\u0010$\u001a\u00020\u001eH\u0016J\b\u0010%\u001a\u00020&H\u0016J\u0010\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u0017H\u0016J\u0018\u0010*\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u00172\u0006\u0010,\u001a\u00020-H\u0016J\u0010\u0010.\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u0017H\u0016J\u0010\u0010/\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u0017H\u0002J\u0018\u00100\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u00172\u0006\u0010)\u001a\u00020\u0017H\u0016J\u0018\u00101\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u00172\u0006\u0010)\u001a\u00020\u0017H\u0002J$\u00102\u001a\u00020\u00182\f\u00103\u001a\b\u0012\u0004\u0012\u000205042\f\u00106\u001a\b\u0012\u0004\u0012\u00020704H\u0002J&\u00108\u001a\u00020(2\u0006\u00109\u001a\u00020:2\u0014\u0010;\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\u0004\u0012\u00020=0<H\u0002J\u0010\u0010>\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020@H\u0007J\u0010\u0010A\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020BH\u0007J\u0010\u0010C\u001a\u00020&2\u0006\u00109\u001a\u00020&H\u0016J,\u0010D\u001a\u00020(2\u0006\u0010)\u001a\u00020\u00172\f\u0010E\u001a\b\u0012\u0004\u0012\u00020\u0017042\f\u00103\u001a\b\u0012\u0004\u0012\u00020\u001704H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u0015\u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\f\u0012\n \u0019*\u0004\u0018\u00010\u00180\u00180\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001a\u001a\n \u0019*\u0004\u0018\u00010\u001b0\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u001c\u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\f\u0012\n \u0019*\u0004\u0018\u00010\u00180\u00180\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006F"}, d2={"Lcom/addonengine/addons/analytics/service/RestrictionsServiceImpl;", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "eventPublisher", "Lcom/atlassian/event/api/EventPublisher;", "cacheManager", "Lcom/atlassian/cache/CacheManager;", "confluenceInfoService", "Lcom/addonengine/addons/analytics/service/confluence/ConfluenceInfoService;", "restrictionsRepository", "Lcom/addonengine/addons/analytics/store/RestrictionsRepository;", "groupService", "Lcom/addonengine/addons/analytics/service/confluence/GroupService;", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "contentService", "Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "(Lcom/atlassian/event/api/EventPublisher;Lcom/atlassian/cache/CacheManager;Lcom/addonengine/addons/analytics/service/confluence/ConfluenceInfoService;Lcom/addonengine/addons/analytics/store/RestrictionsRepository;Lcom/addonengine/addons/analytics/service/confluence/GroupService;Lcom/addonengine/addons/analytics/service/confluence/UserService;Lcom/addonengine/addons/analytics/service/confluence/ContentService;)V", "getEventPublisher", "()Lcom/atlassian/event/api/EventPublisher;", "instanceRestrictionsCache", "Lcom/atlassian/cache/Cache;", "", "", "kotlin.jvm.PlatformType", "log", "Lorg/slf4j/Logger;", "spaceRestrictionsCache", "afterPropertiesSet", "", "buildInstanceCache", "buildSpacesCache", "clearCaches", "clearUserFromCacheByUsername", "userName", "destroy", "getInstanceRestrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/InstanceRestrictions;", "getSpaceRestrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/SpaceRestrictions;", "spaceKey", "isUserAllowedToViewContentAnalytics", "userKey", "contentId", "", "isUserAllowedToViewInstanceAnalytics", "isUserAllowedToViewInstanceAnalyticsInternal", "isUserAllowedToViewSpaceAnalytics", "isUserAllowedToViewSpaceAnalyticsInternal", "isUserMemberOfAllowedGroups", "userGroupRestrictions", "", "Lcom/addonengine/addons/analytics/store/model/UserGroupRestrictionData;", "groupsUserIsIn", "Lcom/addonengine/addons/analytics/service/confluence/model/Group;", "mapDataToSpaceRestrictions", "restrictions", "Lcom/addonengine/addons/analytics/store/model/SpaceRestrictionsData;", "users", "", "Lcom/addonengine/addons/analytics/service/confluence/model/User;", "onGroupMembershipDeletedEvent", "event", "Lcom/atlassian/crowd/event/group/GroupMembershipDeletedEvent;", "onGroupMembershipsCreatedEvent", "Lcom/atlassian/crowd/event/group/GroupMembershipsCreatedEvent;", "saveInstanceRestrictions", "saveSpaceRestrictions", "userRestrictions", "analytics"})
@SourceDebugExtension(value={"SMAP\nRestrictionsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RestrictionsServiceImpl.kt\ncom/addonengine/addons/analytics/service/RestrictionsServiceImpl\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,271:1\n11#2,11:272\n11#2,11:283\n1194#3,2:294\n1222#3,4:296\n1549#3:300\n1620#3,3:301\n1620#3,3:315\n1194#3,2:318\n1222#3,4:320\n1549#3:324\n1620#3,3:325\n1549#3:328\n1620#3,3:329\n1549#3:332\n1620#3,3:333\n1549#3:336\n1620#3,3:337\n1194#3,2:340\n1222#3,4:342\n1855#3,2:346\n1549#3:348\n1620#3,3:349\n1549#3:352\n1620#3,3:353\n1747#3,3:356\n766#3:359\n857#3,2:360\n1549#3:362\n1620#3,3:363\n1549#3:366\n1620#3,3:367\n526#4:304\n511#4,6:305\n125#5:311\n152#5,3:312\n*S KotlinDebug\n*F\n+ 1 RestrictionsServiceImpl.kt\ncom/addonengine/addons/analytics/service/RestrictionsServiceImpl\n*L\n52#1:272,11\n58#1:283,11\n71#1:294,2\n71#1:296,4\n72#1:300\n72#1:301,3\n84#1:315,3\n85#1:318,2\n85#1:320,4\n91#1:324\n91#1:325,3\n98#1:328\n98#1:329,3\n107#1:332\n107#1:333,3\n108#1:336\n108#1:337,3\n111#1:340,2\n111#1:342,4\n128#1:346,2\n138#1:348\n138#1:349,3\n141#1:352\n141#1:353,3\n179#1:356,3\n188#1:359\n188#1:360,2\n188#1:362\n188#1:363,3\n189#1:366\n189#1:367,3\n76#1:304\n76#1:305,6\n77#1:311\n77#1:312,3\n*E\n"})
public final class RestrictionsServiceImpl
implements RestrictionsService,
InitializingBean,
DisposableBean {
    @NotNull
    private final EventPublisher eventPublisher;
    @NotNull
    private final CacheManager cacheManager;
    @NotNull
    private final ConfluenceInfoService confluenceInfoService;
    @NotNull
    private final RestrictionsRepository restrictionsRepository;
    @NotNull
    private final GroupService groupService;
    @NotNull
    private final UserService userService;
    @NotNull
    private final ContentService contentService;
    @NotNull
    private final Cache<String, Boolean> instanceRestrictionsCache;
    @NotNull
    private final Cache<String, Boolean> spaceRestrictionsCache;
    private final Logger log;

    @Inject
    public RestrictionsServiceImpl(@ComponentImport @NotNull EventPublisher eventPublisher, @ComponentImport @NotNull CacheManager cacheManager, @NotNull ConfluenceInfoService confluenceInfoService, @NotNull RestrictionsRepository restrictionsRepository, @NotNull GroupService groupService, @NotNull UserService userService, @NotNull ContentService contentService) {
        Intrinsics.checkNotNullParameter((Object)eventPublisher, (String)"eventPublisher");
        Intrinsics.checkNotNullParameter((Object)cacheManager, (String)"cacheManager");
        Intrinsics.checkNotNullParameter((Object)confluenceInfoService, (String)"confluenceInfoService");
        Intrinsics.checkNotNullParameter((Object)restrictionsRepository, (String)"restrictionsRepository");
        Intrinsics.checkNotNullParameter((Object)groupService, (String)"groupService");
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        Intrinsics.checkNotNullParameter((Object)contentService, (String)"contentService");
        this.eventPublisher = eventPublisher;
        this.cacheManager = cacheManager;
        this.confluenceInfoService = confluenceInfoService;
        this.restrictionsRepository = restrictionsRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.contentService = contentService;
        this.instanceRestrictionsCache = this.buildInstanceCache();
        this.spaceRestrictionsCache = this.buildSpacesCache();
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @NotNull
    public final EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean isUserAllowedToViewInstanceAnalytics(@NotNull String userKey) {
        void klass$iv;
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "isUserAllowedToViewAnalytics_InstanceCheck";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.instanceRestrictionsCache.get((Object)userKey);
        Intrinsics.checkNotNull((Object)object);
        Boolean result$iv = (Boolean)object;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        Boolean bl2 = result$iv;
        Intrinsics.checkNotNullExpressionValue((Object)bl2, (String)"atlassianProfilingTimer(...)");
        return bl2;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean isUserAllowedToViewSpaceAnalytics(@NotNull String userKey, @NotNull String spaceKey) {
        void klass$iv;
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "isUserAllowedToViewAnalytics_SpaceCheck";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.spaceRestrictionsCache.get((Object)(userKey + '|' + spaceKey));
        Intrinsics.checkNotNull((Object)object);
        Boolean result$iv = (Boolean)object;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        Boolean bl2 = result$iv;
        Intrinsics.checkNotNullExpressionValue((Object)bl2, (String)"atlassianProfilingTimer(...)");
        return bl2;
    }

    @Override
    public boolean isUserAllowedToViewContentAnalytics(@NotNull String userKey, long contentId) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        String spaceKey = this.contentService.getById(contentId).getSpace().getKey();
        return this.isUserAllowedToViewSpaceAnalytics(userKey, spaceKey);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public InstanceRestrictions getInstanceRestrictions() {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Map.Entry it;
        void $this$filterTo$iv$iv;
        Map $this$filter$iv;
        Map $this$mapTo$iv$iv2;
        UserGroupRestrictionData it2;
        Object object;
        Iterator iterator;
        Iterable $this$associateByTo$iv$iv;
        List<UserGroupRestrictionData> restrictions = this.restrictionsRepository.getInstanceRestrictions().getUserGroups();
        Iterable $this$associateBy$iv = restrictions;
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Object destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            iterator = (UserGroupRestrictionData)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(new Group(it2.getGroupName()), element$iv$iv);
        }
        Map groupsWithSettings = destination$iv$iv;
        Iterable $this$map$iv2 = restrictions;
        boolean $i$f$map = false;
        $this$associateByTo$iv$iv = $this$map$iv2;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo22 = false;
        Iterator iterator2 = $this$mapTo$iv$iv2.iterator();
        while (iterator2.hasNext()) {
            Object item$iv$iv = iterator2.next();
            it2 = (UserGroupRestrictionData)item$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.add(it2.getGroupName());
        }
        List groupNames = (List)destination$iv$iv;
        List<String> groupsInConfluence = this.groupService.getGroupNamesInConfluence(groupNames);
        $this$mapTo$iv$iv2 = groupsWithSettings;
        boolean $i$f$filter = false;
        void $i$f$mapTo22 = $this$filter$iv;
        Object destination$iv$iv2 = new LinkedHashMap();
        boolean $i$f$filterTo = false;
        iterator = $this$filterTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry element$iv$iv;
            it = element$iv$iv = (Map.Entry)iterator.next();
            boolean bl = false;
            if (!groupsInConfluence.contains(((UserGroupRestrictionData)it.getValue()).getGroupName())) continue;
            destination$iv$iv2.put(element$iv$iv.getKey(), element$iv$iv.getValue());
        }
        $this$filter$iv = destination$iv$iv2;
        boolean $i$f$map2 = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv2 = new ArrayList($this$map$iv.size());
        boolean $i$f$mapTo = false;
        iterator = $this$mapTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv;
            it = item$iv$iv = iterator.next();
            object = destination$iv$iv2;
            boolean bl = false;
            object.add(new UserGroupRestriction((Group)it.getKey(), ((UserGroupRestrictionData)it.getValue()).getUseAnalytics()));
        }
        List existingGroupRestrictions = (List)destination$iv$iv2;
        return new InstanceRestrictions(existingGroupRestrictions);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public SpaceRestrictions getSpaceRestrictions(@NotNull String spaceKey) {
        void $this$associateByTo$iv$iv;
        Map map;
        void destination$iv;
        Object item$iv2;
        void $this$mapTo$iv;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        SpaceRestrictionsData restrictions = this.restrictionsRepository.getSpaceRestrictions(spaceKey);
        Iterable iterable = restrictions.getUsers();
        Collection collection = new LinkedHashSet();
        boolean $i$f$mapTo = false;
        for (Object item$iv2 : $this$mapTo$iv) {
            void it;
            UserRestrictionData userRestrictionData = (UserRestrictionData)item$iv2;
            map = destination$iv;
            boolean bl = false;
            map.add(it.getUserKey());
        }
        Set userKeys = (Set)destination$iv;
        Iterable $this$associateBy$iv = this.userService.getUserDetails(userKeys, true);
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        item$iv2 = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            User user = (User)element$iv$iv;
            map = destination$iv$iv;
            boolean bl = false;
            map.put(it.getUserKey(), element$iv$iv);
        }
        Map users = destination$iv$iv;
        return this.mapDataToSpaceRestrictions(restrictions, users);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public InstanceRestrictions saveInstanceRestrictions(@NotNull InstanceRestrictions restrictions) {
        void $this$mapTo$iv$iv;
        Collection collection;
        void $this$mapTo$iv$iv2;
        Intrinsics.checkNotNullParameter((Object)restrictions, (String)"restrictions");
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Iterable destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            void it;
            UserGroupRestriction userGroupRestriction = (UserGroupRestriction)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestrictionData(it.getGroup().getName(), it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        InstanceRestrictionsData restrictionsData = new InstanceRestrictionsData(list);
        InstanceRestrictionsData savedRestrictionsData = this.restrictionsRepository.saveInstanceRestrictions(restrictionsData);
        this.clearCaches();
        Iterable $this$map$iv2 = savedRestrictionsData.getUserGroups();
        boolean $i$f$map2 = false;
        destination$iv$iv = $this$map$iv2;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo2 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserGroupRestrictionData bl = (UserGroupRestrictionData)item$iv$iv;
            collection = destination$iv$iv2;
            boolean bl2 = false;
            collection.add(new UserGroupRestriction(new Group(it.getGroupName()), it.getUseAnalytics()));
        }
        List list2 = (List)destination$iv$iv2;
        return new InstanceRestrictions(list2);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public SpaceRestrictions saveSpaceRestrictions(@NotNull String spaceKey, @NotNull List<String> userRestrictions, @NotNull List<String> userGroupRestrictions) {
        void $this$associateByTo$iv$iv;
        void $this$associateBy$iv;
        String it;
        Collection<UserGroupRestrictionData> collection;
        Iterable $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter(userRestrictions, (String)"userRestrictions");
        Intrinsics.checkNotNullParameter(userGroupRestrictions, (String)"userGroupRestrictions");
        Iterable $this$map$iv = userGroupRestrictions;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string = (String)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestrictionData(it, true));
        }
        $this$map$iv = userRestrictions;
        collection = (List)destination$iv$iv;
        $i$f$map = false;
        $this$mapTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            it = (String)item$iv$iv;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            collection2.add(new UserRestrictionData(it, true));
        }
        List list = (List)destination$iv$iv;
        Collection<UserGroupRestrictionData> collection3 = collection;
        SpaceRestrictionsData restrictionsData = new SpaceRestrictionsData((List<UserGroupRestrictionData>)collection3, list);
        Iterable $i$f$map2 = this.userService.getUserDetails(CollectionsKt.toSet((Iterable)userRestrictions), true);
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        void $i$f$mapTo2 = $this$associateBy$iv;
        Map destination$iv$iv2 = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it2;
            User user = (User)element$iv$iv;
            Map map = destination$iv$iv2;
            boolean bl = false;
            map.put(it2.getUserKey(), element$iv$iv);
        }
        Map users = destination$iv$iv2;
        SpaceRestrictionsData savedRestrictionsData = this.restrictionsRepository.saveSpaceRestrictions(spaceKey, restrictionsData);
        this.clearCaches();
        return this.mapDataToSpaceRestrictions(savedRestrictionsData, users);
    }

    @EventListener
    public final void onGroupMembershipsCreatedEvent(@NotNull GroupMembershipsCreatedEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Collection collection = event.getEntityNames();
        Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"getEntityNames(...)");
        Iterable $this$forEach$iv = collection;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            String it = (String)element$iv;
            boolean bl = false;
            Intrinsics.checkNotNull((Object)it);
            this.clearUserFromCacheByUsername(it);
        }
    }

    @EventListener
    public final void onGroupMembershipDeletedEvent(@NotNull GroupMembershipDeletedEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = event.getEntityName();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getEntityName(...)");
        this.clearUserFromCacheByUsername(string);
    }

    private final SpaceRestrictions mapDataToSpaceRestrictions(SpaceRestrictionsData restrictions, Map<String, User> users) {
        UserRestrictionData it;
        Collection collection;
        Iterable $this$mapTo$iv$iv;
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            UserGroupRestrictionData userGroupRestrictionData = (UserGroupRestrictionData)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestriction(new Group(((UserGroupRestrictionData)((Object)it)).getGroupName()), ((UserGroupRestrictionData)((Object)it)).getUseAnalytics()));
        }
        $this$map$iv = restrictions.getUsers();
        collection = (List)destination$iv$iv;
        $i$f$map = false;
        $this$mapTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            it = (UserRestrictionData)item$iv$iv;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            User user = users.get(it.getUserKey());
            Intrinsics.checkNotNull((Object)user);
            collection2.add(new UserRestriction(user, it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        Collection collection3 = collection;
        return new SpaceRestrictions((List<UserGroupRestriction>)collection3, list);
    }

    private final boolean isUserAllowedToViewInstanceAnalyticsInternal(String userKey) {
        if (!this.userService.isUserLicensed(userKey)) {
            return false;
        }
        List<UserGroupRestrictionData> restrictions = this.restrictionsRepository.getInstanceRestrictions().getUserGroups();
        if (restrictions.isEmpty()) {
            return true;
        }
        List<Group> groupsUserIsIn = this.userService.getGroupsUserIsMemberOf(userKey);
        return this.isUserMemberOfAllowedGroups(restrictions, groupsUserIsIn);
    }

    private final boolean isUserAllowedToViewSpaceAnalyticsInternal(String userKey, String spaceKey) {
        boolean bl;
        SpaceRestrictionsData restrictions;
        block6: {
            if (!this.isUserAllowedToViewInstanceAnalytics(userKey)) {
                return false;
            }
            restrictions = this.restrictionsRepository.getSpaceRestrictions(spaceKey);
            if (restrictions.getUserGroups().isEmpty() && restrictions.getUsers().isEmpty()) {
                return true;
            }
            Iterable $this$any$iv = restrictions.getUsers();
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UserRestrictionData it = (UserRestrictionData)element$iv;
                    boolean bl2 = false;
                    if (!Intrinsics.areEqual((Object)it.getUserKey(), (Object)userKey)) continue;
                    bl = true;
                    break block6;
                }
                bl = false;
            }
        }
        if (bl) {
            return true;
        }
        List<Group> groupsUserIsIn = this.userService.getGroupsUserIsMemberOf(userKey);
        return this.isUserMemberOfAllowedGroups(restrictions.getUserGroups(), groupsUserIsIn);
    }

    /*
     * WARNING - void declaration
     */
    private final boolean isUserMemberOfAllowedGroups(List<UserGroupRestrictionData> userGroupRestrictions, List<Group> groupsUserIsIn) {
        void $this$mapTo$iv$iv;
        Collection collection;
        void $this$mapTo$iv$iv2;
        void $this$map$iv;
        UserGroupRestrictionData it;
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv = userGroupRestrictions;
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Iterable destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (UserGroupRestrictionData)element$iv$iv;
            boolean bl = false;
            if (!it.getUseAnalytics()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            it = (UserGroupRestrictionData)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getGroupName());
        }
        List allowedGroups = (List)destination$iv$iv;
        Iterable $this$map$iv2 = groupsUserIsIn;
        boolean $i$f$map2 = false;
        destination$iv$iv = $this$map$iv2;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo2 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it2;
            Group bl = (Group)item$iv$iv;
            collection = destination$iv$iv2;
            boolean bl2 = false;
            collection.add(it2.getName());
        }
        Set groupNamesUserIsIn = CollectionsKt.toSet((Iterable)((List)destination$iv$iv2));
        return !((Collection)CollectionsKt.intersect((Iterable)allowedGroups, (Iterable)groupNamesUserIsIn)).isEmpty();
    }

    private final Cache<String, Boolean> buildInstanceCache() {
        Cache cache = this.cacheManager.getCache("Analytics for Confluence - Instance Permissions", arg_0 -> RestrictionsServiceImpl.buildInstanceCache$lambda$20(this, arg_0), new CacheSettingsBuilder().remote().replicateViaInvalidation().replicateAsynchronously().maxEntries(this.confluenceInfoService.isDataCenter() ? 25000 : 10000).expireAfterWrite(24L, TimeUnit.HOURS).build());
        Intrinsics.checkNotNullExpressionValue((Object)cache, (String)"getCache(...)");
        return cache;
    }

    private final Cache<String, Boolean> buildSpacesCache() {
        Cache cache = this.cacheManager.getCache("Analytics for Confluence - Space Permissions", arg_0 -> RestrictionsServiceImpl.buildSpacesCache$lambda$21(this, arg_0), new CacheSettingsBuilder().remote().replicateViaInvalidation().replicateAsynchronously().maxEntries(this.confluenceInfoService.isDataCenter() ? 125000 : 50000).expireAfterWrite(24L, TimeUnit.HOURS).build());
        Intrinsics.checkNotNullExpressionValue((Object)cache, (String)"getCache(...)");
        return cache;
    }

    private final void clearCaches() {
        this.instanceRestrictionsCache.removeAll();
        this.spaceRestrictionsCache.removeAll();
    }

    private final void clearUserFromCacheByUsername(String userName) {
        this.log.debug("Group membership changed, clearing permission caches for user {}", (Object)userName);
        String userKey = this.userService.getUserKeyByUsername(userName);
        if (userKey == null) {
            this.log.debug("Couldn't resolve username. Clearing the whole instance restrictions cache");
            this.instanceRestrictionsCache.removeAll();
        } else {
            this.instanceRestrictionsCache.remove((Object)userKey);
        }
        this.spaceRestrictionsCache.removeAll();
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    private static final Boolean buildInstanceCache$lambda$20(RestrictionsServiceImpl this$0, String userKey) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        this$0.log.debug("Instance Permissions cache miss: " + userKey);
        return this$0.isUserAllowedToViewInstanceAnalyticsInternal(userKey);
    }

    private static final Boolean buildSpacesCache$lambda$21(RestrictionsServiceImpl this$0, String key) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        this$0.log.debug("Space Permissions cache miss: " + key);
        char[] cArray = new char[]{'|'};
        List list = StringsKt.split$default((CharSequence)key, (char[])cArray, (boolean)false, (int)0, (int)6, null);
        String userKey = (String)list.get(0);
        String spaceKey = (String)list.get(1);
        return this$0.isUserAllowedToViewSpaceAnalyticsInternal(userKey, spaceKey);
    }
}

