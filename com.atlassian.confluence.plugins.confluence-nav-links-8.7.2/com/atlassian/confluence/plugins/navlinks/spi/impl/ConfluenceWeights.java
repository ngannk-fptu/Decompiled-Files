/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.navlink.spi.weights.ApplicationWeights
 */
package com.atlassian.confluence.plugins.navlinks.spi.impl;

import com.atlassian.plugins.navlink.spi.weights.ApplicationWeights;

public final class ConfluenceWeights
implements ApplicationWeights {
    public int getApplicationWeight() {
        return 200;
    }
}

