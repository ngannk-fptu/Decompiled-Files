/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KClass
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.scheduler;

import com.addonengine.addons.analytics.scheduler.DataRetentionJobRunner;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.addonengine.addons.analytics.store.EventRepository;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.util.profiling.UtilTimerStack;
import java.io.Serializable;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/scheduler/DataRetentionJobRunnerImpl;", "Lcom/addonengine/addons/analytics/scheduler/DataRetentionJobRunner;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/store/EventRepository;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "runJob", "Lcom/atlassian/scheduler/JobRunnerResponse;", "request", "Lcom/atlassian/scheduler/JobRunnerRequest;", "analytics"})
@SourceDebugExtension(value={"SMAP\nDataRetentionJobRunnerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DataRetentionJobRunnerImpl.kt\ncom/addonengine/addons/analytics/scheduler/DataRetentionJobRunnerImpl\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n*L\n1#1,30:1\n11#2,11:31\n*S KotlinDebug\n*F\n+ 1 DataRetentionJobRunnerImpl.kt\ncom/addonengine/addons/analytics/scheduler/DataRetentionJobRunnerImpl\n*L\n25#1:31,11\n*E\n"})
public final class DataRetentionJobRunnerImpl
extends DataRetentionJobRunner {
    @NotNull
    private final EventRepository eventRepository;
    @NotNull
    private final SettingsService settingsService;

    @Inject
    public DataRetentionJobRunnerImpl(@NotNull EventRepository eventRepository, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.eventRepository = eventRepository;
        this.settingsService = settingsService;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        Intrinsics.checkNotNullParameter((Object)request, (String)"request");
        Serializable serializable = (Serializable)request.getJobConfig().getParameters().get("BATCH_SIZE");
        if (serializable == null) {
            serializable = Long.valueOf(10000L);
        }
        long batchSize = (Long)serializable;
        DataRetentionSettings retentionSettings = this.settingsService.getDataRetentionSettings();
        if (!retentionSettings.getActive()) {
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success();
            Intrinsics.checkNotNullExpressionValue((Object)jobRunnerResponse, (String)"success(...)");
            return jobRunnerResponse;
        }
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "dataRetention";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        long deletedEventsCount = this.eventRepository.deleteEventsBeforeDate(retentionSettings.getMinDate(), batchSize);
        JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success((String)("Deleted " + deletedEventsCount + " Events outside of data retention period."));
        Intrinsics.checkNotNullExpressionValue((Object)jobRunnerResponse, (String)"success(...)");
        return jobRunnerResponse;
    }
}

