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

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\n\b\u0001\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0005J\u000b\u0010\t\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J!\u0010\u000b\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\u0004\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/NewSpaceRestrictionDto;", "", "groupName", "", "userId", "(Ljava/lang/String;Ljava/lang/String;)V", "getGroupName", "()Ljava/lang/String;", "getUserId", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class NewSpaceRestrictionDto {
    @Nullable
    private final String groupName;
    @Nullable
    private final String userId;

    public NewSpaceRestrictionDto(@JsonProperty(value="groupName") @Nullable String groupName, @JsonProperty(value="userId") @Nullable String userId) {
        this.groupName = groupName;
        this.userId = userId;
    }

    @Nullable
    public final String getGroupName() {
        return this.groupName;
    }

    @Nullable
    public final String getUserId() {
        return this.userId;
    }

    @Nullable
    public final String component1() {
        return this.groupName;
    }

    @Nullable
    public final String component2() {
        return this.userId;
    }

    @NotNull
    public final NewSpaceRestrictionDto copy(@JsonProperty(value="groupName") @Nullable String groupName, @JsonProperty(value="userId") @Nullable String userId) {
        return new NewSpaceRestrictionDto(groupName, userId);
    }

    public static /* synthetic */ NewSpaceRestrictionDto copy$default(NewSpaceRestrictionDto newSpaceRestrictionDto, String string, String string2, int n, Object object) {
        if ((n & 1) != 0) {
            string = newSpaceRestrictionDto.groupName;
        }
        if ((n & 2) != 0) {
            string2 = newSpaceRestrictionDto.userId;
        }
        return newSpaceRestrictionDto.copy(string, string2);
    }

    @NotNull
    public String toString() {
        return "NewSpaceRestrictionDto(groupName=" + this.groupName + ", userId=" + this.userId + ')';
    }

    public int hashCode() {
        int result = this.groupName == null ? 0 : this.groupName.hashCode();
        result = result * 31 + (this.userId == null ? 0 : this.userId.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NewSpaceRestrictionDto)) {
            return false;
        }
        NewSpaceRestrictionDto newSpaceRestrictionDto = (NewSpaceRestrictionDto)other;
        if (!Intrinsics.areEqual((Object)this.groupName, (Object)newSpaceRestrictionDto.groupName)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.userId, (Object)newSpaceRestrictionDto.userId);
    }
}

