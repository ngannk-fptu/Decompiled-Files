/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.proxy;

import java.util.Set;

public interface TrustedProxyManager {
    public boolean isTrusted(String var1);

    public Set<String> getAddresses();

    public boolean addAddress(String var1);

    public void removeAddress(String var1);
}

