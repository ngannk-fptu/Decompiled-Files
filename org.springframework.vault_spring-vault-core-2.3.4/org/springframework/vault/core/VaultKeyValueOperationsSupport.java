/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.core;

import java.util.List;
import org.springframework.lang.Nullable;

public interface VaultKeyValueOperationsSupport {
    @Nullable
    public List<String> list(String var1);

    @Nullable
    public Object get(String var1);

    public void delete(String var1);

    public KeyValueBackend getApiVersion();

    public static enum KeyValueBackend {
        KV_1,
        KV_2;


        public static KeyValueBackend unversioned() {
            return KV_1;
        }

        public static KeyValueBackend versioned() {
            return KV_2;
        }
    }
}

