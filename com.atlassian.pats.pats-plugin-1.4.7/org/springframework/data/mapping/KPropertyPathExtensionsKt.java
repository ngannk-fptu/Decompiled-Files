/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.reflect.KProperty
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.data.mapping;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mapping.KPropertyPathKt;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u0002\u00a8\u0006\u0003"}, d2={"toDotPath", "", "Lkotlin/reflect/KProperty;", "spring-data-commons"})
public final class KPropertyPathExtensionsKt {
    @NotNull
    public static final String toDotPath(@NotNull KProperty<?> $this$toDotPath) {
        Intrinsics.checkParameterIsNotNull($this$toDotPath, (String)"$this$toDotPath");
        return KPropertyPathKt.asString($this$toDotPath);
    }
}

