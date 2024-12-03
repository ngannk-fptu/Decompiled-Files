/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.count.MacroCount;
import com.atlassian.confluence.macro.count.MacroCounter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroCountStore {
    private static final Logger log = LoggerFactory.getLogger(MacroCountStore.class);
    private static final String CONTENT_PROPERTY_PREFIX = "macro-count.";

    public List<MacroCount> updateAndGetNewCounts(MacroCounter counter, ContentEntityObject entity, int entityVersion) {
        ArrayList<MacroCount> newCounts = new ArrayList<MacroCount>();
        for (MacroCount count : counter.getUsages()) {
            int previousCount = this.getCount(entity, count.getMacroType(), entityVersion - 1);
            int diff = count.getCount() - previousCount;
            if (diff > 0) {
                newCounts.add(new MacroCount(count.getMacroType(), diff));
            }
            this.persistCount(entity.getProperties(), count.getMacroType(), count.getCount(), entityVersion);
        }
        return newCounts;
    }

    private int getCount(ContentEntityObject entity, String macroId, int expectedVersion) {
        String storedValue = entity.getProperties().getStringProperty(CONTENT_PROPERTY_PREFIX + macroId);
        if (storedValue == null) {
            return 0;
        }
        MacroCountValue macroCountValue = new MacroCountValue(storedValue, entity.getIdAsString(), macroId);
        return macroCountValue.getVersion() == expectedVersion ? macroCountValue.getCount() : 0;
    }

    private void persistCount(ContentProperties properties, String macroId, int count, int entityVersion) {
        if (properties != null) {
            properties.setStringProperty(CONTENT_PROPERTY_PREFIX + macroId, new MacroCountValue(entityVersion, count).toString());
        }
    }

    static class MacroCountValue {
        private static final String VERSION_SEPARATOR = "-";
        private int version;
        private int count;

        public MacroCountValue(int version, int count) {
            this.version = version;
            this.count = count;
        }

        public MacroCountValue(String value, String contentId, String macroId) {
            try {
                String[] components = value.split(VERSION_SEPARATOR);
                this.version = Integer.parseInt(components[0]);
                this.count = Integer.parseInt(components[1]);
            }
            catch (Exception e) {
                log.debug("Unexpected format of macro count content property for content '{}' and macro '{}'", (Object)contentId, (Object)macroId);
            }
        }

        public int getVersion() {
            return this.version;
        }

        public int getCount() {
            return this.count;
        }

        public String toString() {
            return this.version + VERSION_SEPARATOR + this.count;
        }
    }
}

