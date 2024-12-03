/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.util.message;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.message.AbstractMessageManager;
import com.atlassian.confluence.util.message.Message;
import java.util.HashMap;
import java.util.Map;

public class BandanaMessageManager
extends AbstractMessageManager {
    public static final String PERSISTENCE_KEY = "confluence.message.manager";
    private BandanaManager bandanaManager;

    @Override
    protected Map<String, Message> retrieveEntries() {
        HashMap entries = (HashMap)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), PERSISTENCE_KEY);
        if (entries == null) {
            entries = new HashMap();
        }
        return entries;
    }

    @Override
    protected void saveEntries(Map<String, Message> messages) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), PERSISTENCE_KEY, messages);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
}

