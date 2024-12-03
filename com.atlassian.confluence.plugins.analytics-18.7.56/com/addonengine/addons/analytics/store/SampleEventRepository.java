/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store;

import com.addonengine.addons.analytics.store.model.SampleDataMetadata;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005H&\u00a2\u0006\u0002\u0010\u0007J\n\u0010\b\u001a\u0004\u0018\u00010\u0003H&\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/store/SampleEventRepository;", "", "buildSampleEventStore", "Lcom/addonengine/addons/analytics/store/model/SampleDataMetadata;", "fromTime", "", "toTime", "(Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/store/model/SampleDataMetadata;", "getSampleDataMetadata", "analytics"})
public interface SampleEventRepository {
    @Nullable
    public SampleDataMetadata getSampleDataMetadata();

    @NotNull
    public SampleDataMetadata buildSampleEventStore(@Nullable Long var1, @Nullable Long var2);

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        public static /* synthetic */ SampleDataMetadata buildSampleEventStore$default(SampleEventRepository sampleEventRepository, Long l, Long l2, int n, Object object) {
            if (object != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: buildSampleEventStore");
            }
            if ((n & 1) != 0) {
                l = null;
            }
            if ((n & 2) != 0) {
                l2 = null;
            }
            return sampleEventRepository.buildSampleEventStore(l, l2);
        }
    }
}

