/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPatternSource;

public interface ReloadableSisyphusPatternSource
extends SisyphusPatternSource {
    public void reload() throws Exception;
}

