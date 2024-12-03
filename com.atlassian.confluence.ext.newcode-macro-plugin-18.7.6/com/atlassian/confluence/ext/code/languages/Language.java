/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

import java.util.Collection;

public interface Language {
    public String getFriendlyName();

    public String getName();

    public Collection<String> getAliases();

    public boolean isBuiltIn();

    public String getWebResource();
}

