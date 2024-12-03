/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Deferred
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.InstanceAnalyticsService;
import com.addonengine.addons.analytics.service.UtilsKt;
import com.addonengine.addons.analytics.service.model.ActivityByPeriod;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.model.EventsByPeriodData;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J4\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J:\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00112\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/service/InstanceAnalyticsServiceImpl;", "Lcom/addonengine/addons/analytics/service/InstanceAnalyticsService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "(Lcom/addonengine/addons/analytics/store/EventRepository;)V", "getActivityByPeriod", "Lcom/addonengine/addons/analytics/service/model/ActivityByPeriod;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "getEventsByPeriod", "", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "events", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstanceAnalyticsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstanceAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/InstanceAnalyticsServiceImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,57:1\n1549#2:58\n1620#2,3:59\n*S KotlinDebug\n*F\n+ 1 InstanceAnalyticsServiceImpl.kt\ncom/addonengine/addons/analytics/service/InstanceAnalyticsServiceImpl\n*L\n52#1:58\n52#1:59,3\n*E\n"})
public final class InstanceAnalyticsServiceImpl
implements InstanceAnalyticsService {
    @NotNull
    private final EventRepository eventRepository;

    @Autowired
    public InstanceAnalyticsServiceImpl(@NotNull EventRepository eventRepository) {
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        this.eventRepository = eventRepository;
    }

    @Override
    @NotNull
    public ActivityByPeriod getActivityByPeriod(@NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull Set<? extends ContentType> contentTypes, @NotNull CountType countType) {
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)countType), (String)"countType");
        return (ActivityByPeriod)BuildersKt.runBlocking$default(null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super ActivityByPeriod>, Object>(contentTypes, this, datePeriodOptions, spaceTypes, countType, null){
            Object L$1;
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ Set<ContentType> $contentTypes;
            final /* synthetic */ InstanceAnalyticsServiceImpl this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ CountType $countType;
            {
                this.$contentTypes = $contentTypes;
                this.this$0 = $receiver;
                this.$datePeriodOptions = $datePeriodOptions;
                this.$spaceTypes = $spaceTypes;
                this.$countType = $countType;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object var1_1) {
                var12_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure((Object)var1_1);
                        $this$runBlocking = (CoroutineScope)this.L$0;
                        viewsByPeriod = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends PeriodActivity>>, Object>(this.$contentTypes, this.this$0, this.$datePeriodOptions, this.$spaceTypes, this.$countType, null){
                            int label;
                            final /* synthetic */ Set<ContentType> $contentTypes;
                            final /* synthetic */ InstanceAnalyticsServiceImpl this$0;
                            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
                            final /* synthetic */ Set<SpaceType> $spaceTypes;
                            final /* synthetic */ CountType $countType;
                            {
                                this.$contentTypes = $contentTypes;
                                this.this$0 = $receiver;
                                this.$datePeriodOptions = $datePeriodOptions;
                                this.$spaceTypes = $spaceTypes;
                                this.$countType = $countType;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        List<AnalyticsEvent> events2 = UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_VIEWED, AnalyticsEvent.BLOG_VIEWED);
                                        return InstanceAnalyticsServiceImpl.access$getEventsByPeriod(this.this$0, events2, this.$datePeriodOptions, this.$spaceTypes, this.$countType);
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<PeriodActivity>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        }), (int)3, null);
                        createsByPeriod = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends PeriodActivity>>, Object>(this.$contentTypes, this.this$0, this.$datePeriodOptions, this.$spaceTypes, this.$countType, null){
                            int label;
                            final /* synthetic */ Set<ContentType> $contentTypes;
                            final /* synthetic */ InstanceAnalyticsServiceImpl this$0;
                            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
                            final /* synthetic */ Set<SpaceType> $spaceTypes;
                            final /* synthetic */ CountType $countType;
                            {
                                this.$contentTypes = $contentTypes;
                                this.this$0 = $receiver;
                                this.$datePeriodOptions = $datePeriodOptions;
                                this.$spaceTypes = $spaceTypes;
                                this.$countType = $countType;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        List<AnalyticsEvent> events2 = UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_CREATED, AnalyticsEvent.BLOG_CREATED);
                                        return InstanceAnalyticsServiceImpl.access$getEventsByPeriod(this.this$0, events2, this.$datePeriodOptions, this.$spaceTypes, this.$countType);
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<PeriodActivity>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        }), (int)3, null);
                        updatesByPeriod = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends PeriodActivity>>, Object>(this.$contentTypes, this.this$0, this.$datePeriodOptions, this.$spaceTypes, this.$countType, null){
                            int label;
                            final /* synthetic */ Set<ContentType> $contentTypes;
                            final /* synthetic */ InstanceAnalyticsServiceImpl this$0;
                            final /* synthetic */ DatePeriodOptions $datePeriodOptions;
                            final /* synthetic */ Set<SpaceType> $spaceTypes;
                            final /* synthetic */ CountType $countType;
                            {
                                this.$contentTypes = $contentTypes;
                                this.this$0 = $receiver;
                                this.$datePeriodOptions = $datePeriodOptions;
                                this.$spaceTypes = $spaceTypes;
                                this.$countType = $countType;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        List<AnalyticsEvent> events2 = UtilsKt.contentTypesToEvents(this.$contentTypes, AnalyticsEvent.PAGE_UPDATED, AnalyticsEvent.BLOG_UPDATED);
                                        return InstanceAnalyticsServiceImpl.access$getEventsByPeriod(this.this$0, events2, this.$datePeriodOptions, this.$spaceTypes, this.$countType);
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<PeriodActivity>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        }), (int)3, null);
                        this.L$0 = createsByPeriod;
                        this.L$1 = updatesByPeriod;
                        this.label = 1;
                        v0 = viewsByPeriod.await((Continuation)this);
                        if (v0 == var12_2) {
                            return var12_2;
                        }
                        ** GOTO lbl21
                    }
                    case 1: {
                        updatesByPeriod = (Deferred)this.L$1;
                        createsByPeriod = (Deferred)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v0 = $result;
lbl21:
                        // 2 sources

                        var6_7 = (List)v0;
                        this.L$0 = updatesByPeriod;
                        this.L$1 = var6_7;
                        this.label = 2;
                        v1 = createsByPeriod.await((Continuation)this);
                        if (v1 == var12_2) {
                            return var12_2;
                        }
                        ** GOTO lbl34
                    }
                    case 2: {
                        var6_7 = (List)this.L$1;
                        updatesByPeriod = (Deferred)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v1 = $result;
lbl34:
                        // 2 sources

                        var7_8 = (List)v1;
                        this.L$0 = var6_7;
                        this.L$1 = var7_8;
                        this.label = 3;
                        v2 = updatesByPeriod.await((Continuation)this);
                        if (v2 == var12_2) {
                            return var12_2;
                        }
                        ** GOTO lbl47
                    }
                    case 3: {
                        var7_8 = (List)this.L$1;
                        var6_7 = (List)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v2 = $result;
lbl47:
                        // 2 sources

                        var8_9 = v2;
                        var9_10 = (List)var8_9;
                        var10_11 = var7_8;
                        var11_12 = var6_7;
                        return new ActivityByPeriod(var11_12, var10_11, var9_10);
                    }
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<CoroutineScope, Continuation<? super ActivityByPeriod>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)function2;
            }

            @Nullable
            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super ActivityByPeriod> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }), (int)1, null);
    }

    /*
     * WARNING - void declaration
     */
    private final List<PeriodActivity> getEventsByPeriod(List<? extends AnalyticsEvent> events2, DatePeriodOptions datePeriodOptions, Set<? extends SpaceType> spaceTypes, CountType countType) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Iterable iterable = this.eventRepository.getEventsByPeriodForSpaces(events2, datePeriodOptions, spaceTypes, countType);
        DatePeriodOptions datePeriodOptions2 = datePeriodOptions;
        boolean $i$f$map = false;
        void var7_8 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            EventsByPeriodData eventsByPeriodData = (EventsByPeriodData)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new PeriodActivity(it.getDate(), it.getTotal()));
        }
        return DatePeriodOptionsKt.fillInMissingDates(datePeriodOptions2, (List)destination$iv$iv);
    }

    public static final /* synthetic */ List access$getEventsByPeriod(InstanceAnalyticsServiceImpl $this, List events2, DatePeriodOptions datePeriodOptions, Set spaceTypes, CountType countType) {
        return $this.getEventsByPeriod(events2, datePeriodOptions, spaceTypes, countType);
    }
}

