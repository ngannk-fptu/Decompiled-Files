/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlinx.coroutines.flow.Flow
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.springframework.data.repository.kotlin;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=1, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u001c\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0006\bg\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0003J\u0011\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00028\u0000H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0011\u0010\u000b\u001a\u00020\bH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u001f\u0010\u000b\u001a\u00020\b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00028\u00000\rH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ)\u0010\u000b\u001a\u00020\b\"\b\b\u0002\u0010\u000f*\u00028\u00002\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u000f0\u0011H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J\u001f\u0010\u0013\u001a\u00020\b2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00010\rH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ\u0019\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00028\u0001H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0019\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00028\u0001H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u0019\u001a\b\u0012\u0004\u0012\u00028\u00000\u0011H&J\u001c\u0010\u001a\u001a\b\u0012\u0004\u0012\u00028\u00000\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00010\rH&J\u001c\u0010\u001a\u001a\b\u0012\u0004\u0012\u00028\u00000\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00010\u0011H&J\u001b\u0010\u001b\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0016\u001a\u00028\u0001H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ#\u0010\u001c\u001a\u00028\u0000\"\b\b\u0002\u0010\u000f*\u00028\u00002\u0006\u0010\t\u001a\u0002H\u000fH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ&\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u000f0\u0011\"\b\b\u0002\u0010\u000f*\u00028\u00002\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000f0\rH&J&\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u000f0\u0011\"\b\b\u0002\u0010\u000f*\u00028\u00002\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u000f0\u0011H&\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001e"}, d2={"Lorg/springframework/data/repository/kotlin/CoroutineCrudRepository;", "T", "ID", "Lorg/springframework/data/repository/Repository;", "count", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "", "entity", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "entities", "", "(Ljava/lang/Iterable;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "S", "entityStream", "Lkotlinx/coroutines/flow/Flow;", "(Lkotlinx/coroutines/flow/Flow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllById", "ids", "deleteById", "id", "existsById", "", "findAll", "findAllById", "findById", "save", "saveAll", "spring-data-commons"})
public interface CoroutineCrudRepository<T, ID>
extends Repository<T, ID> {
    @Nullable
    public <S extends T> Object save(S var1, @NotNull Continuation<? super T> var2);

    @NotNull
    public <S extends T> Flow<S> saveAll(@NotNull Iterable<? extends S> var1);

    @NotNull
    public <S extends T> Flow<S> saveAll(@NotNull Flow<? extends S> var1);

    @Nullable
    public Object findById(ID var1, @NotNull Continuation<? super T> var2);

    @Nullable
    public Object existsById(ID var1, @NotNull Continuation<? super Boolean> var2);

    @NotNull
    public Flow<T> findAll();

    @NotNull
    public Flow<T> findAllById(@NotNull Iterable<? extends ID> var1);

    @NotNull
    public Flow<T> findAllById(@NotNull Flow<? extends ID> var1);

    @Nullable
    public Object count(@NotNull Continuation<? super Long> var1);

    @Nullable
    public Object deleteById(ID var1, @NotNull Continuation<? super Unit> var2);

    @Nullable
    public Object delete(T var1, @NotNull Continuation<? super Unit> var2);

    @Nullable
    public Object deleteAllById(@NotNull Iterable<? extends ID> var1, @NotNull Continuation<? super Unit> var2);

    @Nullable
    public Object deleteAll(@NotNull Iterable<? extends T> var1, @NotNull Continuation<? super Unit> var2);

    @Nullable
    public <S extends T> Object deleteAll(@NotNull Flow<? extends S> var1, @NotNull Continuation<? super Unit> var2);

    @Nullable
    public Object deleteAll(@NotNull Continuation<? super Unit> var1);
}

