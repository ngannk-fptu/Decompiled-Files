/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.io;

import com.sun.syndication.io.ModuleGenerator;
import com.sun.syndication.io.WireFeedGenerator;

public interface DelegatingModuleGenerator
extends ModuleGenerator {
    public void setFeedGenerator(WireFeedGenerator var1);
}

