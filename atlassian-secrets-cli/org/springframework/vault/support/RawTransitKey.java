/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Map;

public interface RawTransitKey {
    public Map<String, String> getKeys();

    public String getName();
}

