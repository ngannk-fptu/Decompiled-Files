/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPattern;

public interface SisyphusPatternSource
extends Iterable<SisyphusPattern> {
    public SisyphusPattern getPattern(String var1);

    public int size();
}

