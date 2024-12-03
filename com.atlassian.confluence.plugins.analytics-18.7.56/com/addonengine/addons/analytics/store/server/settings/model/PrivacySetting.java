/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.settings.model;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/model/PrivacySetting;", "", "enabled", "", "instanceSalt", "", "(ZLjava/lang/String;)V", "getEnabled", "()Z", "getInstanceSalt", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "analytics"})
public final class PrivacySetting {
    private final boolean enabled;
    @NotNull
    private final String instanceSalt;

    public PrivacySetting(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="instanceSalt") @NotNull String instanceSalt) {
        Intrinsics.checkNotNullParameter((Object)instanceSalt, (String)"instanceSalt");
        this.enabled = enabled;
        this.instanceSalt = instanceSalt;
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    @NotNull
    public final String getInstanceSalt() {
        return this.instanceSalt;
    }

    public final boolean component1() {
        return this.enabled;
    }

    @NotNull
    public final String component2() {
        return this.instanceSalt;
    }

    @NotNull
    public final PrivacySetting copy(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="instanceSalt") @NotNull String instanceSalt) {
        Intrinsics.checkNotNullParameter((Object)instanceSalt, (String)"instanceSalt");
        return new PrivacySetting(enabled, instanceSalt);
    }

    public static /* synthetic */ PrivacySetting copy$default(PrivacySetting privacySetting, boolean bl, String string, int n, Object object) {
        if ((n & 1) != 0) {
            bl = privacySetting.enabled;
        }
        if ((n & 2) != 0) {
            string = privacySetting.instanceSalt;
        }
        return privacySetting.copy(bl, string);
    }

    @NotNull
    public String toString() {
        return "PrivacySetting(enabled=" + this.enabled + ", instanceSalt=" + this.instanceSalt + ')';
    }

    public int hashCode() {
        int n = this.enabled ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        int result = n;
        result = result * 31 + this.instanceSalt.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PrivacySetting)) {
            return false;
        }
        PrivacySetting privacySetting = (PrivacySetting)other;
        if (this.enabled != privacySetting.enabled) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.instanceSalt, (Object)privacySetting.instanceSalt);
    }
}

