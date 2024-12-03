/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u001a\u001f\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0086\u0002\u001a'\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0086\u0002\u001a/\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0086\u0002\u00a8\u0006\n"}, d2={"set", "", "Lorg/springframework/jdbc/core/namedparam/MapSqlParameterSource;", "paramName", "", "value", "", "sqlType", "", "typeName", "spring-jdbc"})
public final class MapSqlParameterSourceExtensionsKt {
    public static final void set(@NotNull MapSqlParameterSource $this$set, @NotNull String paramName, @Nullable Object value) {
        Intrinsics.checkParameterIsNotNull((Object)$this$set, (String)"$this$set");
        Intrinsics.checkParameterIsNotNull((Object)paramName, (String)"paramName");
        $this$set.addValue(paramName, value);
    }

    public static final void set(@NotNull MapSqlParameterSource $this$set, @NotNull String paramName, int sqlType, @Nullable Object value) {
        Intrinsics.checkParameterIsNotNull((Object)$this$set, (String)"$this$set");
        Intrinsics.checkParameterIsNotNull((Object)paramName, (String)"paramName");
        $this$set.addValue(paramName, value, sqlType);
    }

    public static final void set(@NotNull MapSqlParameterSource $this$set, @NotNull String paramName, int sqlType, @NotNull String typeName, @Nullable Object value) {
        Intrinsics.checkParameterIsNotNull((Object)$this$set, (String)"$this$set");
        Intrinsics.checkParameterIsNotNull((Object)paramName, (String)"paramName");
        Intrinsics.checkParameterIsNotNull((Object)typeName, (String)"typeName");
        $this$set.addValue(paramName, value, sqlType, typeName);
    }
}

