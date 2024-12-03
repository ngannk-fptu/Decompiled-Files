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
package com.addonengine.addons.analytics.rest.dto.restrictions;

import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u000e\b\u0001\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0006H\u00c6\u0003J#\u0010\u000e\u001a\u00020\u00002\u000e\b\u0003\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0004H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/UserDetailsRequestDto;", "", "accountIds", "", "", "ignoreIncreasedPrivacyMode", "", "(Ljava/util/Set;Z)V", "getAccountIds", "()Ljava/util/Set;", "getIgnoreIncreasedPrivacyMode", "()Z", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "analytics"})
public final class UserDetailsRequestDto {
    @NotNull
    private final Set<String> accountIds;
    private final boolean ignoreIncreasedPrivacyMode;

    public UserDetailsRequestDto(@JsonProperty(value="accountIds") @NotNull Set<String> accountIds, @JsonProperty(value="ignoreIncreasedPrivacyMode") boolean ignoreIncreasedPrivacyMode) {
        Intrinsics.checkNotNullParameter(accountIds, (String)"accountIds");
        this.accountIds = accountIds;
        this.ignoreIncreasedPrivacyMode = ignoreIncreasedPrivacyMode;
    }

    @NotNull
    public final Set<String> getAccountIds() {
        return this.accountIds;
    }

    public final boolean getIgnoreIncreasedPrivacyMode() {
        return this.ignoreIncreasedPrivacyMode;
    }

    @NotNull
    public final Set<String> component1() {
        return this.accountIds;
    }

    public final boolean component2() {
        return this.ignoreIncreasedPrivacyMode;
    }

    @NotNull
    public final UserDetailsRequestDto copy(@JsonProperty(value="accountIds") @NotNull Set<String> accountIds, @JsonProperty(value="ignoreIncreasedPrivacyMode") boolean ignoreIncreasedPrivacyMode) {
        Intrinsics.checkNotNullParameter(accountIds, (String)"accountIds");
        return new UserDetailsRequestDto(accountIds, ignoreIncreasedPrivacyMode);
    }

    public static /* synthetic */ UserDetailsRequestDto copy$default(UserDetailsRequestDto userDetailsRequestDto, Set set2, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            set2 = userDetailsRequestDto.accountIds;
        }
        if ((n & 2) != 0) {
            bl = userDetailsRequestDto.ignoreIncreasedPrivacyMode;
        }
        return userDetailsRequestDto.copy(set2, bl);
    }

    @NotNull
    public String toString() {
        return "UserDetailsRequestDto(accountIds=" + this.accountIds + ", ignoreIncreasedPrivacyMode=" + this.ignoreIncreasedPrivacyMode + ')';
    }

    public int hashCode() {
        int result = ((Object)this.accountIds).hashCode();
        int n = this.ignoreIncreasedPrivacyMode ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserDetailsRequestDto)) {
            return false;
        }
        UserDetailsRequestDto userDetailsRequestDto = (UserDetailsRequestDto)other;
        if (!Intrinsics.areEqual(this.accountIds, userDetailsRequestDto.accountIds)) {
            return false;
        }
        return this.ignoreIncreasedPrivacyMode == userDetailsRequestDto.ignoreIncreasedPrivacyMode;
    }
}

