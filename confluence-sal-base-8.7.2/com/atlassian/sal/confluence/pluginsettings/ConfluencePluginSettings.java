/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluencePluginSettings
implements PluginSettings {
    private static final Logger log = LoggerFactory.getLogger(ConfluencePluginSettings.class);
    private final BandanaManager bandanaManager;
    private final ConfluenceBandanaContext ctx;

    public ConfluencePluginSettings(BandanaManager bandanaManager, ConfluenceBandanaContext ctx) {
        this.bandanaManager = bandanaManager;
        this.ctx = ctx;
    }

    public Object put(String key, Object val) {
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        if (val == null) {
            Object removed = this.bandanaManager.getValue((BandanaContext)this.ctx, key);
            this.bandanaManager.removeValue((BandanaContext)this.ctx, key);
            return removed;
        }
        Validate.isTrue((key.length() <= 100 ? 1 : 0) != 0, (String)"Key can not be more than 100 characters", (Object[])new Object[0]);
        if (val instanceof Properties || val instanceof List || val instanceof String || val instanceof Map) {
            Object removed = this.bandanaManager.getValue((BandanaContext)this.ctx, key);
            this.bandanaManager.setValue((BandanaContext)this.ctx, key, val);
            return removed;
        }
        throw new IllegalArgumentException("Property type: " + val.getClass() + " not supported");
    }

    public Object get(String key) {
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        if (key.length() > 100) {
            log.debug("Key was greater than 100 characters. Returning null.");
            return null;
        }
        return this.bandanaManager.getValue((BandanaContext)this.ctx, key);
    }

    public Object remove(String key) {
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        if (key.length() > 100) {
            log.debug("Key was greater than 100 characters. Returning null.");
            return null;
        }
        return this.put(key, null);
    }
}

