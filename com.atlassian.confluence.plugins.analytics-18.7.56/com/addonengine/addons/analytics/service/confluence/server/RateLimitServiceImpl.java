/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  javax.inject.Inject
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.jcip.annotations.GuardedBy
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.confluence.RateLimitService;
import com.addonengine.addons.analytics.service.model.ActiveSession;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.jcip.annotations.GuardedBy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExportAsDevService(value={RateLimitService.class})
@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\r\u0010\u000f\u001a\u00020\u0010H\u0001\u00a2\u0006\u0002\b\u0011J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0007H\u0016J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0015\u001a\u00020\u0007H\u0016R,\u0010\u0005\u001a\u001e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006j\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b`\t8\u0002X\u0083\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/RateLimitServiceImpl;", "Lcom/addonengine/addons/analytics/service/confluence/RateLimitService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/SettingsService;)V", "activeSessions", "Ljava/util/HashMap;", "", "Lcom/addonengine/addons/analytics/service/model/ActiveSession;", "Lkotlin/collections/HashMap;", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "activeSessionsCount", "", "activeSessionsCount$analytics", "clearActiveOperationCounts", "", "decrementOperationCount", "sessionId", "purgeStaleSessions", "rateLimit", "", "analytics"})
@SourceDebugExtension(value={"SMAP\nRateLimitServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RateLimitServiceImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/RateLimitServiceImpl\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,98:1\n468#2:99\n414#2:100\n494#2,7:105\n1238#3,4:101\n215#4,2:112\n*S KotlinDebug\n*F\n+ 1 RateLimitServiceImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/RateLimitServiceImpl\n*L\n63#1:99\n63#1:100\n83#1:105,7\n63#1:101,4\n87#1:112,2\n*E\n"})
public final class RateLimitServiceImpl
implements RateLimitService {
    @NotNull
    private final SettingsService settingsService;
    private final Logger log;
    @GuardedBy(value="lock")
    @NotNull
    private final HashMap<String, ActiveSession> activeSessions;
    @NotNull
    private final ReentrantLock lock;

    @Inject
    public RateLimitServiceImpl(@NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.settingsService = settingsService;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.activeSessions = new HashMap();
        this.lock = new ReentrantLock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean rateLimit(@NotNull String sessionId) {
        Intrinsics.checkNotNullParameter((Object)sessionId, (String)"sessionId");
        Lock lock = this.lock;
        lock.lock();
        try {
            boolean bl = false;
            this.purgeStaleSessions();
            if (this.activeSessions.containsKey(sessionId)) {
                ActiveSession activeSession = this.activeSessions.get(sessionId);
                Integer n = activeSession != null ? Integer.valueOf(activeSession.getActiveOperationCount()) : null;
                Intrinsics.checkNotNull((Object)n);
                if (n >= this.settingsService.getRateLimitSettings().getConcurrentOperationsPerSession()) {
                    boolean bl2 = true;
                    return bl2;
                }
                ActiveSession activeSession2 = this.activeSessions.get(sessionId);
                Intrinsics.checkNotNull((Object)activeSession2);
                ActiveSession currentSession = activeSession2;
                this.activeSessions.put(sessionId, currentSession.incremented());
                boolean bl3 = false;
                return bl3;
            }
            if (this.activeSessions.size() < this.settingsService.getRateLimitSettings().getConcurrentSessions()) {
                this.activeSessions.put(sessionId, new ActiveSession(1, null, 2, null));
                boolean bl4 = false;
                return bl4;
            }
            boolean bl5 = true;
            return bl5;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Override
    public void decrementOperationCount(@NotNull String sessionId) {
        Intrinsics.checkNotNullParameter((Object)sessionId, (String)"sessionId");
        Lock lock = this.lock;
        lock.lock();
        try {
            Map map;
            void $this$mapKeysTo$iv$iv;
            void $this$mapKeys$iv;
            ActiveSession activeSession;
            boolean bl = false;
            this.log.debug("Decrementing active operation count for {}", (Object)sessionId.hashCode());
            if (this.activeSessions.get(sessionId) == null) {
                return;
            }
            Object object = activeSession;
            Intrinsics.checkNotNull((Object)object);
            ActiveSession currentSession = object;
            if (currentSession.getActiveOperationCount() == 1) {
                this.activeSessions.remove(sessionId);
            } else {
                this.activeSessions.put(sessionId, new ActiveSession(currentSession.getActiveOperationCount() - 1, null, 2, null));
            }
            object = this.activeSessions;
            String string = "Current state of active sessions: {}";
            Logger logger = this.log;
            boolean $i$f$mapKeys = false;
            void var9_11 = $this$mapKeys$iv;
            Map destination$iv$iv = new LinkedHashMap(MapsKt.mapCapacity((int)$this$mapKeys$iv.size()));
            boolean $i$f$mapKeysTo = false;
            Iterable $this$associateByTo$iv$iv$iv = $this$mapKeysTo$iv$iv.entrySet();
            boolean $i$f$associateByTo = false;
            for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
                void it$iv$iv;
                void it;
                Map.Entry entry = (Map.Entry)element$iv$iv$iv;
                map = destination$iv$iv;
                boolean bl2 = false;
                Map.Entry entry2 = (Map.Entry)element$iv$iv$iv;
                Integer n = ((String)it.getKey()).hashCode();
                Map map2 = map;
                boolean bl3 = false;
                entry = it$iv$iv.getValue();
                map2.put(n, entry);
            }
            map = destination$iv$iv;
            logger.trace(string, (Object)map);
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearActiveOperationCounts() {
        Lock lock = this.lock;
        lock.lock();
        try {
            boolean bl = false;
            this.activeSessions.clear();
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void purgeStaleSessions() {
        Lock lock = this.lock;
        lock.lock();
        try {
            Map filterItems;
            boolean bl = false;
            Map $this$filterValues$iv = this.activeSessions;
            boolean $i$f$filterValues = false;
            LinkedHashMap result$iv = new LinkedHashMap();
            for (Map.Entry entry$iv : $this$filterValues$iv.entrySet()) {
                ActiveSession it = (ActiveSession)entry$iv.getValue();
                boolean bl2 = false;
                if (!it.getLastEventAt().isBefore(Instant.now().minus(this.settingsService.getRateLimitSettings().getStaleOperationSeconds(), ChronoUnit.SECONDS))) continue;
                result$iv.put(entry$iv.getKey(), entry$iv.getValue());
            }
            Map $this$forEach$iv = filterItems = (Map)result$iv;
            boolean $i$f$forEach = false;
            Iterator iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry element$iv;
                Map.Entry entry = element$iv = iterator.next();
                boolean bl3 = false;
                String sessionId = (String)entry.getKey();
                this.activeSessions.remove(sessionId);
            }
            Unit unit = Unit.INSTANCE;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    public final int activeSessionsCount$analytics() {
        Lock lock = this.lock;
        lock.lock();
        try {
            boolean bl = false;
            int n = this.activeSessions.size();
            return n;
        }
        finally {
            lock.unlock();
        }
    }
}

