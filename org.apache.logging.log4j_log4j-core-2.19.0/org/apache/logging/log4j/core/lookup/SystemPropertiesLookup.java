/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name="sys", category="Lookup")
public class SystemPropertiesLookup
extends AbstractLookup {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final Marker LOOKUP = MarkerManager.getMarker((String)"LOOKUP");

    @Override
    public String lookup(LogEvent event, String key) {
        try {
            return System.getProperty(key);
        }
        catch (Exception ex) {
            LOGGER.warn(LOOKUP, "Error while getting system property [{}].", (Object)key, (Object)ex);
            return null;
        }
    }
}

