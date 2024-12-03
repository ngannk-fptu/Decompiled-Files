/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.io;

import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.WireFeedParser;

public interface DelegatingModuleParser
extends ModuleParser {
    public void setFeedParser(WireFeedParser var1);
}

