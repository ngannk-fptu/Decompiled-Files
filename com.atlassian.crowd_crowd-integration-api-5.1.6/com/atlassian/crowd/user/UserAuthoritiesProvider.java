/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.user;

import java.util.List;

public interface UserAuthoritiesProvider {
    public List<String> getAuthorityNames(String var1);
}

