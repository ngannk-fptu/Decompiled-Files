/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.springframework.data.repository;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.CrudRepository;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000\f\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a1\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001\"\u0004\b\u0001\u0010\u0002*\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0004\u001a\u0002H\u0002\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\u0006"}, d2={"findByIdOrNull", "T", "ID", "Lorg/springframework/data/repository/CrudRepository;", "id", "(Lorg/springframework/data/repository/CrudRepository;Ljava/lang/Object;)Ljava/lang/Object;", "spring-data-commons"})
public final class CrudRepositoryExtensionsKt {
    @Nullable
    public static final <T, ID> T findByIdOrNull(@NotNull CrudRepository<T, ID> $this$findByIdOrNull, ID id) {
        Intrinsics.checkParameterIsNotNull($this$findByIdOrNull, (String)"$this$findByIdOrNull");
        return $this$findByIdOrNull.findById(id).orElse(null);
    }
}

