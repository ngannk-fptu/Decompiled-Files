/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KProperty
 *  kotlin.reflect.KProperty$Getter
 *  kotlin.reflect.KProperty1
 *  kotlin.reflect.KType
 *  kotlin.reflect.KTypeParameter
 *  kotlin.reflect.KVisibility
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.springframework.data.mapping;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KParameter;
import kotlin.reflect.KProperty;
import kotlin.reflect.KProperty1;
import kotlin.reflect.KType;
import kotlin.reflect.KTypeParameter;
import kotlin.reflect.KVisibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=1, d1={"\u0000p\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u001b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B)\u0012\u000e\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00018\u00010\u0003\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00000\u0006\u00a2\u0006\u0002\u0010\u0007J&\u0010/\u001a\u00028\u00002\u0016\u00100\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010201\"\u0004\u0018\u000102H\u0096\u0001\u00a2\u0006\u0002\u00103J$\u00104\u001a\u00028\u00002\u0014\u00100\u001a\u0010\u0012\u0004\u0012\u00020 \u0012\u0006\u0012\u0004\u0018\u00010205H\u0096\u0001\u00a2\u0006\u0002\u00106R\u0018\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u001d\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00000\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0018\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u0010X\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0013\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0015R\u0014\u0010\u0018\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0015R\u0014\u0010\u0019\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u0015R\u0014\u0010\u001a\u001a\u00020\u00148\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b\u001a\u0010\u0015R\u0012\u0010\u001b\u001a\u00020\u001cX\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0018\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\tX\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b!\u0010\fR\u0019\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0012\u0010$\u001a\u00020%X\u0096\u0005\u00a2\u0006\u0006\u001a\u0004\b&\u0010'R\u001a\u0010(\u001a\b\u0012\u0004\u0012\u00020)0\t8\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b*\u0010\fR\u0016\u0010+\u001a\u0004\u0018\u00010,8\u0016X\u0097\u0005\u00a2\u0006\u0006\u001a\u0004\b-\u0010.\u00a8\u00067"}, d2={"Lorg/springframework/data/mapping/KPropertyPath;", "T", "U", "Lkotlin/reflect/KProperty;", "parent", "child", "Lkotlin/reflect/KProperty1;", "(Lkotlin/reflect/KProperty;Lkotlin/reflect/KProperty1;)V", "annotations", "", "", "getAnnotations", "()Ljava/util/List;", "getChild", "()Lkotlin/reflect/KProperty1;", "getter", "Lkotlin/reflect/KProperty$Getter;", "getGetter", "()Lkotlin/reflect/KProperty$Getter;", "isAbstract", "", "()Z", "isConst", "isFinal", "isLateinit", "isOpen", "isSuspend", "name", "", "getName", "()Ljava/lang/String;", "parameters", "Lkotlin/reflect/KParameter;", "getParameters", "getParent", "()Lkotlin/reflect/KProperty;", "returnType", "Lkotlin/reflect/KType;", "getReturnType", "()Lkotlin/reflect/KType;", "typeParameters", "Lkotlin/reflect/KTypeParameter;", "getTypeParameters", "visibility", "Lkotlin/reflect/KVisibility;", "getVisibility", "()Lkotlin/reflect/KVisibility;", "call", "args", "", "", "([Ljava/lang/Object;)Ljava/lang/Object;", "callBy", "", "(Ljava/util/Map;)Ljava/lang/Object;", "spring-data-commons"})
final class KPropertyPath<T, U>
implements KProperty<T> {
    @NotNull
    private final KProperty<U> parent;
    @NotNull
    private final KProperty1<U, T> child;

    @NotNull
    public final KProperty<U> getParent() {
        return this.parent;
    }

    @NotNull
    public final KProperty1<U, T> getChild() {
        return this.child;
    }

    public KPropertyPath(@NotNull KProperty<? extends U> parent, @NotNull KProperty1<U, ? extends T> child) {
        Intrinsics.checkParameterIsNotNull(parent, (String)"parent");
        Intrinsics.checkParameterIsNotNull(child, (String)"child");
        this.parent = parent;
        this.child = child;
    }

    @NotNull
    public List<Annotation> getAnnotations() {
        return this.child.getAnnotations();
    }

    @NotNull
    public KProperty.Getter<T> getGetter() {
        return (KProperty.Getter)this.child.getGetter();
    }

    public boolean isAbstract() {
        return this.child.isAbstract();
    }

    public boolean isConst() {
        return this.child.isConst();
    }

    public boolean isFinal() {
        return this.child.isFinal();
    }

    public boolean isLateinit() {
        return this.child.isLateinit();
    }

    public boolean isOpen() {
        return this.child.isOpen();
    }

    public boolean isSuspend() {
        return this.child.isSuspend();
    }

    @NotNull
    public String getName() {
        return this.child.getName();
    }

    @NotNull
    public List<KParameter> getParameters() {
        return this.child.getParameters();
    }

    @NotNull
    public KType getReturnType() {
        return this.child.getReturnType();
    }

    @NotNull
    public List<KTypeParameter> getTypeParameters() {
        return this.child.getTypeParameters();
    }

    @Nullable
    public KVisibility getVisibility() {
        return this.child.getVisibility();
    }

    public T call(Object ... args) {
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        return (T)this.child.call(args);
    }

    public T callBy(@NotNull Map<KParameter, ? extends Object> args) {
        Intrinsics.checkParameterIsNotNull(args, (String)"args");
        return (T)this.child.callBy(args);
    }
}

