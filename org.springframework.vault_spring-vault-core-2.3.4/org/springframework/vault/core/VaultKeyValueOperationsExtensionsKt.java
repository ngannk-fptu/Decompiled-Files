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
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.support.VaultResponseSupport;

@Metadata(mv={1, 4, 2}, bv={1, 0, 3}, k=2, d1={"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a)\u0010\u0000\u001a\n\u0012\u0004\u0012\u0002H\u0002\u0018\u00010\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\b\u00a8\u0006\u0007"}, d2={"get", "Lorg/springframework/vault/support/VaultResponseSupport;", "T", "", "Lorg/springframework/vault/core/VaultKeyValueOperations;", "path", "", "spring-vault-core"})
public final class VaultKeyValueOperationsExtensionsKt {
    public static final /* synthetic */ <T> VaultResponseSupport<T> get(VaultKeyValueOperations $this$get, String path) {
        int $i$f$get = 0;
        Intrinsics.checkNotNullParameter((Object)$this$get, (String)"$this$get");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return $this$get.get(path, Object.class);
    }
}

