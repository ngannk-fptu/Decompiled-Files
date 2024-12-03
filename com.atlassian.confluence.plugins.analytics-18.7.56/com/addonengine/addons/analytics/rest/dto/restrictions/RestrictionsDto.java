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

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\u00020\u0002B\u0015\u0012\u000e\b\u0001\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004H\u00c6\u0003J\u001f\u0010\t\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\u000e\b\u0003\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/RestrictionsDto;", "T", "", "restrictions", "", "(Ljava/util/List;)V", "getRestrictions", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class RestrictionsDto<T> {
    @NotNull
    private final List<T> restrictions;

    public RestrictionsDto(@JsonProperty(value="restrictions") @NotNull List<? extends T> restrictions) {
        Intrinsics.checkNotNullParameter(restrictions, (String)"restrictions");
        this.restrictions = restrictions;
    }

    @NotNull
    public final List<T> getRestrictions() {
        return this.restrictions;
    }

    @NotNull
    public final List<T> component1() {
        return this.restrictions;
    }

    @NotNull
    public final RestrictionsDto<T> copy(@JsonProperty(value="restrictions") @NotNull List<? extends T> restrictions) {
        Intrinsics.checkNotNullParameter(restrictions, (String)"restrictions");
        return new RestrictionsDto<T>(restrictions);
    }

    public static /* synthetic */ RestrictionsDto copy$default(RestrictionsDto restrictionsDto, List list, int n, Object object) {
        if ((n & 1) != 0) {
            list = restrictionsDto.restrictions;
        }
        return restrictionsDto.copy(list);
    }

    @NotNull
    public String toString() {
        return "RestrictionsDto(restrictions=" + this.restrictions + ')';
    }

    public int hashCode() {
        return ((Object)this.restrictions).hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RestrictionsDto)) {
            return false;
        }
        RestrictionsDto restrictionsDto = (RestrictionsDto)other;
        return Intrinsics.areEqual(this.restrictions, restrictionsDto.restrictions);
    }
}

