/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KProperty1
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.extensions.ao.KotlinActiveObjectExtensionsKt;
import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventCursor;
import com.addonengine.addons.analytics.service.EventListIterator;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.EventService;
import com.addonengine.addons.analytics.service.EventServiceImpl;
import com.addonengine.addons.analytics.service.HashService;
import com.addonengine.addons.analytics.service.Page;
import com.addonengine.addons.analytics.service.PageRequest;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.UserAgentService;
import com.addonengine.addons.analytics.service.model.settings.PrivacySettings;
import com.addonengine.addons.analytics.store.EventRepository;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty1;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ExportAsService(value={EventService.class})
@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001BE\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0001\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\u0002\u0010\u0010J(\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J(\u0010\"\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020#2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J(\u0010$\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020%2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J(\u0010&\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020'2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J$\u0010(\u001a\u00020)2\u0006\u0010\u001d\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010,2\b\u0010-\u001a\u0004\u0018\u00010\u001cH\u0016J,\u0010(\u001a\u00020)2\u0006\u0010\u001d\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010,2\b\u0010-\u001a\u0004\u0018\u00010\u001c2\u0006\u0010 \u001a\u00020!H\u0016J\u0016\u0010.\u001a\b\u0012\u0004\u0012\u0002000/2\u0006\u00101\u001a\u000202H\u0016J\u0016\u00103\u001a\b\u0012\u0004\u0012\u0002000/2\u0006\u00101\u001a\u000202H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\n \u0015*\u0004\u0018\u00010\u00140\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0016\u001a\n \u0015*\u0004\u0018\u00010\u00170\u0017X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00064"}, d2={"Lcom/addonengine/addons/analytics/service/EventServiceImpl;", "Lcom/addonengine/addons/analytics/service/EventService;", "ao", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "transactionTemplate", "Lcom/atlassian/sal/api/transaction/TransactionTemplate;", "userAgentService", "Lcom/addonengine/addons/analytics/service/UserAgentService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "hashService", "Lcom/addonengine/addons/analytics/service/HashService;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "(Lcom/atlassian/activeobjects/external/ActiveObjects;Lcom/atlassian/sal/api/transaction/TransactionTemplate;Lcom/addonengine/addons/analytics/service/UserAgentService;Lcom/addonengine/addons/analytics/service/SettingsService;Lcom/addonengine/addons/analytics/service/HashService;Lcom/atlassian/sal/api/user/UserManager;Lcom/addonengine/addons/analytics/store/EventRepository;)V", "getAo", "()Lcom/atlassian/activeobjects/external/ActiveObjects;", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "streamBatchSize", "", "Ljava/lang/Integer;", "createAttachmentEvent", "Lcom/addonengine/addons/analytics/store/server/ao/Event;", "name", "", "confEvent", "Lcom/atlassian/confluence/event/events/content/attachment/AttachmentEvent;", "userKey", "eventAt", "", "createBlogEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostEvent;", "createCommentEvent", "Lcom/atlassian/confluence/event/events/content/comment/CommentEvent;", "createPageEvent", "Lcom/atlassian/confluence/event/events/content/page/PageEvent;", "save", "", "Lcom/atlassian/confluence/event/events/ConfluenceEvent;", "user", "Lcom/atlassian/sal/api/user/UserProfile;", "userAgent", "stream", "Ljava/util/stream/Stream;", "Lcom/addonengine/addons/analytics/service/Event;", "query", "Lcom/addonengine/addons/analytics/service/EventQuery;", "streamUnsecured", "analytics"})
@SourceDebugExtension(value={"SMAP\nEventServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventServiceImpl.kt\ncom/addonengine/addons/analytics/service/EventServiceImpl\n+ 2 KotlinActiveObjectExtensions.kt\ncom/addonengine/addons/analytics/extensions/ao/KotlinActiveObjectExtensionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,162:1\n16#2:163\n17#2:170\n16#2:171\n17#2:178\n16#2:179\n17#2:186\n16#2:187\n17#2:194\n125#3:164\n152#3,3:165\n125#3:172\n152#3,3:173\n125#3:180\n152#3,3:181\n125#3:188\n152#3,3:189\n37#4,2:168\n37#4,2:176\n37#4,2:184\n37#4,2:192\n*S KotlinDebug\n*F\n+ 1 EventServiceImpl.kt\ncom/addonengine/addons/analytics/service/EventServiceImpl\n*L\n97#1:163\n97#1:170\n109#1:171\n109#1:178\n121#1:179\n121#1:186\n133#1:187\n133#1:194\n97#1:164\n97#1:165,3\n109#1:172\n109#1:173,3\n121#1:180\n121#1:181,3\n133#1:188\n133#1:189,3\n97#1:168,2\n109#1:176,2\n121#1:184,2\n133#1:192,2\n*E\n"})
public final class EventServiceImpl
implements EventService {
    @NotNull
    private final ActiveObjects ao;
    @NotNull
    private final TransactionTemplate transactionTemplate;
    @NotNull
    private final UserAgentService userAgentService;
    @NotNull
    private final SettingsService settingsService;
    @NotNull
    private final HashService hashService;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final EventRepository eventRepository;
    private final Logger log;
    private final Integer streamBatchSize;

    @Autowired
    public EventServiceImpl(@ComponentImport @NotNull ActiveObjects ao, @ComponentImport @NotNull TransactionTemplate transactionTemplate, @NotNull UserAgentService userAgentService, @NotNull SettingsService settingsService, @NotNull HashService hashService, @ComponentImport @NotNull UserManager userManager, @NotNull EventRepository eventRepository) {
        Intrinsics.checkNotNullParameter((Object)ao, (String)"ao");
        Intrinsics.checkNotNullParameter((Object)transactionTemplate, (String)"transactionTemplate");
        Intrinsics.checkNotNullParameter((Object)userAgentService, (String)"userAgentService");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        Intrinsics.checkNotNullParameter((Object)hashService, (String)"hashService");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        this.ao = ao;
        this.transactionTemplate = transactionTemplate;
        this.userAgentService = userAgentService;
        this.settingsService = settingsService;
        this.hashService = hashService;
        this.userManager = userManager;
        this.eventRepository = eventRepository;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.streamBatchSize = Integer.getInteger("confluence.analytics.event.stream.batch.size", 10000);
    }

    @Override
    @NotNull
    public ActiveObjects getAo() {
        return this.ao;
    }

    @Override
    public void save(@NotNull ConfluenceEvent confEvent, @Nullable UserProfile user, @Nullable String userAgent) {
        Intrinsics.checkNotNullParameter((Object)confEvent, (String)"confEvent");
        this.save(confEvent, user, userAgent, confEvent.getTimestamp());
    }

    @Override
    public void save(@NotNull ConfluenceEvent confEvent, @Nullable UserProfile user, @Nullable String userAgent, long eventAt) {
        String string;
        Intrinsics.checkNotNullParameter((Object)confEvent, (String)"confEvent");
        if (userAgent != null && this.userAgentService.isRobotUserAgent(userAgent)) {
            this.log.debug("User agent matches a robot, so the event was not saved.");
            return;
        }
        PrivacySettings privacySettings = this.settingsService.getPrivacySettings();
        UserProfile userProfile = user;
        String userKey = userProfile != null && (userProfile = userProfile.getUserKey()) != null ? userProfile.getStringValue() : null;
        if (userKey == null) {
            string = "[anonymous]";
        } else if (privacySettings.getEnabled()) {
            String string2 = privacySettings.getInstanceSalt();
            Intrinsics.checkNotNull((Object)string2);
            string = this.hashService.hashString(userKey, string2);
        } else {
            string = userKey;
        }
        String transformedUserKey = string;
        this.transactionTemplate.execute(() -> EventServiceImpl.save$lambda$0(confEvent, this, transformedUserKey, eventAt));
    }

    /*
     * WARNING - void declaration
     */
    private final com.addonengine.addons.analytics.store.server.ao.Event createAttachmentEvent(String name, AttachmentEvent confEvent, String userKey, long eventAt) {
        void $this$createWithProps$iv;
        void $this$toTypedArray$iv$iv;
        void $this$mapTo$iv$iv$iv;
        ActiveObjects activeObjects = this.getAo();
        Pair[] pairArray = new Pair[7];
        pairArray[0] = TuplesKt.to((Object)((Object)createAttachmentEvent.1.INSTANCE), (Object)name);
        pairArray[1] = TuplesKt.to((Object)((Object)createAttachmentEvent.2.INSTANCE), (Object)eventAt);
        pairArray[2] = TuplesKt.to((Object)((Object)createAttachmentEvent.3.INSTANCE), (Object)confEvent.getContent().getId());
        pairArray[3] = TuplesKt.to((Object)((Object)createAttachmentEvent.4.INSTANCE), (Object)confEvent.getAttachment().getSpaceKey());
        pairArray[4] = TuplesKt.to((Object)((Object)createAttachmentEvent.5.INSTANCE), (Object)userKey);
        Date date = confEvent.getAttachment().getLastModificationDate();
        pairArray[5] = TuplesKt.to((Object)((Object)createAttachmentEvent.6.INSTANCE), (Object)(date != null ? Long.valueOf(date.getTime()) : null));
        pairArray[6] = TuplesKt.to((Object)((Object)createAttachmentEvent.7.INSTANCE), (Object)confEvent.getAttachment().getId());
        Map entityProperties$iv = MapsKt.mapOf((Pair[])pairArray);
        boolean $i$f$createWithProps = false;
        Object $this$map$iv$iv = entityProperties$iv;
        boolean $i$f$map = false;
        Map map = $this$map$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList($this$map$iv$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv$iv;
            Map.Entry entry = item$iv$iv$iv = iterator.next();
            Collection collection = destination$iv$iv$iv;
            boolean bl = false;
            KProperty1 prop$iv = (KProperty1)entry.getKey();
            Object value$iv = entry.getValue();
            collection.add(new DBParam(KotlinActiveObjectExtensionsKt.toDBParamFieldName(prop$iv.getName()), value$iv));
        }
        $this$map$iv$iv = (List)destination$iv$iv$iv;
        boolean $i$f$toTypedArray = false;
        void thisCollection$iv$iv = $this$toTypedArray$iv$iv;
        DBParam[] params$iv = thisCollection$iv$iv.toArray(new DBParam[0]);
        RawEntity rawEntity = $this$createWithProps$iv.create(com.addonengine.addons.analytics.store.server.ao.Event.class, Arrays.copyOf(params$iv, params$iv.length));
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"create(...)");
        return (com.addonengine.addons.analytics.store.server.ao.Event)rawEntity;
    }

    /*
     * WARNING - void declaration
     */
    private final com.addonengine.addons.analytics.store.server.ao.Event createPageEvent(String name, PageEvent confEvent, String userKey, long eventAt) {
        void $this$createWithProps$iv;
        void $this$toTypedArray$iv$iv;
        void $this$mapTo$iv$iv$iv;
        ActiveObjects activeObjects = this.getAo();
        Pair[] pairArray = new Pair[7];
        pairArray[0] = TuplesKt.to((Object)((Object)createPageEvent.1.INSTANCE), (Object)name);
        pairArray[1] = TuplesKt.to((Object)((Object)createPageEvent.2.INSTANCE), (Object)eventAt);
        pairArray[2] = TuplesKt.to((Object)((Object)createPageEvent.3.INSTANCE), (Object)confEvent.getContent().getId());
        pairArray[3] = TuplesKt.to((Object)((Object)createPageEvent.4.INSTANCE), (Object)confEvent.getPage().getSpaceKey());
        pairArray[4] = TuplesKt.to((Object)((Object)createPageEvent.5.INSTANCE), (Object)userKey);
        Date date = confEvent.getContent().getLastModificationDate();
        pairArray[5] = TuplesKt.to((Object)((Object)createPageEvent.6.INSTANCE), (Object)(date != null ? Long.valueOf(date.getTime()) : null));
        pairArray[6] = TuplesKt.to((Object)((Object)createPageEvent.7.INSTANCE), (Object)confEvent.getContent().getId());
        Map entityProperties$iv = MapsKt.mapOf((Pair[])pairArray);
        boolean $i$f$createWithProps = false;
        Object $this$map$iv$iv = entityProperties$iv;
        boolean $i$f$map = false;
        Map map = $this$map$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList($this$map$iv$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv$iv;
            Map.Entry entry = item$iv$iv$iv = iterator.next();
            Collection collection = destination$iv$iv$iv;
            boolean bl = false;
            KProperty1 prop$iv = (KProperty1)entry.getKey();
            Object value$iv = entry.getValue();
            collection.add(new DBParam(KotlinActiveObjectExtensionsKt.toDBParamFieldName(prop$iv.getName()), value$iv));
        }
        $this$map$iv$iv = (List)destination$iv$iv$iv;
        boolean $i$f$toTypedArray = false;
        void thisCollection$iv$iv = $this$toTypedArray$iv$iv;
        DBParam[] params$iv = thisCollection$iv$iv.toArray(new DBParam[0]);
        RawEntity rawEntity = $this$createWithProps$iv.create(com.addonengine.addons.analytics.store.server.ao.Event.class, Arrays.copyOf(params$iv, params$iv.length));
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"create(...)");
        return (com.addonengine.addons.analytics.store.server.ao.Event)rawEntity;
    }

    /*
     * WARNING - void declaration
     */
    private final com.addonengine.addons.analytics.store.server.ao.Event createBlogEvent(String name, BlogPostEvent confEvent, String userKey, long eventAt) {
        void $this$createWithProps$iv;
        void $this$toTypedArray$iv$iv;
        void $this$mapTo$iv$iv$iv;
        ActiveObjects activeObjects = this.getAo();
        Pair[] pairArray = new Pair[7];
        pairArray[0] = TuplesKt.to((Object)((Object)createBlogEvent.1.INSTANCE), (Object)name);
        pairArray[1] = TuplesKt.to((Object)((Object)createBlogEvent.2.INSTANCE), (Object)eventAt);
        pairArray[2] = TuplesKt.to((Object)((Object)createBlogEvent.3.INSTANCE), (Object)confEvent.getContent().getId());
        pairArray[3] = TuplesKt.to((Object)((Object)createBlogEvent.4.INSTANCE), (Object)confEvent.getBlogPost().getSpaceKey());
        pairArray[4] = TuplesKt.to((Object)((Object)createBlogEvent.5.INSTANCE), (Object)userKey);
        Date date = confEvent.getContent().getLastModificationDate();
        pairArray[5] = TuplesKt.to((Object)((Object)createBlogEvent.6.INSTANCE), (Object)(date != null ? Long.valueOf(date.getTime()) : null));
        pairArray[6] = TuplesKt.to((Object)((Object)createBlogEvent.7.INSTANCE), (Object)confEvent.getContent().getId());
        Map entityProperties$iv = MapsKt.mapOf((Pair[])pairArray);
        boolean $i$f$createWithProps = false;
        Object $this$map$iv$iv = entityProperties$iv;
        boolean $i$f$map = false;
        Map map = $this$map$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList($this$map$iv$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv$iv;
            Map.Entry entry = item$iv$iv$iv = iterator.next();
            Collection collection = destination$iv$iv$iv;
            boolean bl = false;
            KProperty1 prop$iv = (KProperty1)entry.getKey();
            Object value$iv = entry.getValue();
            collection.add(new DBParam(KotlinActiveObjectExtensionsKt.toDBParamFieldName(prop$iv.getName()), value$iv));
        }
        $this$map$iv$iv = (List)destination$iv$iv$iv;
        boolean $i$f$toTypedArray = false;
        void thisCollection$iv$iv = $this$toTypedArray$iv$iv;
        DBParam[] params$iv = thisCollection$iv$iv.toArray(new DBParam[0]);
        RawEntity rawEntity = $this$createWithProps$iv.create(com.addonengine.addons.analytics.store.server.ao.Event.class, Arrays.copyOf(params$iv, params$iv.length));
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"create(...)");
        return (com.addonengine.addons.analytics.store.server.ao.Event)rawEntity;
    }

    /*
     * WARNING - void declaration
     */
    private final com.addonengine.addons.analytics.store.server.ao.Event createCommentEvent(String name, CommentEvent confEvent, String userKey, long eventAt) {
        void $this$createWithProps$iv;
        void $this$toTypedArray$iv$iv;
        void $this$mapTo$iv$iv$iv;
        ActiveObjects activeObjects = this.getAo();
        Pair[] pairArray = new Pair[7];
        pairArray[0] = TuplesKt.to((Object)((Object)createCommentEvent.1.INSTANCE), (Object)name);
        pairArray[1] = TuplesKt.to((Object)((Object)createCommentEvent.2.INSTANCE), (Object)eventAt);
        ContentEntityObject contentEntityObject = confEvent.getComment().getContainer();
        pairArray[2] = TuplesKt.to((Object)((Object)createCommentEvent.3.INSTANCE), (Object)(contentEntityObject != null ? Long.valueOf(contentEntityObject.getId()) : null));
        pairArray[3] = TuplesKt.to((Object)((Object)createCommentEvent.4.INSTANCE), (Object)confEvent.getComment().getSpace().getKey());
        pairArray[4] = TuplesKt.to((Object)((Object)createCommentEvent.5.INSTANCE), (Object)userKey);
        Date date = confEvent.getComment().getLastModificationDate();
        pairArray[5] = TuplesKt.to((Object)((Object)createCommentEvent.6.INSTANCE), (Object)(date != null ? Long.valueOf(date.getTime()) : null));
        pairArray[6] = TuplesKt.to((Object)((Object)createCommentEvent.7.INSTANCE), (Object)confEvent.getComment().getId());
        Map entityProperties$iv = MapsKt.mapOf((Pair[])pairArray);
        boolean $i$f$createWithProps = false;
        Object $this$map$iv$iv = entityProperties$iv;
        boolean $i$f$map = false;
        Map map = $this$map$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList($this$map$iv$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv$iv;
            Map.Entry entry = item$iv$iv$iv = iterator.next();
            Collection collection = destination$iv$iv$iv;
            boolean bl = false;
            KProperty1 prop$iv = (KProperty1)entry.getKey();
            Object value$iv = entry.getValue();
            collection.add(new DBParam(KotlinActiveObjectExtensionsKt.toDBParamFieldName(prop$iv.getName()), value$iv));
        }
        $this$map$iv$iv = (List)destination$iv$iv$iv;
        boolean $i$f$toTypedArray = false;
        void thisCollection$iv$iv = $this$toTypedArray$iv$iv;
        DBParam[] params$iv = thisCollection$iv$iv.toArray(new DBParam[0]);
        RawEntity rawEntity = $this$createWithProps$iv.create(com.addonengine.addons.analytics.store.server.ao.Event.class, Arrays.copyOf(params$iv, params$iv.length));
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"create(...)");
        return (com.addonengine.addons.analytics.store.server.ao.Event)rawEntity;
    }

    @Override
    @NotNull
    public Stream<Event> stream(@NotNull EventQuery query) throws PermissionException {
        Intrinsics.checkNotNullParameter((Object)query, (String)"query");
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
            throw new PermissionException("stream is only allowed for system admins.");
        }
        return this.streamUnsecured(query);
    }

    @Override
    @NotNull
    public Stream<Event> streamUnsecured(@NotNull EventQuery query) {
        Intrinsics.checkNotNullParameter((Object)query, (String)"query");
        Function2 function2 = (Function2)new Function2<EventQuery, PageRequest<EventCursor>, Page<List<? extends Event>, EventCursor>>((Object)this.eventRepository){

            @NotNull
            public final Page<List<Event>, EventCursor> invoke(@NotNull EventQuery p0, @NotNull PageRequest<EventCursor> p1) {
                Intrinsics.checkNotNullParameter((Object)p0, (String)"p0");
                Intrinsics.checkNotNullParameter(p1, (String)"p1");
                return ((EventRepository)this.receiver).getEvents(p0, p1);
            }
        };
        Integer n = this.streamBatchSize;
        Intrinsics.checkNotNullExpressionValue((Object)n, (String)"streamBatchSize");
        EventListIterator eventListIterator2 = new EventListIterator((Function2<? super EventQuery, ? super PageRequest<EventCursor>, Page<List<Event>, EventCursor>>)function2, query, ((Number)n).intValue());
        Stream<Event> stream = StreamSupport.stream(((Iterable)new Iterable<List<? extends Event>>(eventListIterator2){
            final /* synthetic */ EventListIterator $eventListIterator$inlined;
            {
                this.$eventListIterator$inlined = eventListIterator2;
            }

            @NotNull
            public Iterator<List<? extends Event>> iterator() {
                boolean bl = false;
                return (Iterator)((Object)this.$eventListIterator$inlined);
            }
        }).spliterator(), false).flatMap(arg_0 -> EventServiceImpl.streamUnsecured$lambda$2(streamUnsecured.2.INSTANCE, arg_0));
        Intrinsics.checkNotNullExpressionValue(stream, (String)"flatMap(...)");
        return stream;
    }

    private static final Unit save$lambda$0(ConfluenceEvent $confEvent, EventServiceImpl this$0, String $transformedUserKey, long $eventAt) {
        com.addonengine.addons.analytics.store.server.ao.Event event;
        Intrinsics.checkNotNullParameter((Object)$confEvent, (String)"$confEvent");
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$transformedUserKey, (String)"$transformedUserKey");
        ConfluenceEvent confluenceEvent = $confEvent;
        if (confluenceEvent instanceof PageViewEvent) {
            event = this$0.createPageEvent("page_viewed", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof PageCreateEvent) {
            event = this$0.createPageEvent("page_created", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof PageUpdateEvent) {
            event = this$0.createPageEvent("page_updated", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof PageRemoveEvent) {
            event = this$0.createPageEvent("page_removed", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof PageTrashedEvent) {
            event = this$0.createPageEvent("page_trashed", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof PageRestoreEvent) {
            event = this$0.createPageEvent("page_restored", (PageEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostViewEvent) {
            event = this$0.createBlogEvent("blog_viewed", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostCreateEvent) {
            event = this$0.createBlogEvent("blog_created", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostUpdateEvent) {
            event = this$0.createBlogEvent("blog_updated", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostRemoveEvent) {
            event = this$0.createBlogEvent("blog_removed", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostTrashedEvent) {
            event = this$0.createBlogEvent("blog_trashed", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof BlogPostRestoreEvent) {
            event = this$0.createBlogEvent("blog_restored", (BlogPostEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof CommentRemoveEvent) {
            event = this$0.createCommentEvent("comment_removed", (CommentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof CommentCreateEvent) {
            event = this$0.createCommentEvent("comment_created", (CommentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof CommentUpdateEvent) {
            event = this$0.createCommentEvent("comment_updated", (CommentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof AttachmentViewEvent) {
            event = this$0.createAttachmentEvent("attachment_viewed", (AttachmentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof AttachmentCreateEvent) {
            event = this$0.createAttachmentEvent("attachment_created", (AttachmentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof AttachmentUpdateEvent) {
            event = this$0.createAttachmentEvent("attachment_updated", (AttachmentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else if (confluenceEvent instanceof AttachmentRemoveEvent) {
            event = this$0.createAttachmentEvent("attachment_removed", (AttachmentEvent)$confEvent, $transformedUserKey, $eventAt);
        } else {
            throw new IllegalArgumentException("Unsupported event attempted to be saved: " + $confEvent);
        }
        com.addonengine.addons.analytics.store.server.ao.Event event2 = event;
        event2.save();
        this$0.log.debug("Captured event: " + event2.getId() + ", " + event2.getName() + ", " + event2.getContainerId() + ", " + event2.getContentId() + ", " + event2.getVersionModificationDate() + ", " + event2.getSpaceKey() + ", " + event2.getUserKey());
        return Unit.INSTANCE;
    }

    private static final Stream streamUnsecured$lambda$2(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Stream)$tmp0.invoke(p0);
    }
}

