/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.dm.PropScanResult;
import java.io.BufferedReader;

public interface SisyphusPropertyMatcher {
    public PropScanResult match(BufferedReader var1);
}

