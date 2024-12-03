/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.ui;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.ui.Model;

@Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=2, d1={"\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0000\n\u0000\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\u0002\u00a8\u0006\u0007"}, d2={"set", "", "Lorg/springframework/ui/Model;", "attributeName", "", "attributeValue", "", "spring-context"})
public final class ModelExtensionsKt {
    public static final void set(@NotNull Model $receiver, @NotNull String attributeName, @NotNull Object attributeValue) {
        Intrinsics.checkParameterIsNotNull((Object)$receiver, (String)"$receiver");
        Intrinsics.checkParameterIsNotNull((Object)attributeName, (String)"attributeName");
        Intrinsics.checkParameterIsNotNull((Object)attributeValue, (String)"attributeValue");
        $receiver.addAttribute(attributeName, attributeValue);
    }
}

