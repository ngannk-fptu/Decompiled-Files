/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlinx.coroutines.flow.Flow
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.data.repository.kotlin;

import kotlin.Metadata;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.kotlin.CoroutineCrudRepository;

@NoRepositoryBean
@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=1, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0003J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&\u00a8\u0006\b"}, d2={"Lorg/springframework/data/repository/kotlin/CoroutineSortingRepository;", "T", "ID", "Lorg/springframework/data/repository/kotlin/CoroutineCrudRepository;", "findAll", "Lkotlinx/coroutines/flow/Flow;", "sort", "Lorg/springframework/data/domain/Sort;", "spring-data-commons"})
public interface CoroutineSortingRepository<T, ID>
extends CoroutineCrudRepository<T, ID> {
    @NotNull
    public Flow<T> findAll(@NotNull Sort var1);
}

