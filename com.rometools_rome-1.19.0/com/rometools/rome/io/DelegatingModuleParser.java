/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io;

import com.rometools.rome.io.ModuleParser;
import com.rometools.rome.io.WireFeedParser;

public interface DelegatingModuleParser
extends ModuleParser {
    public void setFeedParser(WireFeedParser var1);
}

