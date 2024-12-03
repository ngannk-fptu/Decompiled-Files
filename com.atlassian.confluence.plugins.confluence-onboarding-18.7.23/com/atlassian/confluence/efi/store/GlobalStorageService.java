/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.efi.store;

import java.util.Set;

public interface GlobalStorageService {
    public String get(String var1);

    public boolean set(String var1, String var2);

    public void remove(String var1);

    public String getNpsEnabledSetting();

    public Set<String> getSet(String var1);

    public void set(String var1, Set<String> var2);
}

