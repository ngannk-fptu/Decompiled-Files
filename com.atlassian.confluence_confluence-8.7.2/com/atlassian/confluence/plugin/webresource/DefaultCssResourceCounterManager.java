/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugin.webresource.CssResourceCounterManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.tenant.TenantRegistry;

public class DefaultCssResourceCounterManager
implements CssResourceCounterManager {
    private final BandanaManager bandanaManager;

    public DefaultCssResourceCounterManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Deprecated(forRemoval=true)
    public DefaultCssResourceCounterManager(BandanaManager bandanaManager, TenantRegistry ignored) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public void invalidateGlobalCssResourceCounter() {
        int old = this.getGlobalCssResourceCounter();
        this.setGlobalCssResourceCounter(old + 1);
    }

    @Override
    public void invalidateSpaceCssResourceCounter(String spaceKey) {
        int old = this.getSpaceCssResourceCounter(spaceKey);
        this.setSpaceCssResourceCounter(spaceKey, old + 1);
    }

    @Override
    public int getGlobalCssResourceCounter() {
        Integer globalCssResourceCounter = (Integer)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.counter", false);
        if (globalCssResourceCounter == null) {
            return 1;
        }
        return globalCssResourceCounter;
    }

    @Override
    public int getSpaceCssResourceCounter(String spaceKey) {
        Integer spaceCssResourceCounter = (Integer)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.counter", false);
        if (spaceCssResourceCounter == null) {
            return 1;
        }
        return spaceCssResourceCounter;
    }

    private void setGlobalCssResourceCounter(Integer globalCssResourceCounter) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.counter", (Object)globalCssResourceCounter);
    }

    private void setSpaceCssResourceCounter(String spaceKey, Integer spaceCssResourceCounter) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.counter", (Object)spaceCssResourceCounter);
    }
}

