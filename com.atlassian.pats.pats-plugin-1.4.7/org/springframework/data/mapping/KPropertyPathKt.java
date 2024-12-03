/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.reflect.KProperty
 *  kotlin.reflect.KProperty1
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.data.mapping;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KProperty;
import kotlin.reflect.KProperty1;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mapping.KPropertyPath;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000\u0016\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u00012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003H\u0000\u001a;\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00050\u0003\"\u0004\b\u0000\u0010\u0006\"\u0004\b\u0001\u0010\u0005*\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00060\u00032\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u0002H\u0006\u0012\u0004\u0012\u0002H\u00050\bH\u0086\u0002\u00a8\u0006\t"}, d2={"asString", "", "property", "Lkotlin/reflect/KProperty;", "div", "U", "T", "other", "Lkotlin/reflect/KProperty1;", "spring-data-commons"})
public final class KPropertyPathKt {
    @NotNull
    public static final String asString(@NotNull KProperty<?> property) {
        Intrinsics.checkParameterIsNotNull(property, (String)"property");
        KProperty<?> kProperty = property;
        return kProperty instanceof KPropertyPath ? KPropertyPathKt.asString(((KPropertyPath)property).getParent()) + '.' + ((KPropertyPath)property).getChild().getName() : property.getName();
    }

    @NotNull
    public static final <T, U> KProperty<U> div(@NotNull KProperty<? extends T> $this$div, @NotNull KProperty1<T, ? extends U> other) {
        Intrinsics.checkParameterIsNotNull($this$div, (String)"$this$div");
        Intrinsics.checkParameterIsNotNull(other, (String)"other");
        return new KPropertyPath<U, T>($this$div, other);
    }
}

