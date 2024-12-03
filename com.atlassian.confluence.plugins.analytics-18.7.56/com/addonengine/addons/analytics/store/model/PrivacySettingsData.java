/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.model;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/model/PrivacySettingsData;", "", "enabled", "", "instanceSalt", "", "(ZLjava/lang/String;)V", "getEnabled", "()Z", "getInstanceSalt", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "analytics"})
public final class PrivacySettingsData {
    private final boolean enabled;
    @Nullable
    private final String instanceSalt;

    public PrivacySettingsData(boolean enabled, @Nullable String instanceSalt) {
        this.enabled = enabled;
        this.instanceSalt = instanceSalt;
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    @Nullable
    public final String getInstanceSalt() {
        return this.instanceSalt;
    }

    public final boolean component1() {
        return this.enabled;
    }

    @Nullable
    public final String component2() {
        return this.instanceSalt;
    }

    @NotNull
    public final PrivacySettingsData copy(boolean enabled, @Nullable String instanceSalt) {
        return new PrivacySettingsData(enabled, instanceSalt);
    }

    public static /* synthetic */ PrivacySettingsData copy$default(PrivacySettingsData privacySettingsData, boolean bl, String string, int n, Object object) {
        if ((n & 1) != 0) {
            bl = privacySettingsData.enabled;
        }
        if ((n & 2) != 0) {
            string = privacySettingsData.instanceSalt;
        }
        return privacySettingsData.copy(bl, string);
    }

    @NotNull
    public String toString() {
        return "PrivacySettingsData(enabled=" + this.enabled + ", instanceSalt=" + this.instanceSalt + ')';
    }

    public int hashCode() {
        int n = this.enabled ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        int result = n;
        result = result * 31 + (this.instanceSalt == null ? 0 : this.instanceSalt.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PrivacySettingsData)) {
            return false;
        }
        PrivacySettingsData privacySettingsData = (PrivacySettingsData)other;
        if (this.enabled != privacySettingsData.enabled) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.instanceSalt, (Object)privacySettingsData.instanceSalt);
    }
}

