/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.model.PeriodActivity;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J9\u0010\u000f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/service/model/ActivityByPeriod;", "", "views", "", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "creates", "updates", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "getCreates", "()Ljava/util/List;", "getUpdates", "getViews", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ActivityByPeriod {
    @NotNull
    private final List<PeriodActivity> views;
    @NotNull
    private final List<PeriodActivity> creates;
    @NotNull
    private final List<PeriodActivity> updates;

    public ActivityByPeriod(@NotNull List<PeriodActivity> views, @NotNull List<PeriodActivity> creates, @NotNull List<PeriodActivity> updates) {
        Intrinsics.checkNotNullParameter(views, (String)"views");
        Intrinsics.checkNotNullParameter(creates, (String)"creates");
        Intrinsics.checkNotNullParameter(updates, (String)"updates");
        this.views = views;
        this.creates = creates;
        this.updates = updates;
    }

    @NotNull
    public final List<PeriodActivity> getViews() {
        return this.views;
    }

    @NotNull
    public final List<PeriodActivity> getCreates() {
        return this.creates;
    }

    @NotNull
    public final List<PeriodActivity> getUpdates() {
        return this.updates;
    }

    @NotNull
    public final List<PeriodActivity> component1() {
        return this.views;
    }

    @NotNull
    public final List<PeriodActivity> component2() {
        return this.creates;
    }

    @NotNull
    public final List<PeriodActivity> component3() {
        return this.updates;
    }

    @NotNull
    public final ActivityByPeriod copy(@NotNull List<PeriodActivity> views, @NotNull List<PeriodActivity> creates, @NotNull List<PeriodActivity> updates) {
        Intrinsics.checkNotNullParameter(views, (String)"views");
        Intrinsics.checkNotNullParameter(creates, (String)"creates");
        Intrinsics.checkNotNullParameter(updates, (String)"updates");
        return new ActivityByPeriod(views, creates, updates);
    }

    public static /* synthetic */ ActivityByPeriod copy$default(ActivityByPeriod activityByPeriod, List list, List list2, List list3, int n, Object object) {
        if ((n & 1) != 0) {
            list = activityByPeriod.views;
        }
        if ((n & 2) != 0) {
            list2 = activityByPeriod.creates;
        }
        if ((n & 4) != 0) {
            list3 = activityByPeriod.updates;
        }
        return activityByPeriod.copy(list, list2, list3);
    }

    @NotNull
    public String toString() {
        return "ActivityByPeriod(views=" + this.views + ", creates=" + this.creates + ", updates=" + this.updates + ')';
    }

    public int hashCode() {
        int result = ((Object)this.views).hashCode();
        result = result * 31 + ((Object)this.creates).hashCode();
        result = result * 31 + ((Object)this.updates).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ActivityByPeriod)) {
            return false;
        }
        ActivityByPeriod activityByPeriod = (ActivityByPeriod)other;
        if (!Intrinsics.areEqual(this.views, activityByPeriod.views)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.creates, activityByPeriod.creates)) {
            return false;
        }
        return Intrinsics.areEqual(this.updates, activityByPeriod.updates);
    }
}

