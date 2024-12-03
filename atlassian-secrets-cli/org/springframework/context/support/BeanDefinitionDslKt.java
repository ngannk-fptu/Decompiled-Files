/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.context.support;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.BeanDefinitionDsl;

@Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=2, d1={"\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005\u00a8\u0006\u0006"}, d2={"beans", "Lorg/springframework/context/support/BeanDefinitionDsl;", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "spring-context"})
public final class BeanDefinitionDslKt {
    @NotNull
    public static final BeanDefinitionDsl beans(@NotNull Function1<? super BeanDefinitionDsl, Unit> init) {
        Intrinsics.checkParameterIsNotNull(init, (String)"init");
        BeanDefinitionDsl beans2 = new BeanDefinitionDsl(null, 1, null);
        init.invoke((Object)beans2);
        return beans2;
    }
}

