/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io;

import com.rometools.rome.io.ModuleGenerator;
import com.rometools.rome.io.WireFeedGenerator;

public interface DelegatingModuleGenerator
extends ModuleGenerator {
    public void setFeedGenerator(WireFeedGenerator var1);
}

