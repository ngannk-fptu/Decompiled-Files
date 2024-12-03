/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ErrorDto;", "", "reason", "", "message", "(Ljava/lang/String;Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "getReason", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class ErrorDto {
    @NotNull
    private final String reason;
    @NotNull
    private final String message;

    public ErrorDto(@NotNull String reason, @NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)reason, (String)"reason");
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        this.reason = reason;
        this.message = message;
    }

    @NotNull
    public final String getReason() {
        return this.reason;
    }

    @NotNull
    public final String getMessage() {
        return this.message;
    }

    @NotNull
    public final String component1() {
        return this.reason;
    }

    @NotNull
    public final String component2() {
        return this.message;
    }

    @NotNull
    public final ErrorDto copy(@NotNull String reason, @NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)reason, (String)"reason");
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        return new ErrorDto(reason, message);
    }

    public static /* synthetic */ ErrorDto copy$default(ErrorDto errorDto, String string, String string2, int n, Object object) {
        if ((n & 1) != 0) {
            string = errorDto.reason;
        }
        if ((n & 2) != 0) {
            string2 = errorDto.message;
        }
        return errorDto.copy(string, string2);
    }

    @NotNull
    public String toString() {
        return "ErrorDto(reason=" + this.reason + ", message=" + this.message + ')';
    }

    public int hashCode() {
        int result = this.reason.hashCode();
        result = result * 31 + this.message.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ErrorDto)) {
            return false;
        }
        ErrorDto errorDto = (ErrorDto)other;
        if (!Intrinsics.areEqual((Object)this.reason, (Object)errorDto.reason)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.message, (Object)errorDto.message);
    }
}

