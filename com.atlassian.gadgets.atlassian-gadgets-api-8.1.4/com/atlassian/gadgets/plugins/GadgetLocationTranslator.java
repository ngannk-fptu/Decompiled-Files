/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.plugins;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import java.net.URI;

public interface GadgetLocationTranslator {
    public PluginGadgetSpec.Key translate(PluginGadgetSpec.Key var1);

    public URI translate(URI var1);
}

