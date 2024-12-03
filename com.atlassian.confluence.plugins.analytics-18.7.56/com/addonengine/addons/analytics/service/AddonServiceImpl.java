/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.AddonService;
import com.addonengine.addons.analytics.service.model.AddonDetails;
import com.addonengine.addons.analytics.service.model.SampleDataDetails;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.SampleEventRepository;
import com.addonengine.addons.analytics.store.model.SampleDataMetadata;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J!\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\nH\u0016\u00a2\u0006\u0002\u0010\fJ\u0014\u0010\r\u001a\u0004\u0018\u00010\b2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/service/AddonServiceImpl;", "Lcom/addonengine/addons/analytics/service/AddonService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "sampleEventRepository", "Lcom/addonengine/addons/analytics/store/SampleEventRepository;", "(Lcom/addonengine/addons/analytics/store/EventRepository;Lcom/addonengine/addons/analytics/store/SampleEventRepository;)V", "buildSampleData", "Lcom/addonengine/addons/analytics/service/model/SampleDataDetails;", "fromTime", "", "toTime", "(Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/service/model/SampleDataDetails;", "convertSampleDataMetadata", "sampleDataMetadata", "Lcom/addonengine/addons/analytics/store/model/SampleDataMetadata;", "getDetails", "Lcom/addonengine/addons/analytics/service/model/AddonDetails;", "analytics"})
public final class AddonServiceImpl
implements AddonService {
    @NotNull
    private final EventRepository eventRepository;
    @NotNull
    private final SampleEventRepository sampleEventRepository;

    @Autowired
    public AddonServiceImpl(@NotNull EventRepository eventRepository, @NotNull SampleEventRepository sampleEventRepository) {
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        Intrinsics.checkNotNullParameter((Object)sampleEventRepository, (String)"sampleEventRepository");
        this.eventRepository = eventRepository;
        this.sampleEventRepository = sampleEventRepository;
    }

    @Override
    @NotNull
    public AddonDetails getDetails() {
        Instant instant = this.eventRepository.getFirstEventDate();
        if (instant == null) {
            instant = Instant.now();
        }
        Instant firstEventAt2 = instant;
        SampleDataDetails sampleDataDetails = this.convertSampleDataMetadata(this.sampleEventRepository.getSampleDataMetadata());
        Intrinsics.checkNotNull((Object)firstEventAt2);
        return new AddonDetails(firstEventAt2, sampleDataDetails);
    }

    @Override
    @NotNull
    public SampleDataDetails buildSampleData(@Nullable Long fromTime, @Nullable Long toTime) {
        SampleDataDetails sampleDataDetails = this.convertSampleDataMetadata(this.sampleEventRepository.buildSampleEventStore(fromTime, toTime));
        Intrinsics.checkNotNull((Object)sampleDataDetails);
        return sampleDataDetails;
    }

    private final SampleDataDetails convertSampleDataMetadata(SampleDataMetadata sampleDataMetadata) {
        SampleDataDetails sampleDataDetails;
        SampleDataMetadata sampleDataMetadata2 = sampleDataMetadata;
        if (sampleDataMetadata2 != null) {
            SampleDataMetadata it = sampleDataMetadata2;
            boolean bl = false;
            sampleDataDetails = new SampleDataDetails(it.getMinDate(), it.getMaxDate(), it.getLastUpdatedAt());
        } else {
            sampleDataDetails = null;
        }
        return sampleDataDetails;
    }
}

