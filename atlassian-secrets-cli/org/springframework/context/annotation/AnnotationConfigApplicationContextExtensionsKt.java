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
package org.springframework.context.annotation;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=2, d1={"\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005\u00a8\u0006\u0006"}, d2={"AnnotationConfigApplicationContext", "Lorg/springframework/context/annotation/AnnotationConfigApplicationContext;", "configure", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "spring-context"})
public final class AnnotationConfigApplicationContextExtensionsKt {
    @NotNull
    public static final AnnotationConfigApplicationContext AnnotationConfigApplicationContext(@NotNull Function1<? super AnnotationConfigApplicationContext, Unit> configure) {
        Intrinsics.checkParameterIsNotNull(configure, (String)"configure");
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        configure.invoke((Object)annotationConfigApplicationContext);
        return annotationConfigApplicationContext;
    }
}

