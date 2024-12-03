/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package org.springframework.vault.core;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.vault.core.VaultWrappingOperations;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;

@Metadata(mv={1, 4, 2}, bv={1, 0, 3}, k=2, d1={"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a)\u0010\u0000\u001a\n\u0012\u0004\u0012\u0002H\u0002\u0018\u00010\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\b\u00a8\u0006\u0007"}, d2={"read", "Lorg/springframework/vault/support/VaultResponseSupport;", "T", "", "Lorg/springframework/vault/core/VaultWrappingOperations;", "token", "Lorg/springframework/vault/support/VaultToken;", "spring-vault-core"})
public final class VaultWrappingOperationsExtensionsKt {
    public static final /* synthetic */ <T> VaultResponseSupport<T> read(VaultWrappingOperations $this$read, VaultToken token) {
        int $i$f$read = 0;
        Intrinsics.checkNotNullParameter((Object)$this$read, (String)"$this$read");
        Intrinsics.checkNotNullParameter((Object)token, (String)"token");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return $this$read.read(token, Object.class);
    }
}

